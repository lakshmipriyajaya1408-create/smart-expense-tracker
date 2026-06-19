package com.expense.tracker.repository;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserAndDeletedFalse(User user);
   List<Expense> findByUserAndCategoryAndDeletedFalse(User user, String category);
}
