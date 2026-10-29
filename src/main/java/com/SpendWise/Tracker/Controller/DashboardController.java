
package com.SpendWise.Tracker.Controller;

import com.SpendWise.Tracker.entity.Expense;
import com.SpendWise.Tracker.service.ExpenseService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.time.Month;
import java.util.LinkedHashMap;
@Controller
public class DashboardController {

    private final ExpenseService expenseService;

    public DashboardController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        loadDashboardData(model);
        model.addAttribute("expense", new Expense());

        return "dashboard";
    }

    @PostMapping("/dashboard/add")
    public String addExpense(
            @ModelAttribute("expense") Expense expense,
            RedirectAttributes redirectAttributes) {

        try {
            if (expense.getExpenseDate() == null) {
                expense.setExpenseDate(java.time.LocalDate.now());
            }

            expenseService.addExpense(expense);

            redirectAttributes.addFlashAttribute(
                    "message", "Expense added successfully!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not save expense. Please check your details."
            );
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            expenseService.deleteExpense(id);

            redirectAttributes.addFlashAttribute(
                    "message", "Expense deleted successfully!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }

        return "redirect:/dashboard";
    }

    private void loadDashboardData(Model model) {

        List<Expense> expenses = expenseService.getAllExpenses();

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth currentMonth = YearMonth.now();
        int currentYear = currentMonth.getYear();

        Map<String, BigDecimal> monthlyTotals = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            BigDecimal total = expenses.stream()
                .filter(e -> e.getExpenseDate() != null)
                .filter(e -> e.getExpenseDate().getYear() == currentYear)
                .filter(e -> e.getExpenseDate().getMonth() == month)
                .map(Expense::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyTotals.put(
                month.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    java.util.Locale.ENGLISH
                ),
                total
            );
        }

        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("currentYear", currentYear);

        BigDecimal monthlyExpenses = expenses.stream()
                .filter(e -> e.getExpenseDate() != null
                        && YearMonth.from(e.getExpenseDate())
                                .equals(currentMonth))
                .map(Expense::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Expense> recentExpenses = expenses.stream()
                .sorted(Comparator.comparing(
                        Expense::getExpenseDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(10)
                .toList();

        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("totalTransactions", expenses.size());
        model.addAttribute("recentExpenses", recentExpenses);

        model.addAttribute(
                "categoryTotals",
                expenseService.getCategoryTotals()
        );

        model.addAttribute(
                "merchantTotals",
                expenseService.getMerchantTotals()
        );
    }
}
