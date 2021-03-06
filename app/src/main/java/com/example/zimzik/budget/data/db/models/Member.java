package com.example.zimzik.budget.data.db.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Member {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String firstName;
    private String lastName;
    private long birthday;
    private String phoneNumber;
    private int actual;
    private long timeIdent;

    public Member(String firstName, String lastName, long birthday, String phoneNumber, long timeIdent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        actual = 1;
        this.timeIdent = timeIdent;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public long getTimeIdent() {
        return timeIdent;
    }

    public void setTimeIdent(long timeIdent) {
        this.timeIdent = timeIdent;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return uid == member.uid &&
                birthday == member.birthday &&
                actual == member.actual &&
                timeIdent == member.timeIdent &&
                Objects.equals(firstName, member.firstName) &&
                Objects.equals(lastName, member.lastName) &&
                Objects.equals(phoneNumber, member.phoneNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uid, firstName, lastName, birthday, phoneNumber, actual, timeIdent);
    }
}