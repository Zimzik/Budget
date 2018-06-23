package com.example.zimzik.budget.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.zimzik.budget.data.db.models.Member;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface MemberDao {
    @Insert
    void insertAll(Member member);

    @Delete
    void delete(Member member);

    @Query("SELECT * FROM member WHERE actual=1")
    Single<List<Member>> getAllMembers();
    //List<Member> getAllMembers();

    @Query(("SELECT * FROM member WHERE actual=0"))
    Single<List<Member>> getAllArchivedMembers();

    @Update
    void update(Member member);
}
