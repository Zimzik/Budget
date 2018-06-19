package com.example.zimzik.budget.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zimzik.budget.R;

public class MemberListFragment extends Fragment {
    public MemberListFragment() {
    }

    public static MemberListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MemberListFragment fragment = new MemberListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
        return view;
    }
}
