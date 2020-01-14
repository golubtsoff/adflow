package dao.impl;

import dao.AbstractDao;
import dao.DbAssistant;
import entity.statistics.Group;
import entity.statistics.Request;
import entity.users.partner.Platform;
import rest.statistics.dto.GroupByElementsStatistics;
import rest.statistics.dto.GroupByPeriodsStatistics;
import rest.statistics.dto.TotalStatistics;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestDao extends AbstractDao<Request> {

    public RequestDao(){
        super(Request.class);
    }

    public List<Tuple> getDataForCampaignApproval(Platform platform, long sessionId){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.with(LocalTime.MIN);
        LocalDateTime to = now.with(LocalTime.MAX);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        String sql =
        "SELECT\n" +
                "  c.ID                                                                           id,\n" +
                "  c.DAILY_BUDGET                                                                 db,\n" +
                "  c.CPM_RATE                                                                     campaign_cpm,\n" +
                "  COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) / 1000                                   crt,\n" +
                "  c.ACTION                                                                       action,\n" +
                "  c.STATUS                                                                       status,\n" +
                "  (select count(r2.id) > 0 from requests r2 where\n" +
                "    c.ID = r2.CAMPAIGN_ID\n" +
                "    AND r2.SESSION_ID = " + sessionId + " \n" +
                "    AND r2.CREATION_TIME BETWEEN '" + from.format(formatter) + "' \n" +
                "    AND '" + to.format(formatter) + "') rbbu,\n" +
                "  2 - (select count(r2.id) > 0 from requests r2 where\n" +
                "    c.ID = r2.CAMPAIGN_ID\n" +
                "    AND r2.SESSION_ID = " + sessionId + " \n" +
                "    AND r2.CREATION_TIME BETWEEN '" + from.format(formatter) + "' \n" +
                "    AND '" + to.format(formatter) + "') \n" +
                "- COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) / c.DAILY_BUDGET / 1000 k,\n" +
                "  c.DAILY_BUDGET < ((COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) + c.CPM_RATE) / 1000) db_is_over,\n" +
                "  cu.BALANCE < (c.CPM_RATE / 1000)                                               balance_is_over,\n" +
                "  p.PICTURE_FORMAT_ID                                                            pid,\n" +
                "  p.FILENAME                                                                     fname,\n" +
                "  pl.CPM_RATE                                                                    platform_cpm,\n" +
                "  cu.BALANCE                                                                     balance\n" +
                "FROM CAMPAIGNS c\n" +
                "LEFT JOIN (select *, row_number() over (partition by r3.SESSION_ID, r3.CAMPAIGN_ID ORDER BY id) rn " +
                "from requests r3) r\n" +
                "  ON c.ID = r.CAMPAIGN_ID\n" +
                "    AND r.CREATION_TIME BETWEEN '" + from.format(formatter) + "'\n" +
                "    AND '" + to.format(formatter) + "'\n" +
                "    AND r.rn = 1\n" +
                "LEFT JOIN PICTURES p\n" +
                "  ON c.ID = p.CAMPAIGN_ID\n" +
                "LEFT JOIN PLATFORMS pl\n" +
                "  ON pl.ID = " + platform.getId() + " \n" +
                "LEFT JOIN CUSTOMERS cu\n" +
                "  ON cu.ID = c.CUSTOMER_ID\n" +
                "WHERE c.ACTION = 'RUN'\n" +
                "     AND p.PICTURE_FORMAT_ID = pl.PICTURE_FORMAT_ID \n" +
                "     AND c.CPM_RATE >= pl.CPM_RATE\n" +
                "GROUP BY c.ID\n" +
                "ORDER BY k DESC\n" +
                "LIMIT 1";
        TypedQuery<Tuple> query = DbAssistant.getSessionFactory().getCurrentSession()
                .createNativeQuery(sql, Tuple.class);
        return query.getResultList();
    }

    public List<GroupByElementsStatistics> getGroupByCampaignsStatistics(long userId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql =
            "select r.CAMPAIGN_ID elementId,\n " +
            "       ca.title title,\n" +
            "       count(r.CAMPAIGN_ID) displaysCount,\n " +
            "       count(r.CLICK_ON) clickCount,\n " +
            "       coalesce(sum(r.CAMPAIGN_CPM_RATE)/1000, 0) cost,\n " +
            "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n " +
            "from requests r, campaigns ca, customers cu, users u\n " +
            "where r.CAMPAIGN_ID = ca.id\n " +
            "  and u.user_id = " + userId + " \n " +
            "  and cu.user_id = u.user_id\n " +
            "  and ca.customer_id = cu.id\n " +
            "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n " +
            "  and '" + to.format(formatter) + "' \n " +
            "group by elementId\n " +
            "order by elementId";

        return getGroupByElementsStatistics(sql);
    }

    private List<GroupByElementsStatistics> getGroupByElementsStatistics(String sql){
        TypedQuery<Tuple> query = DbAssistant.getSessionFactory().getCurrentSession()
                .createNativeQuery(sql, Tuple.class);
        List<Tuple> tuples = query.getResultList();
        List<GroupByElementsStatistics> result = new ArrayList<>();
        for (Tuple tuple : tuples){
            result.add(new GroupByElementsStatistics(
                    tuple.get("elementId", BigInteger.class).longValue(),
                    tuple.get("title", String.class),
                    tuple.get("displaysCount", BigInteger.class).intValue(),
                    tuple.get("clickCount", BigInteger.class).intValue(),
                    tuple.get("cost", BigDecimal.class).setScale(2, RoundingMode.HALF_DOWN),
                    tuple.get("actualShowTime", BigDecimal.class).intValue()
            ));
        }
        return result;
    }

    public TotalStatistics getTotalAllCampaignsStatistics(long userId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql =
            "select \n" +
            "   count(r.CAMPAIGN_ID) displaysCount,\n" +
            "   count(r.CLICK_ON) clickCount,\n" +
            "   coalesce(sum(r.CAMPAIGN_CPM_RATE)/1000, 0) cost,\n" +
            "   coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n" +
            "from requests r, campaigns ca, customers cu, users u\n" +
            "where r.CAMPAIGN_ID = ca.id\n" +
            "  and u.user_id = " + userId + " \n" +
            "  and cu.user_id = u.user_id\n" +
            "  and ca.customer_id = cu.id\n" +
            "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
            "    and '" + to.format(formatter) + "' \n";

        return getTotalStatistics(sql);
    }

    private TotalStatistics getTotalStatistics(String sql){
        TypedQuery<Tuple> query = DbAssistant.getSessionFactory().getCurrentSession()
                .createNativeQuery(sql, Tuple.class);
        Tuple tuple = query.getSingleResult();
        return new TotalStatistics(
                tuple.get("displaysCount", BigInteger.class).intValue(),
                tuple.get("clickCount", BigInteger.class).intValue(),
                tuple.get("cost", BigDecimal.class).setScale(2, RoundingMode.HALF_DOWN),
                tuple.get("actualShowTime", BigDecimal.class).intValue()
        );
    }

    public List<GroupByPeriodsStatistics> getGroupByPeriodsCampaignStatistics(
            long userId, long campaignId, LocalDate from, LocalDate to, Group group){
        String middlePartSql = getMiddlePartSql(group);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql =
            "select count(r.CAMPAIGN_ID) displaysCount,\n" +
            "       count(r.CLICK_ON) clickCount,\n" +
            "       coalesce(sum(r.CAMPAIGN_CPM_RATE)/1000, 0) cost,\n" +
            "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime,\n" +
            middlePartSql + "\n" +
            "from requests r, campaigns ca, customers cu, users u\n" +
            "where u.user_id = " + userId + " \n" +
            "    and cu.user_id = u.user_id\n" +
            "    and ca.customer_id = cu.id\n" +
            "    and ca.id = r.CAMPAIGN_ID\n" +
            "    and r.CAMPAIGN_ID = " + campaignId + " \n" +
            "    and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
            "    and '" + to.format(formatter) + "' \n" +
            " group by period ";

        return getGroupByPeriodsStatistics(sql);
    }

    private List<GroupByPeriodsStatistics> getGroupByPeriodsStatistics(String sql){
        TypedQuery<Tuple> query = DbAssistant.getSessionFactory().getCurrentSession()
                .createNativeQuery(sql, Tuple.class);
        List<Tuple> tuples = query.getResultList();
        List<GroupByPeriodsStatistics> result = new ArrayList<>();
        for (Tuple tuple : tuples){
            result.add(new GroupByPeriodsStatistics(
                    tuple.get("period", String.class),
                    tuple.get("displaysCount", BigInteger.class).intValue(),
                    tuple.get("clickCount", BigInteger.class).intValue(),
                    tuple.get("cost", BigDecimal.class).setScale(2, RoundingMode.HALF_DOWN),
                    tuple.get("actualShowTime", BigDecimal.class).intValue()
            ));
        }
        return result;
    }

    private String getMiddlePartSql(Group group){
        switch (group){
            case DATE:
                return " date_format(r.CREATION_TIME, '%Y.%m.%d') period \n";
            case WEEK:
                return " concat_ws(' - ',\n" +
                        "       date_format(\n" +
                        "           subdate(r.CREATION_TIME, interval weekday(r.CREATION_TIME) day),\n" +
                        "           '%Y.%m.%d'\n" +
                        "       ),\n" +
                        "       date_format(\n" +
                        "           adddate(r.CREATION_TIME, interval 6 - weekday(r.CREATION_TIME) day),\n" +
                        "           '%Y.%m.%d'\n" +
                        "       )) period\n";
            case MONTH:
                return " date_format(r.CREATION_TIME, '%Y.%m') period \n";
            case YEAR:
                return " date_format(r.CREATION_TIME, '%Y') period \n";
        }
        return null;
    }

    public TotalStatistics getTotalCampaignStatistics(
            long userId, long campaignId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "select count(r.CAMPAIGN_ID) displaysCount,\n" +
                "       count(r.CLICK_ON) clickCount,\n" +
                "       coalesce(sum(r.CAMPAIGN_CPM_RATE)/1000, 0) cost,\n" +
                "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n" +
                "from requests r, campaigns ca, customers cu, users u\n" +
                "where r.CAMPAIGN_ID = " + campaignId + " \n" +
                "  and r.CAMPAIGN_ID = ca.id\n" +
                "  and u.user_id = " + userId + " \n" +
                "  and cu.user_id = u.user_id\n" +
                "  and ca.customer_id = cu.id\n" +
                "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
                "    and '" + to.format(formatter) + "' \n";

        return getTotalStatistics(sql);
    }

    public List<GroupByElementsStatistics> getGroupByPlatformsStatistics(long userId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "select pl.id elementId,\n" +
                "       pl.title title,\n" +
                "       count(r.CAMPAIGN_ID) displaysCount,\n" +
                "       count(r.CLICK_ON) clickCount,\n" +
                "       coalesce(sum(r.PLATFORM_CPM_RATE)/1000, 0) cost,\n" +
                "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n" +
                "from requests r, platforms pl, partners pa, users u, sessions s\n" +
                "where u.user_id = " + userId + " \n" +
                "  and pa.user_id = u.user_id\n" +
                "  and pl.partner_id = pa.id\n" +
                "  and s.PLATFORM_ID = pl.id\n" +
                "  and r.SESSION_ID = s.ID\n" +
                "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
                "    and '" + to.format(formatter) + "' \n" +
                "group by elementId\n" +
                "order by elementId";

        return getGroupByElementsStatistics(sql);
    }

    public TotalStatistics getTotalAllPlatformsStatistics(long userId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "select count(r.CAMPAIGN_ID) displaysCount,\n" +
                "       count(r.CLICK_ON) clickCount,\n" +
                "       coalesce(sum(r.PLATFORM_CPM_RATE)/1000, 0) cost,\n" +
                "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n" +
                "from requests r, platforms pl, partners pa, users u, sessions s\n" +
                "where u.user_id = " + userId + " \n" +
                "  and pa.user_id = u.user_id\n" +
                "  and pl.partner_id = pa.id\n" +
                "  and s.PLATFORM_ID = pl.id\n" +
                "  and r.SESSION_ID = s.ID\n" +
                "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
                "    and '" + to.format(formatter) + "' \n";

        return getTotalStatistics(sql);
    }

    public List<GroupByPeriodsStatistics> getGroupByPeriodsPlatformStatistics(
            long userId, long platformId, LocalDate from, LocalDate to, Group group){
        String middlePartSql = getMiddlePartSql(group);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "select count(r.CAMPAIGN_ID) displaysCount,\n" +
                "       count(r.CLICK_ON) clickCount,\n" +
                "       coalesce(sum(r.PLATFORM_CPM_RATE)/1000, 0) cost,\n" +
                "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime,\n" +
                middlePartSql + "\n" +
                "from requests r, platforms pl, partners pa, users u, sessions s\n" +
                "where u.user_id = " + userId + " \n" +
                "  and pa.user_id = u.user_id\n" +
                "  and pl.partner_id = pa.id\n" +
                "  and pl.id = " + platformId + " \n" +
                "  and s.PLATFORM_ID = pl.id\n" +
                "  and r.SESSION_ID = s.ID\n" +
                "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
                "    and '" + to.format(formatter) + "' \n" +
                "group by period";

        return getGroupByPeriodsStatistics(sql);
    }

    public TotalStatistics getTotalPlatformStatistics(
            long userId, long platformId, LocalDate from, LocalDate to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "select count(r.CAMPAIGN_ID) displaysCount,\n" +
                "       count(r.CLICK_ON) clickCount,\n" +
                "       coalesce(sum(r.PLATFORM_CPM_RATE)/1000, 0) cost,\n" +
                "       coalesce(sum(r.ACTUAL_SHOW_TIME), 0) actualShowTime\n" +
                "from requests r, platforms pl, partners pa, users u, sessions s\n" +
                "where u.user_id = " + userId + " \n" +
                "  and pa.user_id = u.user_id\n" +
                "  and pl.partner_id = pa.id\n" +
                "  and pl.id = " + platformId + " \n" +
                "  and s.PLATFORM_ID = pl.id\n" +
                "  and r.SESSION_ID = s.ID\n" +
                "  and DATE(r.CREATION_TIME) between '" + from.format(formatter) + "' \n" +
                "    and '" + to.format(formatter) + "' \n";

        return getTotalStatistics(sql);
    }
}
