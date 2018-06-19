package com.example.zimzik.budget.data.db.repo;

import com.example.zimzik.budget.data.db.dao.PeriodDao;
import com.example.zimzik.budget.data.db.models.Period;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PeriodRepo {
    private final PeriodDao mPeriodDao;

    public PeriodRepo(PeriodDao mPeriodDao) {
        this.mPeriodDao = mPeriodDao;
    }

    public Completable insertMonth(Period period) {
        return Completable.fromAction(() -> mPeriodDao.insertPeriod(period));
    }

    public Completable updateMonth(Period period) {
        return Completable.fromAction(() -> mPeriodDao.updatePeriod(period));
    }

    public Completable delete(Period period) {
        return Completable.fromAction(() -> mPeriodDao.delete(period));
    }

    public Single<List<Period>> selectById(int id) {
        return mPeriodDao.selectById(id);
    }

    public Single<List<Period>> getAll() {
        return mPeriodDao.getAll();
    }

    public Completable deleteAll() {
        return Completable.fromAction(mPeriodDao::deleteAll);
    }
}