package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Scene;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.budget.Helper;
import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.AppDB;

import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private TextView mTvMonthAndYear;
    private TextView mTvMembersQuantity;
    private TextView mTvPassedMoney;
    private TextView mTvAllMoneyFromMembers;
    private TextView mTvAllMoneyFromIncomes;
    private TextView mTvExenseSum;
    private TextView mTvTotalSum;

    AppDB mDB;

    private int mCurrentMonth;
    private Calendar mCalendar;

    private Integer mMoneyFromMembers = 0;
    private Integer mMoneyFromIncomes = 0;
    private Integer mMoneyFromExpenses = 0;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
        mCurrentMonth = mCalendar.get(Calendar.MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTvMonthAndYear = view.findViewById(R.id.tv_month_and_year);
        mTvMembersQuantity = view.findViewById(R.id.tv_members_quantity);
        mTvPassedMoney = view.findViewById(R.id.tv_passed_money);
        mTvAllMoneyFromMembers = view.findViewById(R.id.tv_all_money_from_members);
        mTvAllMoneyFromIncomes = view.findViewById(R.id.tv_all_money_from_incomes);
        mTvExenseSum = view.findViewById(R.id.tv_summ_expenses);
        mTvTotalSum = view.findViewById(R.id.tv_total_sum);
        setMonthAndYear();
        setMembersQuantity();
        setPassedMoney();
        loadBalance();
        return view;
    }

    private void setMonthAndYear() {
        String[] months = Helper.getLocatedMonth(getContext());
        int year = mCalendar.get(Calendar.YEAR);
        mTvMonthAndYear.setText(months[mCurrentMonth] + ", " + String.valueOf(year));

    }

    @SuppressLint("CheckResult")
    private void setMembersQuantity() {
        mDB.getMemberRepo().getAllMembers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(members -> mTvMembersQuantity.setText(String.valueOf(members.size())));
    }

    @SuppressLint("CheckResult")
    private void setPassedMoney() {
        mDB.getPeriodRepo().getAllFromMonth(mCurrentMonth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(periods -> mTvPassedMoney.setText(String.valueOf(periods.size())));
    }

    @SuppressLint("CheckResult")
    private void loadBalance() {
        Single<Integer> period = mDB.getPeriodRepo()
                .getAllSum()
                .onErrorReturn(error -> 0)
                .doOnSuccess(sum -> mTvAllMoneyFromMembers.setText(String.valueOf(sum)));

        Single<Integer> income = mDB.getIncomeRepo()
                .getAllSum()
                .onErrorReturn(error -> 0)
                .doOnSuccess(sum -> mTvAllMoneyFromIncomes.setText(String.valueOf(sum)));

        Single<Integer> expense = mDB.getExpenseRepo()
                .getAllSum()
                .onErrorReturn(error -> 0)
                .doOnSuccess(sum -> mTvExenseSum.setText(String.valueOf(sum)));

        Single.zip(period, income, expense, (s1, s2, s3) -> s1 + s2 - s3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setTotalSum);

    }

    private void setTotalSum(int sum) {
        mTvTotalSum.setText(String.valueOf(sum));
    }
}
