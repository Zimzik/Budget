package com.example.zimzik.budget.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.zimzik.budget.data.db.models.Income;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface IncomeDao {

    @Insert
    void insertIncome(Income income);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateIncome(Income income);

    @Delete
    void deleteIncome(Income income);

    @Query("SELECT * FROM Income")
    Single<List<Income>> getAll();
}
