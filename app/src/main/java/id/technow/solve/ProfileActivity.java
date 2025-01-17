package id.technow.solve;

import androidx.appcompat.app.AppCompatActivity;
import id.technow.solve.Api.RetrofitClient;
import id.technow.solve.Model.DetailUser;
import id.technow.solve.Model.ResponseDetails;
import id.technow.solve.Model.ResponseProfile;
import id.technow.solve.Model.UserModel;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import id.technow.solve.R;

import id.technow.solve.Storage.SharedPrefManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class ProfileActivity extends AppCompatActivity {
    TextView username, email, highScore, gamePlayed, txtSchools;
    FrameLayout frame;
    Context context;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap, dstBmp;
    ProgressDialog loading;
    TextView editProfile, uploadProfile;
    ProgressDialog progress;
    DetailUser detailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UserModel user = SharedPrefManager.getInstance(ProfileActivity.this).getUser();
        String token = "Bearer " + user.getToken();

        highScore = findViewById(R.id.highScore);
        gamePlayed = findViewById(R.id.gamePlayed);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        editProfile = findViewById(R.id.editProfile);
        uploadProfile = findViewById(R.id.uploadProfile);
        txtSchools = findViewById(R.id.txtSchools);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        Gson gson = new Gson();

        String json = sharedPrefs.getString("DetailUser", "DetailUser");
        Type type = new TypeToken<ResponseDetails>() {
        }.getType();
        ResponseDetails responseDetails = gson.fromJson(json, type);

        DetailUser detail = responseDetails.getUser();

        txtSchools.setText(detail.getSchool().getName());

        if (detail.getHigh_score() == null) {
            highScore.setText("0");
        } else {
            highScore.setText(detail.getHigh_score() + " ");
        }
        gamePlayed.setText(detail.getCount_played() + " ");
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangeProfile.class);
                startActivity(intent) ;
            }
        });
        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFoto();
            }
        });

        frame = findViewById(R.id.frame);
        getFoto();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);

                if (bitmap.getWidth() >= bitmap.getHeight()) {

                    dstBmp = Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                    );

                } else {
                    dstBmp = Bitmap.createBitmap(
                            bitmap,
                            0,
                            bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                            bitmap.getWidth(),
                            bitmap.getWidth()
                    );
                }
                frame.setBackground(new BitmapDrawable(bitmap));
                // uploadProfile.setVisibility(View.VISIBLE);
                uploadFoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String imgToString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        dstBmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        //    bt_uploadFoto.setEnabled(true);
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public void uploadFoto() {
        UserModel user = SharedPrefManager.getInstance(this).getUser();
        String token = "Bearer " + user.getToken();

        progress = ProgressDialog.show(ProfileActivity.this, null, "Loading ...", true, false);

        String image = imgToString();
        retrofit2.Call<ResponseProfile> call = RetrofitClient.getInstance().getApi().updateAvatar(token, image);
        call.enqueue(new Callback<ResponseProfile>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseProfile> call, Response<ResponseProfile> response) {
                if (response.isSuccessful()) {
                    //getDetails();
                    progress.dismiss();
                    updateData();
                    Toast.makeText(ProfileActivity.this, "Foto Profil Berganti", Toast.LENGTH_LONG).show();

                } else {
                    progress.dismiss();
                    Toast.makeText(ProfileActivity.this, response.code() + "Ukuran Foto Terlalu Besar", Toast.LENGTH_LONG).show();

                    //        Toast.makeText(ProfileActivity.this, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseProfile> call, Throwable t) {
                progress.dismiss();
               // Toast.makeText(ProfileActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(ProfileActivity.this, "Ukuran Foto Terlalu Besar 2", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getDetails() {
        UserModel user = SharedPrefManager.getInstance(this).getUser();
        if (user.getToken() != null) {
            String token = "Bearer " + user.getToken();

            retrofit2.Call<ResponseDetails> call = RetrofitClient.getInstance().getApi().detail("application/json", token);
            call.enqueue(new Callback<ResponseDetails>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseDetails> call, Response<ResponseDetails> response) {
                    if (response.isSuccessful()) {
                        SharedPrefManager.getInstance(ProfileActivity.this).saveDetail(response.body().getUser());
                        username.setText(response.body().getUser().getUsername());
                        email.setText(response.body().getUser().getEmail());
                        String defaultLink = getResources().getString(R.string.link);
                        String link = defaultLink + "user/";
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                        SharedPreferences.Editor editorList = sharedPrefs.edit();
                        Gson gson = new Gson();

                        String detailUser = gson.toJson(response.body());
                        editorList.putString("DetailUser", detailUser);
                        editorList.commit();


                    } else {
                        Toast.makeText(ProfileActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseDetails> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDetails();
        getFoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
        getFoto();
    }

    public void updateData() {
        UserModel user = SharedPrefManager.getInstance(this).getUser();
        if (user.getToken() != null) {
            String token = "Bearer " + user.getToken();

            retrofit2.Call<ResponseDetails> call = RetrofitClient.getInstance().getApi().detail("application/json", token);
            call.enqueue(new Callback<ResponseDetails>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseDetails> call, Response<ResponseDetails> response) {
                    if (response.isSuccessful()) {

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                        SharedPreferences.Editor editorList = sharedPrefs.edit();
                        Gson gson = new Gson();

                        String detailUser = gson.toJson(response.body());
                        editorList.putString("DetailUser", detailUser);
                        editorList.commit();

                        SharedPrefManager.getInstance(ProfileActivity.this).saveDetail(response.body().getUser());
                        Intent intent = new Intent(ProfileActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ProfileActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseDetails> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public void getFoto() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        Gson gson = new Gson();

        String json = sharedPrefs.getString("DetailUser", "DetailUser");
        Type type = new TypeToken<ResponseDetails>() {
        }.getType();
        ResponseDetails responseDetails = gson.fromJson(json, type);

        detailUser = responseDetails.getUser();
        username.setText(detailUser.getUsername());
        email.setText(detailUser.getEmail());
        String defaultLink = "http://185.210.144.115:8080/storage/";
        String link = defaultLink + "user/";
        Picasso.get().load(link + detailUser.getId()).error(R.drawable.ic_userprofile)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        frame.setBackground(new BitmapDrawable(ProfileActivity.this.getResources(), bitmap));

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

}

