package com.example.zimzik.budget.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.zimzik.budget.data.db.models.Expense;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ExpenseDao {

    @Insert
    void insertExpense(Expense expense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateExpense(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM Expense")
    Single<List<Expense>> getAll();

}
