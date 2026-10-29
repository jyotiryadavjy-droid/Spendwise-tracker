
package com.SpendWise.Tracker.service;

import com.SpendWise.Tracker.entity.Expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExpenseService {

    Expense addExpense(Expense expense);

    List<Expense> getAllExpenses();

    Expense getExpenseById(Long id);

    void deleteExpense(Long id);

    Map<String, BigDecimal> getCategoryTotals();

    Map<String, BigDecimal> getMerchantTotals();
}
