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
    private long phoneNumber;
    private long timeIdent;

    public Member(String firstName, String lastName, long birthday, long phoneNumber, long timeIdent) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
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
                phoneNumber == member.phoneNumber &&
                timeIdent == member.timeIdent &&
                Objects.equals(firstName, member.firstName) &&
                Objects.equals(lastName, member.lastName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uid, firstName, lastName, birthday, phoneNumber, timeIdent);
    }
}