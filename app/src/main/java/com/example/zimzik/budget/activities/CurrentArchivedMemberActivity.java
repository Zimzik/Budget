
package com.example.zimzik.budget.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.adapters.ViewPagerAdapter;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.fragments.CurrentArchMemberFinInfoFragment;
import com.example.zimzik.budget.fragments.CurrentMemberFinInfoFragment;
import com.example.zimzik.budget.fragments.CurrentMemberInfoFragment;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CurrentArchivedMemberActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private Member mMember;
    private AppDB mDB;
    private Gson mGson = new Gson();
    private final String KEY_MEMBER = "member";
    private static final String DIRNAME = "avatarsDir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_archived_member);

        mDB = AppDB.getsInstance(this);
        mMember = mGson.fromJson(getIntent().getStringExtra(KEY_MEMBER), Member.class);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(mMember.toString());
        }

        mViewPager = findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_archived_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cm_menu_delete) {
            onDeleteMemberClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            mMember = mGson.fromJson(data.getStringExtra(KEY_MEMBER), Member.class);
            CurrentMemberInfoFragment fragment = (CurrentMemberInfoFragment) mAdapter.getItem(1);
            fragment.refreshData(mMember);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        CurrentArchMemberFinInfoFragment currentArchMemberFinInfoFragment = CurrentArchMemberFinInfoFragment.newInstance(mMember);
        CurrentMemberInfoFragment memberInfoFragment = CurrentMemberInfoFragment.newInstance(mMember);

        mAdapter.addFragment(currentArchMemberFinInfoFragment, getString(R.string.financial_info));
        mAdapter.addFragment(memberInfoFragment, getString(R.string.member_info));
        viewPager.setAdapter(mAdapter);
    }

    private void onDeleteMemberClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_shure) + mMember.getLastName() + " " + mMember.getFirstName() + " " + getString(R.string.from_db));
        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> mDB.getMemberRepo().delete(mMember)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    deleteAvatarImageFromStorage();
                    Toast.makeText(this, R.string.member_successfully_delete, Toast.LENGTH_LONG).show();
                    finish();
                }));
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

        });
        builder.setCancelable(true);
        builder.show();
    }

    private void deleteAvatarImageFromStorage() {
        if (mMember.getTimeIdent() != 0) {
            ContextWrapper cw = new ContextWrapper(this);
            File directory = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
            File file = new File(directory.getAbsolutePath(), mMember.getTimeIdent() + ".jpg");
            file.delete();
        }
    }
}
