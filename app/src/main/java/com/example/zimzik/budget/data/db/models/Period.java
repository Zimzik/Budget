package com.example.zimzik.budget.data.db.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(indices = {@Index(value = {"year", "monthNum", "member_id"}, unique = true)}, foreignKeys = @ForeignKey(entity = Member.class, parentColumns = "uid", childColumns = "member_id", onDelete = ForeignKey.CASCADE))
public class Period {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int year;
    private int monthNum;
    private int money;
    @ColumnInfo(name = "member_id")
    private int memberId;


    public Period(int year, int monthNum, int money, int memberId) {
        this.year = year;
        this.monthNum = monthNum;
        this.money = money;
        this.memberId = memberId;
    }

    public int getId() {
        return id;
    }

    public void setId(int mid) {
        this.id = mid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthNum() {
        return monthNum;
    }

    public void setMonthNum(int monthNum) {
        this.monthNum = monthNum;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int member_id) {
        this.memberId = member_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return id == period.id &&
                year == period.year &&
                monthNum == period.monthNum &&
                money == period.money &&
                memberId == period.memberId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, year, monthNum, money, memberId);
    }
}