package com.example.zimzik.budget.activities;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.fragments.ExpensesFragment;
import com.example.zimzik.budget.fragments.HomeFragment;
import com.example.zimzik.budget.fragments.IncomesFragment;
import com.example.zimzik.budget.fragments.MemberListFragment;

public class MainActivity extends AppCompatActivity {


    // set listener to bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.navigation_home:
                setActionBarTitle(getResources().getString(R.string.app_name));
                setFragment(HomeFragment.newInstance(), ft);
                return true;
            case R.id.navigation_member_list:
                setActionBarTitle(getResources().getString(R.string.members));
                setFragment(MemberListFragment.newInstance(), ft);
                return true;
            case R.id.navigation_incomes:
                setActionBarTitle(getResources().getString(R.string.revenues));
                setFragment(IncomesFragment.newInstance(), ft);
                return true;
            case R.id.navigation_expenses:
                setActionBarTitle(getResources().getString(R.string.divergence));
                setFragment(ExpensesFragment.newInstance(), ft);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarTitle(getResources().getString(R.string.app_name));
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        setFragment(HomeFragment.newInstance(), ft);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //set action bar title for fragment
    private void setActionBarTitle(String s) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(s);
        }
    }

    //replace frame layout in activity_main.xml to fragment
    private void setFragment(Fragment fragment, FragmentTransaction ft) {
        ft.replace(R.id.fr_holder, fragment);
        ft.commit();
    }
}
