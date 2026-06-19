package com.expense.tracker.controller;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.User;
import com.expense.tracker.repository.ExpenseRepository;
import com.expense.tracker.repository.UserRepository;
import com.expense.tracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import com.expense.tracker.model.Category;
import com.expense.tracker.repository.CategoryRepository;
import com.expense.tracker.model.Payment;
import com.expense.tracker.repository.PaymentRepository;


@Controller
public class TrackerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
private CategoryRepository categoryRepository;
@Autowired
private PaymentRepository paymentRepository;
  

    @GetMapping("/")
    public String home() {
        return "modules/home";
    }

   @PostMapping("/login")
@ResponseBody
public ResponseEntity<String> login(
        @RequestParam String username,
        @RequestParam String password,
        HttpSession session) {

    if (userService.login(username, password)) {

        User user = userRepository.findByUsername(username);

        session.setAttribute("user", user);

        return ResponseEntity.ok("success");
    }

    return ResponseEntity.status(401).body("invalid");
}

   @PostMapping("/register")
public ResponseEntity<String> register(@RequestParam String username,
                                       @RequestParam String password) {
    userService.register(username, password);
    return ResponseEntity.ok("registered");
}

    @GetMapping("/dashboard")
    public String dashboard() {
        return "modules/dashboard";
    }

    @PostMapping("/api/expenses")
    public ResponseEntity<String> addExpense(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam Double amount,
            @RequestParam String category,
            @RequestParam String payment,
            @RequestParam(required = false) String note,
            @RequestParam String date) {

        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        Expense expense = new Expense();
        expense.setName(name);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setPayment(payment);
        expense.setNote(note);
        expense.setDate(LocalDate.parse(date));
        expense.setUser(user);

        expenseRepository.save(expense);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("/api/expenses")
public ResponseEntity<List<Expense>> getExpenses(@RequestParam String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) return ResponseEntity.badRequest().build();
    return ResponseEntity.ok(expenseRepository.findByUserAndDeletedFalse(user));
}
@DeleteMapping("/api/expenses/{id}")
public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
    Expense expense = expenseRepository.findById(id).orElse(null);
    if (expense == null) return ResponseEntity.badRequest().body("Not found");
    expense.setDeleted(true);
    expenseRepository.save(expense);
    return ResponseEntity.ok("deleted");
}
@PutMapping("/api/expenses/{id}")
public ResponseEntity<String> updateExpense(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam Double amount,
        @RequestParam String category,
        @RequestParam String payment,
        @RequestParam(required = false) String note,
        @RequestParam String date) {

    Expense expense = expenseRepository.findById(id).orElse(null);
    if (expense == null) return ResponseEntity.badRequest().body("Not found");

    expense.setName(name);
    expense.setAmount(amount);
    expense.setCategory(category);
    expense.setPayment(payment);
    expense.setNote(note);
    expense.setDate(LocalDate.parse(date));

    expenseRepository.save(expense);
    return ResponseEntity.ok("updated");
}
@GetMapping("/api/expenses/search")
public ResponseEntity<List<Expense>> searchExpenses(
        @RequestParam String username,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String payment) {

    User user = userRepository.findByUsername(username);
    if (user == null) return ResponseEntity.badRequest().build();

    List<Expense> expenses = expenseRepository.findByUserAndDeletedFalse(user);

    // Filter by keyword
    if (keyword != null && !keyword.isEmpty()) {
        expenses = expenses.stream()
            .filter(e -> e.getName().toLowerCase().contains(keyword.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }

    // Filter by category
    if (category != null && !category.isEmpty() && !category.equals("All")) {
        expenses = expenses.stream()
            .filter(e -> e.getCategory().equals(category))
            .collect(java.util.stream.Collectors.toList());
    }

    // Filter by payment
    if (payment != null && !payment.isEmpty() && !payment.equals("All")) {
        expenses = expenses.stream()
            .filter(e -> e.getPayment().equals(payment))
            .collect(java.util.stream.Collectors.toList());
    }

    return ResponseEntity.ok(expenses);
}
@GetMapping("/expenses/category/{category}")
public List<Expense> getByCategory(
        @PathVariable String category,
        HttpSession session) {

    User user = (User) session.getAttribute("user");

    return expenseRepository
            .findByUserAndCategoryAndDeletedFalse(user, category);
}
@GetMapping("/api/categories")
@ResponseBody
public List<Category> getCategories() {
    return categoryRepository.findByType("category");
}

@GetMapping("/api/payments")
@ResponseBody
public List<Payment> getPayments() {
    return paymentRepository.findByType("payment");
}
}

