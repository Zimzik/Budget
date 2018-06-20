package com.example.zimzik.budget.data.db.repo;

import com.example.zimzik.budget.data.db.dao.IncomeDao;
import com.example.zimzik.budget.data.db.models.Income;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class IncomeRepo {
    private final IncomeDao mIncomeDao;

    public IncomeRepo(IncomeDao incomeDao) {
        mIncomeDao = incomeDao;
    }

    public Completable insertIncome(Income income) {
        return Completable.fromAction(() -> mIncomeDao.insertIncome(income));
    }

    public Completable updateIncome(Income income) {
        return Completable.fromAction(() -> mIncomeDao.updateIncome(income));
    }

    public Completable deleteIncome(Income income) {
        return Completable.fromAction(() -> mIncomeDao.deleteIncome(income));
    }

    public Single<List<Income>> getAll() {
        return mIncomeDao.getAll();
    }
}
