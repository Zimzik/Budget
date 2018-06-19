package com.example.zimzik.budget.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Member;
import com.google.gson.Gson;

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
    private static final String KEY_MEMBER = "KEY_MEMBER";
    private static final String DIRNAME = "avatarsDir";

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_member_info, container, false);
        mAvatar = view.findViewById(R.id.iv_user_avatar);
        mTextView = view.findViewById(R.id.cm_tv_name);
        mTvAge = view.findViewById(R.id.cm_tv_age);
        mTvPhoneNumber = view.findViewById(R.id.cm_tv_phone_number);
        setAllFields(mMember);
        return view;
    }

    private void setAllFields(Member member) {
        String name = member.getLastName() + " " + member.getFirstName();
        String age = String.format("%s (%d)", new SimpleDateFormat("dd.MM.yyyy").format(new Date(member.getBirthday())).toString(), MemberListFragment.calculateAge(member.getBirthday()));
        long phoneNumber = member.getPhoneNumber();
        setAvatarIcon(member);
        mTextView.setText(name);
        mTvAge.setText(age);
        mTvPhoneNumber.setText(String.valueOf(phoneNumber));
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
}
