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
import android.util.Log;
import android.view.MenuItem;
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

public class NewMemberActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView mTvBirthday;
    public static final String DATEPICKER_TAG = "datepicker";
    private static final String DIRNAME = "avatarsDir";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 3;
    private Date mBirthday;
    private AppDB mDB;
    private EditText mEtFirstName, mEtSecondName, mEtPhoneNumber;
    private ImageView mAvatar;
    private RxPermissions mRxPermissions;
    private Uri mCurrentAvatarUri;
    private Uri mImageUri;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        Context that = this;
        mRxPermissions = new RxPermissions(this);

        //Init EditTexts
        mEtFirstName = findViewById(R.id.et_first_name);
        mEtSecondName = findViewById(R.id.et_second_name);
        mEtPhoneNumber = findViewById(R.id.et_phone_number);
        mAvatar = findViewById(R.id.iv_member_avatar);

        //Init DB
        mDB = AppDB.getsInstance(this);

        //set action bar title
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.new_member);
        }

        //create calendar and DatePickerDialog to select a date
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvBirthday.setOnClickListener(v -> {
            datePickerDialog.setVibrate(isVibrate());
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        });

        //avatar onclick implementation
        mAvatar.setOnClickListener(this::onAvatarClick);

        //save button implementation
        findViewById(R.id.save_new_member_btn).setOnClickListener(v -> saveButtonOnclick(that));
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mBirthday = calendar.getTime();
        mTvBirthday.setText(new SimpleDateFormat("dd/MM/yyy").format(mBirthday));
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
                    mCurrentAvatarUri = CropImage.getActivityResult(data).getUri();
                    mAvatar.setImageURI(null);
                    mAvatar.setImageURI(mCurrentAvatarUri);
                }
        }
    }

    @SuppressLint("CheckResult")
    private void saveButtonOnclick(Context context) {
        //Get text from all fields
        String firstName = mEtFirstName.getText().toString();
        String secondName = mEtSecondName.getText().toString();
        String stringBirthday = mTvBirthday.getText().toString();
        String strintPhoneNumber = mEtPhoneNumber.getText().toString();
        //reset all fields errors
        mEtFirstName.setError(null);
        mEtSecondName.setError(null);
        mTvBirthday.setError(null);
        mEtPhoneNumber.setError(null);
        // empty fields validation
        if (firstName.isEmpty() || secondName.isEmpty() || stringBirthday.equals("Birthday") || strintPhoneNumber.isEmpty()) {
            if (firstName.isEmpty()) {
                mEtFirstName.setError(getString(R.string.this_field_is_empty));
            }
            if (secondName.isEmpty()) {
                mEtSecondName.setError(getString(R.string.this_field_is_empty));
            }
            if (stringBirthday.equals("Birthday")) {
                mTvBirthday.setError(getString(R.string.this_field_is_empty));
            }
            if (strintPhoneNumber.isEmpty()) {
                mEtPhoneNumber.setError(getString(R.string.this_field_is_empty));
            }
        } else {
            //Create new thread to save data on DB
            try {
                mDB.getMemberRepo().insertAll(new Member(firstName, secondName, mBirthday.getTime(), strintPhoneNumber, saveAvatarToInternalStorage()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            //Successfully data saved message
                            Toast.makeText(context, R.string.new_member_success, Toast.LENGTH_LONG).show();

                            // Clear all fileds and errors
                            mEtFirstName.setError(null);
                            mEtSecondName.setError(null);
                            mTvBirthday.setError(null);
                            mEtPhoneNumber.setError(null);
                            mEtFirstName.setText("");
                            mEtSecondName.setText("");
                            mTvBirthday.setText(R.string.birthday);
                            mEtPhoneNumber.setText("");
                            mAvatar.setImageResource(R.drawable.ic_round_account_button_with_user_inside);
                            mCurrentAvatarUri = null;
                        }, Throwable::printStackTrace);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private long saveAvatarToInternalStorage() throws IOException {
        if (mCurrentAvatarUri != null) {
            long timeIdent = new Date().getTime();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentAvatarUri);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory  = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
            File mypath = new File(directory, timeIdent + ".jpg");
            OutputStream fos = null;

            try {
                fos = new FileOutputStream(mypath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
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

    private boolean isVibrate() {
        return true;
    }
}
