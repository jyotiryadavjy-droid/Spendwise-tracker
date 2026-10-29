
package com.SpendWise.Tracker.service.impl;

import com.SpendWise.Tracker.entity.Expense;
import com.SpendWise.Tracker.repository.ExpenseRepository;
import com.SpendWise.Tracker.service.ExpenseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense addExpense(Expense expense) {
        if (expense.getAmount() == null
                || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        if (expense.getMerchant() == null
                || expense.getMerchant().isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter an app or merchant name."
            );
        }

        if (expense.getCategory() == null
                || expense.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a category."
            );
        }

        return expenseRepository.save(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Expense not found with ID: " + id
                        )
                );
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Expense not found with ID: " + id
            );
        }

        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getCategoryTotals() {
        return getAllExpenses().stream()
                .filter(e -> e.getAmount() != null)
                .collect(Collectors.groupingBy(
                        e -> normalizeName(e.getCategory(), "Other"),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMerchantTotals() {
        return getAllExpenses().stream()
                .filter(e -> e.getAmount() != null)
                .collect(Collectors.groupingBy(
                        e -> normalizeName(e.getMerchant(), "Other"),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    private String normalizeName(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        String cleaned = value.trim();

        return cleaned.substring(0, 1).toUpperCase()
                + cleaned.substring(1);
    }
}
