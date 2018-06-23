package com.example.zimzik.budget.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.ArchivedMemberListAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.fragments.MemberListFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ArchivedMemberListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

       private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArchivedMemberListAdapter mMemberListAdapter;
    private RecyclerView mRecyclerView;
    private AppDB mDB;
    private static final String MEMBER_KEY = "member";
    private static final String DIRNAME = "avatarsDir";
    private static final String TAG = MemberListFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_member_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.archive));
        }

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mRecyclerView = findViewById(R.id.rv_members);
        mMemberListAdapter = new ArchivedMemberListAdapter(null, null, null, null);
        mDB = AppDB.getsInstance(this);
        refreshList();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.archive_member_list_menu, menu);
        MenuItem item = menu.findItem(R.id.search_member);
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
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        refreshList();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @SuppressLint("CheckResult")
    private void refreshList() {
        mDB.getMemberRepo().getAllArchivedMembers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(memberList -> {
                    Collections.sort(memberList, (m1, m2) -> m1.toString().compareToIgnoreCase(m2.toString()));

                    ArchivedMemberListAdapter listAdapter = new ArchivedMemberListAdapter(memberList, m -> {
                        Intent intent = new Intent(this, CurrentArchivedMemberActivity.class);
                        Gson gson = new Gson();
                        String myJson = gson.toJson(m);
                        intent.putExtra(MEMBER_KEY, myJson);
                        startActivity(intent);
                    }, m -> {
                        deleteAvatarImageFromStorage(m);
                        deleteMemberFromDB(m);
                    }, m -> {
                        m.setActual(1);
                        mDB.getMemberRepo()
                                .update(m)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::refreshList);
                    });
                    mMemberListAdapter = listAdapter;
                    mRecyclerView.setAdapter(listAdapter);
                }, throwable -> Log.i(TAG, getString(R.string.get_all_members_error)));
    }


    // delete member from db method
    private void deleteMemberFromDB(Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_shure) + m.getLastName() + " " + m.getFirstName() + " " + getString(R.string.from_db));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> mDB.getMemberRepo().delete(m)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(this, R.string.member_successfully_delete, Toast.LENGTH_LONG).show();
                    refreshList();
                }));
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    private void deleteAvatarImageFromStorage(Member member) {
        if (member.getTimeIdent() != 0) {
            ContextWrapper cw = new ContextWrapper(this);
            File directory = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
            File file = new File(directory.getAbsolutePath(), member.getTimeIdent() + ".jpg");
            file.delete();
        }
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
