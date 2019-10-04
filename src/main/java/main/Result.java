package main;

import entity.users.customer.Campaign;

import java.math.BigDecimal;

public class Result {
    private Campaign campaign;
    private BigDecimal crt;
    private Integer rbbu;
    private double k;
    private double kCrt;

    public Result(Campaign campaign, BigDecimal crt, Integer rbbu){
        this.campaign = campaign;
        this.crt = crt;
        this.rbbu = rbbu;
        this.k = 2-rbbu-crt.doubleValue()/(1000 * this.campaign.getDailyBudget().doubleValue());
        this.kCrt = 1 - (crt.doubleValue() + campaign.getCpmRate().doubleValue())
                / (1000 * this.campaign.getDailyBudget().doubleValue());
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public BigDecimal getCrt() {
        return crt;
    }

    public void setCrt(BigDecimal crt) {
        this.crt = crt;
    }

    public Integer getRbbu() {
        return rbbu;
    }

    public void setRbbu(Integer rbbu) {
        this.rbbu = rbbu;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getkCrt() {
        return kCrt;
    }

    public void setkCrt(double kCrt) {
        this.kCrt = kCrt;
    }

    @Override
    public String toString() {
        return "Result{" +
                "campaign=" + campaign.getId() +
                ", crt=" + crt +
                ", rbbu=" + rbbu +
                ", k=" + k +
                ", kCrt=" + kCrt +
                '}';
    }
}
