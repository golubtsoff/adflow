package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "PAYMENT_DETAILS")
    private String paymentDetails;


    public Account() {
    }

    public Account(BigDecimal balance, String paymentDetails) {
        this(null, balance, paymentDetails);
    }

    public Account(Long id, BigDecimal balance, String paymentDetails) {
        this.id = id;
        this.balance = balance;
        this.paymentDetails = paymentDetails;
    }

    public Long getId() {
        return id;
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
                "id=" + id +
                ", balance=" + balance +
                ", paymentDetails='" + paymentDetails + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(paymentDetails, account.paymentDetails);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, balance, paymentDetails);
    }
}
