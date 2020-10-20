package fr.misoda.ingenico.model;

import lombok.*;

import javax.persistence.*;

@ToString
@EqualsAndHashCode
@Entity
@Table(name="accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final Currency currency;
    private long balance;

    public BankAccount(Currency currency) {
        this.currency = currency;
    }

    public BankAccount() {
        this.currency = Currency.EUR;
    }

    public Currency getCurrency() { return this.currency; }
    public long getBalance() { return  this.balance;}

    public long add(long amount ) { this.balance += amount; return this.balance; }
}
