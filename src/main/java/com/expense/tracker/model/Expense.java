package com.expense.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double amount;
    private String category;
    private String payment;
    private String note;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    private boolean deleted = false;

public boolean isDeleted() { return deleted; }
public void setDeleted(boolean deleted) { this.deleted = deleted; }
}