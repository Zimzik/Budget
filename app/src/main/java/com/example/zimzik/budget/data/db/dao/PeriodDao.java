package com.example.zimzik.budget.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.zimzik.budget.data.db.models.Period;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PeriodDao {
    @Insert
    void insertPeriod(Period period);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updatePeriod(Period period);

    @Delete
    void delete(Period period);

    @Query("SELECT * FROM Period WHERE member_id LIKE :id AND monthNum LIKE :monthNum AND year LIKE :year")
    Single<List<Period>> checkForAvail(int id, int monthNum, int year);

    @Query("SELECT * FROM Period WHERE member_id LIKE :id")
    Single<List<Period>> selectById(int id);

    @Query("SELECT * FROM Period")
    Single<List<Period>> getAll();

    @Query("DELETE FROM Period")
    void deleteAll();

    @Query("SELECT * FROM Period WHERE monthNum LIKE :month")
    Single<List<Period>> getAllForMonth(int month);

    @Query("SELECT SUM(money) FROM Period")
    Single<Integer> getTotalSum();
}