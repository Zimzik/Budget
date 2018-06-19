package com.example.zimzik.budget.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.AppDB;
import com.example.zimzik.budget.data.db.models.Member;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EditMemberActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText mEtFirstName, mEtSecondName, mEtPhoneNumber;
    private TextView mTvBirthday;
    private ImageView mAvatar;
    private Member mMember;
    private AppDB mDB;
    private Date mBirthday;
    private Gson mGson = new Gson();
    public static final String DATEPICKER_TAG = "datepicker";
    private static final String DIRNAME = "avatarsDir";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 3;
    private RxPermissions mRxPermissions;
    private Uri mCurrentAvatarUri;
    private Uri mNewAvatarUri;
    private Uri mImageUri;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.edit_member);
        }

        mDB = AppDB.getsInstance(this);
        mRxPermissions = new RxPermissions(this);

        //define all views
        mEtFirstName = findViewById(R.id.et_em_first_name);
        mEtSecondName = findViewById(R.id.et_em_second_name);
        mEtPhoneNumber = findViewById(R.id.et_em_phone_number);
        mTvBirthday = findViewById(R.id.tv_em_birthday);
        mAvatar = findViewById(R.id.iv_member_avatar_edit);

        //set datepicker
        setDatePicker(savedInstanceState);
        //set all field from current member information
        setAllFields();

        findViewById(R.id.save_changes_em_btn).setOnClickListener(v -> saveChanges());
        mAvatar.setOnClickListener(this::onAvatarClick);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    croppPicture(mImageUri);
                }
                break;

            case PICK_FROM_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    croppPicture(selectedImage);
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mNewAvatarUri = CropImage.getActivityResult(data).getUri();
                    mAvatar.setImageURI(null);
                    mAvatar.setImageURI(mNewAvatarUri);
                }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mBirthday = calendar.getTime();
        mTvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(mBirthday));
    }

    private void setAllFields() {
        mMember = mGson.fromJson(getIntent().getStringExtra("member"), Member.class);
        mBirthday = new Date(mMember.getBirthday());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
        mTvBirthday.setText(sdf.format(mBirthday));
        mEtFirstName.setText(mMember.getFirstName());
        mEtSecondName.setText(mMember.getLastName());
        mEtPhoneNumber.setText(String.valueOf(mMember.getPhoneNumber()));
        setAvatarIcon();
    }

    private void setAvatarIcon() {
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath(), mMember.getTimeIdent() + ".jpg");
        if (file.exists()) {
            mCurrentAvatarUri = Uri.fromFile(file);
            mAvatar.setImageURI(mCurrentAvatarUri);
        } else {
            mAvatar.setImageResource(R.drawable.ic_round_account_button_with_user_inside);
        }
    }

    private void setDatePicker(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        mTvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });
    }

    @SuppressLint("CheckResult")
    private void saveChanges() {
        String firstName = mEtFirstName.getText().toString();
        String secondName = mEtSecondName.getText().toString();
        String stringPhoneNumber = mEtPhoneNumber.getText().toString();
        mEtFirstName.setError(null);
        mEtSecondName.setError(null);
        mEtPhoneNumber.setError(null);

        if (firstName.isEmpty() || secondName.isEmpty() || stringPhoneNumber.isEmpty()) {
            if (firstName.isEmpty()) {
                mEtFirstName.setError(getString(R.string.this_field_is_empty));
            }
            if (secondName.isEmpty()) {
                mEtSecondName.setError(getString(R.string.this_field_is_empty));
            }
            if (stringPhoneNumber.isEmpty()) {
                mEtPhoneNumber.setError(getString(R.string.this_field_is_empty));
            }
        } else {
            mMember.setFirstName(firstName);
            mMember.setLastName(secondName);
            mMember.setPhoneNumber(Long.valueOf(stringPhoneNumber));
            mMember.setBirthday(mBirthday.getTime());
            try {
                mMember.setTimeIdent(saveAvatarToInternalStorage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDB.getMemberRepo().update(mMember)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Intent intent = new Intent();
                        String newMember = mGson.toJson(mMember);
                        intent.putExtra("mMember", newMember);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(this, getString(R.string.information_successfully_updated), Toast.LENGTH_LONG).show();
                        finish();
                    });
        }
    }

    private long saveAvatarToInternalStorage() throws IOException {
        if (mNewAvatarUri != null) {
            File oldAvatarFile = new File(mCurrentAvatarUri.getPath());
            oldAvatarFile.delete();
            long timeIdent = new Date().getTime();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mNewAvatarUri);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory  = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
            File mypath = new File(directory, timeIdent + ".jpg");
            OutputStream fos = null;

            try {
                fos = new FileOutputStream(mypath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                Toast.makeText(this, "Saved to: " + directory.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return timeIdent;
        } else {
            return 0;
        }
    }

    private void onAvatarClick(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.avatar_context_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.from_gallery_menu_item: {
                    getImageFromGallary();
                }
                break;

                case R.id.from_camera_menu_item: {
                    getImageFromCamera();
                }
                break;
            }
            return true;
        });
        popupMenu.show();
    }

    @SuppressLint("CheckResult")
    private void getImageFromGallary() {
        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLARY);
                    } else {
                        Toast.makeText(this, R.string.access_denied, Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(
                                this,
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void getImageFromCamera() {
        mRxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mImageUri = Uri.fromFile(new File((Environment.getExternalStorageDirectory()), "ava.jpeg"));
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                    } else {
                        Toast.makeText(this, getString(R.string.access_denied), Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, PICK_FROM_CAMERA);
                    }
                });
    }

    private void croppPicture(Uri uri) {
        CropImage.activity(uri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .start(this);
    }

    private boolean isVibrate() {
        return true;
    }

}
