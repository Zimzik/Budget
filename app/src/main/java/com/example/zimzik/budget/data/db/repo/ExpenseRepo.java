package com.example.zimzik.budget.data.db.repo;

import com.example.zimzik.budget.data.db.dao.ExpenseDao;
import com.example.zimzik.budget.data.db.models.Expense;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class ExpenseRepo {
    private final ExpenseDao mExpenseDao;

    public ExpenseRepo(ExpenseDao expenseDao) {
        mExpenseDao = expenseDao;
    }


    public Completable insertExpense(Expense expense) {
        return Completable.fromAction(() -> mExpenseDao.insertExpense(expense));
    }

    public Completable updateExpense(Expense expense) {
        return Completable.fromAction(() -> mExpenseDao.updateExpense(expense));
    }

    public Completable delete(Expense expense) {
        return Completable.fromAction(() -> mExpenseDao.delete(expense));
    }

    public Single<List<Expense>> getAll() {
        return mExpenseDao.getAll();
    }

    public Single<Integer> getAllSum() {return  mExpenseDao.getAllSum();}
}
