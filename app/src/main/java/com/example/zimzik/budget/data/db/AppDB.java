package com.example.zimzik.budget.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.zimzik.budget.data.db.dao.ExpenseDao;
import com.example.zimzik.budget.data.db.dao.IncomeDao;
import com.example.zimzik.budget.data.db.dao.MemberDao;
import com.example.zimzik.budget.data.db.dao.PeriodDao;
import com.example.zimzik.budget.data.db.models.Expense;
import com.example.zimzik.budget.data.db.models.Income;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.data.db.models.Period;
import com.example.zimzik.budget.data.db.repo.ExpenseRepo;
import com.example.zimzik.budget.data.db.repo.IncomeRepo;
import com.example.zimzik.budget.data.db.repo.MemberRepo;
import com.example.zimzik.budget.data.db.repo.PeriodRepo;

@Database(entities = {Member.class, Period.class, Income.class, Expense.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static AppDB sInstance;

    private final PeriodRepo mPeriodRepo = new PeriodRepo(monthDao());
    private final MemberRepo mMemberRepo = new MemberRepo(memberDao());
    private final IncomeRepo mIncomeRepo = new IncomeRepo(incomeDao());
    private final ExpenseRepo mExpenseRepo = new ExpenseRepo(expenseDao());


    public synchronized static AppDB getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "members.db").build();
        }
        return sInstance;
    }

    public abstract MemberDao memberDao();

    public abstract PeriodDao monthDao();

    public abstract IncomeDao incomeDao();

    public abstract ExpenseDao expenseDao();

    public PeriodRepo getPeriodRepo() {
        return mPeriodRepo;
    }

    public MemberRepo getMemberRepo() {
        return mMemberRepo;
    }

    public IncomeRepo getIncomeRepo() { return mIncomeRepo; }

    public ExpenseRepo getExpenseRepo() {return  mExpenseRepo; }
}