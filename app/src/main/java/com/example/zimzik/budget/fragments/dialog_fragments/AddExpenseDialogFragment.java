package com.example.zimzik.budget.fragments.dialog_fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Expense;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddExpenseDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog mDatePickerDialog;

    private EditText mEtDescription, mEtMoney;
    private TextView mTvDate;

    private Date mIncomeDate;
    private static AddExpenseDialogFragment.SaveExpense<Expense> sSaveExpense;

    public static final String DATEPICKER_TAG = "datepicker";

    public static AddExpenseDialogFragment newInstance(SaveExpense<Expense> saveExpense) {
        sSaveExpense = saveExpense;

        Bundle args = new Bundle();

        AddExpenseDialogFragment fragment = new AddExpenseDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        mDatePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.add_new_expense);
        View view = inflater.inflate(R.layout.dialog_add_new_expense, null);

        mTvDate = view.findViewById(R.id.tv_expense_date);
        mEtDescription = view.findViewById(R.id.et_expense_descr);
        mEtMoney = view.findViewById(R.id.et_expense_money);

        mTvDate.setOnClickListener(v -> {
            mDatePickerDialog.setVibrate(isVibrate());
            mDatePickerDialog.setYearRange(1985, 2028);
            mDatePickerDialog.setCloseOnSingleTapDay(false);
            mDatePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
        });

        view.findViewById(R.id.save_expense_btn).setOnClickListener(v -> onSaveButtonClick());
        view.findViewById(R.id.cancel_new_expense_btn).setOnClickListener(v -> onCancelButtonClick());

        return view;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mIncomeDate = calendar.getTime();
        mTvDate.setText(new SimpleDateFormat("dd/MM/yyy").format(calendar.getTime()));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void onSaveButtonClick() {
        mTvDate.setError(null);
        mEtDescription.setError(null);
        mEtMoney.setError(null);
        if (mTvDate.getText().toString().isEmpty() || mEtDescription.getText().toString().isEmpty() || mEtMoney.getText().toString().isEmpty()) {
            if (mTvDate.getText().toString().isEmpty()) {
                mTvDate.setError(getString(R.string.this_field_is_empty));
            }
            if (mEtDescription.getText().toString().isEmpty()) {
                mEtDescription.setError(getString(R.string.this_field_is_empty));
            }
            if (mEtMoney.getText().toString().isEmpty()) {
                mEtMoney.setError(getString(R.string.this_field_is_empty));
            }
        } else {
            Expense expense = new Expense(mEtDescription.getText().toString(), Integer.valueOf(mEtMoney.getText().toString()), mIncomeDate.getTime());
            sSaveExpense.call(expense);
            mTvDate.setText(getString(R.string.date));
            mEtDescription.setText("");
            mEtMoney.setText("");
            getDialog().dismiss();
        }
    }

    private void onCancelButtonClick() {
        getDialog().dismiss();
    }

    private Boolean isVibrate() {
        return true;
    }

    public interface SaveExpense<T> {
        void call(T object);
    }
}
