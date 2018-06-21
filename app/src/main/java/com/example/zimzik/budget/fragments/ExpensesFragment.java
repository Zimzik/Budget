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
import com.example.zimzik.budget.adapters.ExpenseListAdapter;
import com.example.zimzik.budget.adapters.IncomeListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Expense;
import com.example.zimzik.budget.data.db.models.Income;
import com.example.zimzik.budget.fragments.dialog_fragments.AddExpenseDialogFragment;

import java.util.Collections;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ExpensesFragment extends Fragment {
    private AppDB mDB;
    private TextView mTvTotalSumm;
    private ExpenseListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    AddExpenseDialogFragment mAddExpenseDialogFragment;

    public static ExpensesFragment newInstance() {

        Bundle args = new Bundle();

        ExpensesFragment fragment = new ExpensesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        mAddExpenseDialogFragment = AddExpenseDialogFragment.newInstance(expense -> {
            mDB.getExpenseRepo().insertExpense(expense)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(getContext(), R.string.expense_successfully_saved, Toast.LENGTH_LONG).show();
                        refreshTable();
                    });
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        view.findViewById(R.id.add_money_btn_expense).setOnClickListener(v -> addMoneyButtonClick());
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ_expense);
        mRecyclerView = view.findViewById(R.id.rv_expenses);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable();
        return view;
    }

    @SuppressLint("CheckResult")
    private void addMoneyButtonClick() {
        mAddExpenseDialogFragment.show(getFragmentManager(), "AddExpenseDialog");
    }

    @SuppressLint("CheckResult")
    private void refreshTable() {
        mDB.getExpenseRepo().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(expenses -> {
                    Collections.sort(expenses, (e1, e2) -> {
                        Long e1Time = e1.getDate();
                        Long e2Time = e2.getDate();
                        if (e2Time > e1Time) return 1;
                        else if (e2Time == e1Time) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Expense r : expenses) {
                        summ += r.getSumm();
                    }
                    String s = String.format(getString(R.string.total) + " " + summ);
                    mTvTotalSumm.setText(s);
                    mAdapter = new ExpenseListAdapter(expenses, expense -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.are_you_sure_to_delete_this_expense);
                        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> deleteExpenseFromDB(expense));
                        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

                        });
                        builder.setCancelable(true);
                        builder.show();
                    });
                    mRecyclerView.setAdapter(mAdapter);

                });
    }

    @SuppressLint("CheckResult")
    private void deleteExpenseFromDB(Expense expense) {
        mDB.getExpenseRepo().delete(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), R.string.expense_succesfully_saved, Toast.LENGTH_LONG).show();
                    refreshTable();
                });
    }
}
