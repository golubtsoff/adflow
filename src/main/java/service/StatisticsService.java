package service;

import dao.DaoFactory;
import dao.DbAssistant;
import dao.impl.RequestDao;
import entity.statistics.Group;
import exception.BadRequestException;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.statistics.dto.*;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {

    public static ShortStatisticsDto getShortCampaignStatistics(Long userId, String from, String to)
            throws BadRequestException, DbException {
        Map<String, LocalDate> dates = convertToLocalDate(from, to);
        Transaction transaction = DbAssistant.getTransaction();
        try {
            RequestDao requestDao = DaoFactory.getRequestDao();
            List<GroupByElementsStatistics> groupByElementsStatisticsList =
                requestDao.getGroupByCampaignsStatistics(userId, dates.get("from"), dates.get("to"));
            TotalStatistics totalStatistics = requestDao
                    .getTotalAllCampaignsStatistics(userId,  dates.get("from"), dates.get("to"));
            return new ShortStatisticsDto(
                    dates.get("from"),
                    dates.get("to"),
                    totalStatistics,
                    groupByElementsStatisticsList
            );
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Map<String, LocalDate> convertToLocalDate(String from, String to) throws BadRequestException {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        if (fromDate.isAfter(toDate)){
            throw new BadRequestException("Date 'from' is after 'to'");
        }
        Map<String, LocalDate> dates = new HashMap<>();
        dates.put("from", fromDate);
        dates.put("to", toDate);
        return dates;
    }

    public static DetailStatisticsDto getDetailCampaignStatistics(
            Long userId,
            Long campaignId,
            String from,
            String to,
            String group)
            throws DbException, BadRequestException {
        Map<String, LocalDate> dates = convertToLocalDate(from, to);
        Transaction transaction = DbAssistant.getTransaction();
        try {
            RequestDao requestDao = DaoFactory.getRequestDao();
            List<GroupByPeriodsStatistics> groupByPeriodsStatisticsList =
                    requestDao.getGroupByPeriodsCampaignStatistics(
                            userId, campaignId, dates.get("from"), dates.get("to"), convertStringToGroup(group));
            TotalStatistics totalStatistics = requestDao
                    .getTotalCampaignStatistics(userId, campaignId, dates.get("from"), dates.get("to"));
            return new DetailStatisticsDto(
                    dates.get("from"),
                    dates.get("to"),
                    totalStatistics,
                    groupByPeriodsStatisticsList
            );
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Group convertStringToGroup(String group) throws BadRequestException {
        try {
            return Group.valueOf(group.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(e);
        }
    }

    public static ShortStatisticsDto getShortPlatformStatistics(Long userId, String from, String to)
            throws BadRequestException, DbException {
        Map<String, LocalDate> dates = convertToLocalDate(from, to);
        Transaction transaction = DbAssistant.getTransaction();
        try {
            RequestDao requestDao = DaoFactory.getRequestDao();
            List<GroupByElementsStatistics> groupByElementsStatisticsList =
                    requestDao.getGroupByPlatformsStatistics(userId, dates.get("from"), dates.get("to"));
            TotalStatistics totalStatistics = requestDao
                    .getTotalAllPlatformsStatistics(userId,  dates.get("from"), dates.get("to"));
            return new ShortStatisticsDto(
                    dates.get("from"),
                    dates.get("to"),
                    totalStatistics,
                    groupByElementsStatisticsList
            );
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static DetailStatisticsDto getDetailPlatformStatistics(
            Long userId,
            Long platformId,
            String from,
            String to,
            String group)
            throws DbException, BadRequestException {
        Map<String, LocalDate> dates = convertToLocalDate(from, to);
        Transaction transaction = DbAssistant.getTransaction();
        try {
            RequestDao requestDao = DaoFactory.getRequestDao();
            List<GroupByPeriodsStatistics> groupByPeriodsStatisticsList =
                    requestDao.getGroupByPeriodsPlatformStatistics(
                            userId, platformId, dates.get("from"), dates.get("to"), convertStringToGroup(group));
            TotalStatistics totalStatistics = requestDao
                    .getTotalPlatformStatistics(userId, platformId, dates.get("from"), dates.get("to"));
            return new DetailStatisticsDto(
                    dates.get("from"),
                    dates.get("to"),
                    totalStatistics,
                    groupByPeriodsStatisticsList
            );
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
