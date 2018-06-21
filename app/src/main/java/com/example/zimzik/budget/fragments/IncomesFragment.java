package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.IncomeListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Income;
import com.example.zimzik.budget.fragments.dialog_fragments.AddIncomeDialogFragment;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IncomesFragment extends Fragment {
    private AppDB mDB;
    private TextView mTvTotalSumm;
    private IncomeListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private AddIncomeDialogFragment mAddIncomeDialogFragment;
    public IncomesFragment() {
        // Required empty public constructor
    }

    public static IncomesFragment newInstance() {
        
        Bundle args = new Bundle();
        
        IncomesFragment fragment = new IncomesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        mAddIncomeDialogFragment = AddIncomeDialogFragment.newInstance(income -> mDB.getIncomeRepo().insertIncome(income)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), R.string.income_successfully_saved, Toast.LENGTH_LONG).show();
                    refreshTable();
                }));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_incomes, container, false);
        view.findViewById(R.id.add_money_btn_income).setOnClickListener(v -> addMoneyButtonClick());
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ_income);
        mRecyclerView = view.findViewById(R.id.rv_incomes);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable();
        return view;
    }

    @SuppressLint("CheckResult")
    private void addMoneyButtonClick() {
        mAddIncomeDialogFragment.show(getFragmentManager(), "AddIncomeDialog");
    }

    @SuppressLint("CheckResult")
    private void refreshTable() {
        mDB.getIncomeRepo().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(incomes -> {
                    Collections.sort(incomes, (i1, i2) -> {
                        Long i1Time = i1.getDate();
                        Long i2Time = i2.getDate();
                        if (i2Time > i1Time) return 1;
                        else if (i2Time == i1Time) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Income r : incomes) {
                        summ += r.getSumm();
                    }
                    String s = String.format(getString(R.string.total) + " " + summ);
                    mTvTotalSumm.setText(s);
                    mAdapter = new IncomeListAdapter(incomes, income -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.are_you_shure_to_delete_period);
                        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> deleteIncomeFromDB(income));
                        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

                        });
                        builder.setCancelable(true);
                        builder.show();
                    });
                    mRecyclerView.setAdapter(mAdapter);

                });
    }

    @SuppressLint("CheckResult")
    private void deleteIncomeFromDB(Income income) {
        mDB.getIncomeRepo().deleteIncome(income)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), R.string.income_successfully_deleted, Toast.LENGTH_LONG).show();
                    refreshTable();
                });
    }
}
