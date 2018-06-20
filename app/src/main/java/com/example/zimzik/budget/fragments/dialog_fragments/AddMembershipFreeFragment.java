package com.example.zimzik.budget.fragments.dialog_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.zimzik.budget.Helper;
import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Period;

public class AddMembershipFreeFragment extends DialogFragment {
    private Spinner mYearSpinner, mMonthSpinner;
    private EditText mEtMoney;
    private int mMonthNum, mYear, mMoney;
    private static int sMemberId;
    private static SaveClick<Period> sSaveClick;

    public static AddMembershipFreeFragment newInstance(int memberId, SaveClick<Period> saveClick) {
        sMemberId = memberId;
        sSaveClick = saveClick;

        Bundle args = new Bundle();

        AddMembershipFreeFragment fragment = new AddMembershipFreeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setTitle(R.string.add_new_membership_free);
        View view = inflater.inflate(R.layout.dialog_fragment_add_membership_free, null);
        mYearSpinner = view.findViewById(R.id.years_spinner);
        mMonthSpinner = view.findViewById(R.id.months_spinner);
        mEtMoney = view.findViewById(R.id.et_membership);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Helper.getLocatedMonth(getContext()));
        ArrayAdapter<CharSequence> yearsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.years_array, android.R.layout.simple_spinner_dropdown_item);


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
        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYear = Integer.valueOf(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMonthSpinner.setAdapter(monthAdapter);
        mYearSpinner.setAdapter(yearsAdapter);

        view.findViewById(R.id.save_membership_btn).setOnClickListener(v -> {
            //money field validation
            mEtMoney.setError(null);
            if (mEtMoney.getText().toString().isEmpty()) {
                mEtMoney.setError(getString(R.string.this_field_is_empty));
            } else {
                //if money field is not empty, get this value and save period on DB
                mMoney = Integer.valueOf(mEtMoney.getText().toString());
                Period period = new Period(mYear, mMonthNum, mMoney, sMemberId);
                sSaveClick.click(period);
                dismiss();
            }
        });

        //close dialog fragment whet click "cancel" button
        view.findViewById(R.id.cancel_new_membership_btn).setOnClickListener(v -> dismiss());

        return view;
    }

    public interface SaveClick<T> {
        void click(T object);
    }
}
