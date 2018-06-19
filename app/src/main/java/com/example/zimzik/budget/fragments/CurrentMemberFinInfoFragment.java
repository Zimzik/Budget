package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.Helper;
import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.FinancialListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.data.db.models.Period;
import com.google.gson.Gson;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CurrentMemberFinInfoFragment extends Fragment {

    private Member mMember;
    private static final String KEY_MEMBER = "KEY_MEMBER";
    private final Gson mGson = new Gson();
    private AppDB mDB;
    private FinancialListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTvTotalSumm;

    // for dialog
    private int mMonthNum;
    private int mYear;
    private int mMoney;
    private Spinner mMonthSpinner, mYearsSpinner;
    private EditText mEtMoney;

    public CurrentMemberFinInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMember = mGson.fromJson(getArguments().getString(KEY_MEMBER), Member.class);
        }
    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_member_fin_info, container, false);
        mTvTotalSumm = view.findViewById(R.id.tv_total_summ);
        view.findViewById(R.id.add_money_btn).setOnClickListener(v -> addMoneyButtonClick());
        mDB = AppDB.getsInstance(getContext());
        mRecyclerView = view.findViewById(R.id.rv_periods);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshTable();
        return view;
    }

    @Override
    public void onResume() {
        refreshTable();
        super.onResume();
    }

    public static CurrentMemberFinInfoFragment newInstance(Member member) {
        CurrentMemberFinInfoFragment fragment = new CurrentMemberFinInfoFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(KEY_MEMBER, gson.toJson(member));
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressLint("CheckResult")
    private void addMoneyButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(setDialogView())
                .setMessage(R.string.add_new_membership_free)
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {

                })
                .setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (mEtMoney.getText().toString().isEmpty()) {
                mEtMoney.setError(getString(R.string.this_field_is_empty));
            } else {
                mMoney = Integer.valueOf(mEtMoney.getText().toString());
                Period period = new Period(mYear, mMonthNum, mMoney, mMember.getUid());
                mDB.getPeriodRepo().insertMonth(period)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Toast.makeText(getContext(), R.string.info_successfully_saved_on_db, Toast.LENGTH_LONG).show();
                            mEtMoney.setText("");
                        }, __ -> ignoreOrUpdate(period));
                dialog.dismiss();
                refreshTable();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void refreshTable() {
        mDB.getPeriodRepo().selectById(mMember.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(periods -> {
                    Collections.sort(periods, (p1, p2) -> {
                        if (p1.getYear() > p2.getYear() || p1.getYear() == p2.getYear() && p1.getMonthNum() > p2.getMonthNum()) return 1;
                        else if (p1.getYear() == p2.getYear() && p1.getMonthNum() == p2.getMonthNum()) return 0;
                        else return -1;
                    });
                    int summ = 0;
                    for (Period p : periods) {
                        summ += p.getMoney();
                    }
                    String s = getString(R.string.total) + summ;
                    mTvTotalSumm.setText(s);
                    mAdapter = new FinancialListAdapter(periods, period -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.are_you_shure_to_delete_period);
                        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                            deletePeriodFromDb(period);
                        });
                        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

                        });
                        builder.setCancelable(true);
                        builder.show();
                    });
                    mRecyclerView.setAdapter(mAdapter);
                });
    }

    private View setDialogView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_new_membership, null);
        mMonthSpinner = view.findViewById(R.id.months_spinner);
        mYearsSpinner = view.findViewById(R.id.years_spinner);
        mEtMoney = view.findViewById(R.id.et_money);

        // adapters for spinners
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getContext(), R.array.months_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> yearsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.years_array, android.R.layout.simple_spinner_item);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMonthSpinner.setAdapter(monthAdapter);
        mYearsSpinner.setAdapter(yearsAdapter);

        // get number  of month
        mMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMonthNum = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // get mYear
        mYearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYear = Integer.valueOf(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void ignoreOrUpdate(Period period) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setMessage("Information for the current month is present in the DB. Do you want to update this?");
        builder.setPositiveButton("Update", (dialogInterface, i) -> mDB.getPeriodRepo()
                .updateMonth(period)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), "Information successfully updated!", Toast.LENGTH_LONG).show();
                    refreshTable();
                }));


        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    @SuppressLint("CheckResult")
    private void deletePeriodFromDb(Period p) {
        mDB.getPeriodRepo().delete(p)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(getContext(), "Period successfully delete!", Toast.LENGTH_LONG).show();
                    refreshTable();
                });
    }

}
