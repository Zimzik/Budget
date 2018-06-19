package com.example.zimzik.budget.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zimzik.budget.R;

public class IncomesFragment extends Fragment {
    public IncomesFragment() {
    }

    public static IncomesFragment newInstance() {
        
        Bundle args = new Bundle();
        
        IncomesFragment fragment = new IncomesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incomes, container, false);
        return view;
    }
}
