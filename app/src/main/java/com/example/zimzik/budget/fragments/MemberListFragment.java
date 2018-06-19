package com.example.zimzik.budget.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.activities.CurrentMemberActivity;
import com.example.zimzik.budget.activities.NewMemberActivity;
import com.example.zimzik.budget.adapters.MemberListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MemberListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MemberListAdapter mMemberListAdapter;
    private RecyclerView mRecyclerView;
    private AppDB mDB;
    private static final String MEMBER_KEY = "member";
    private static final String TAG = MemberListFragment.class.getSimpleName();

    public MemberListFragment() {
        // Required empty public constructor
    }

    public static MemberListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MemberListFragment fragment = new MemberListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = AppDB.getsInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate fragment_member_list.xml
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
        //define SwipeRefreshLayout and RecyclerView
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = view.findViewById(R.id.rv_members);
        refreshList();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.member_list_menu, menu);
        MenuItem item = menu.findItem(R.id.search_member);
        //define SearchView and handle queries from it
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mMemberListAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_new_member) {
            startActivity(new Intent(getContext(), NewMemberActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        refreshList();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @SuppressLint("CheckResult")
    private void refreshList() {
        mDB.getMemberRepo().getAllMembers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(memberList -> {
                    Collections.sort(memberList, (m1, m2) -> m1.toString().compareToIgnoreCase(m2.toString()));

                    MemberListAdapter listAdapter = new MemberListAdapter(memberList, m -> {
                        Intent intent = new Intent(getActivity(), CurrentMemberActivity.class);
                        Gson gson = new Gson();
                        String myJson = gson.toJson(m);
                        intent.putExtra(MEMBER_KEY, myJson);
                        startActivity(intent);
                    }, m -> deleteMemberFromDB(m));
                    mRecyclerView.setAdapter(listAdapter);
                }, throwable -> Log.i(TAG, getString(R.string.get_all_members_error)));
    }

    // delete member from db method
    private void deleteMemberFromDB(Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.are_you_shure) + m.getLastName() + " " + m.getFirstName() + " " + getString(R.string.from_db));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> mDB.getMemberRepo().delete(m)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshList));
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    // calculate age
    public static int calculateAge(long birthday) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTime(new Date(birthday));
        dob.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age;
    }
}
