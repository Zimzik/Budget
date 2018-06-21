package com.example.zimzik.budget.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Member;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentMemberInfoFragment extends Fragment {

    private Member mMember;
    private TextView mTextView;
    private TextView mTvAge;
    private TextView mTvPhoneNumber;
    private ImageView mAvatar;
    private final Gson mGson = new Gson();
    private static final String KEY_MEMBER = "member";
    private static final String DIRNAME = "avatarsDir";
    private static final int CALL_PHONE = 1;
    private RxPermissions mRxPermissions;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CALL_PHONE
    };

    public CurrentMemberInfoFragment() {
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMember = mGson.fromJson(getArguments().getString(KEY_MEMBER), Member.class);
        }
        mRxPermissions = new RxPermissions(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_member_info, container, false);
        mAvatar = view.findViewById(R.id.iv_user_avatar);
        mTextView = view.findViewById(R.id.cm_tv_name);
        mTvAge = view.findViewById(R.id.cm_tv_age);
        mTvPhoneNumber = view.findViewById(R.id.cm_tv_phone_number);
        mTvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                mRxPermissions
                        .request(Manifest.permission.CALL_PHONE)
                        .subscribe(granted -> {
                            if (granted) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMember.getPhoneNumber()));
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), R.string.sorry_cannot_call, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        setAllFields(mMember);
        return view;
    }

    private void setAllFields(Member member) {
        String name = member.getLastName() + " " + member.getFirstName();
        String age = String.format("%s (%d)", new SimpleDateFormat("dd.MM.yyyy").format(new Date(member.getBirthday())).toString(), MemberListFragment.calculateAge(member.getBirthday()));
        String phoneNumber = member.getPhoneNumber();
        setAvatarIcon(member);
        mTextView.setText(name);
        mTvAge.setText(age);
        mTvPhoneNumber.setText(phoneNumber);
    }

    public static CurrentMemberInfoFragment newInstance(Member member) {
        CurrentMemberInfoFragment fragment = new CurrentMemberInfoFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(KEY_MEMBER, gson.toJson(member));
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setAvatarIcon(Member member) {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath(), member.getTimeIdent() + ".jpg");
        if (file.exists()) {
            mAvatar.setImageURI(Uri.fromFile(file));
        } else {
            mAvatar.setImageResource(R.drawable.ic_round_account_button_with_user_inside);
        }
    }

    public void refreshData(Member member) {
        mMember = member;
        setAllFields(mMember);
    }
}
