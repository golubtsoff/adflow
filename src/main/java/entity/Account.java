package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Account {

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "PAYMENT_DETAILS")
    private String paymentDetails;


    public Account() {
    }

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }


    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                ", paymentDetails='" + paymentDetails + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(balance, account.balance) &&
                Objects.equals(paymentDetails, account.paymentDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, paymentDetails);
    }
}
