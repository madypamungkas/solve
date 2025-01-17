package id.technow.solve;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import id.technow.solve.Adapter.HomeItemAdapter;
import id.technow.solve.Adapter.LeaChooseTypeAdapter;
import id.technow.solve.Adapter.LeadChooseCategoryAdapter;
import id.technow.solve.Api.RetrofitClient;
import id.technow.solve.Model.CategoryModel;
import id.technow.solve.Model.MenuHomeModel;
import id.technow.solve.Model.ResponseCategory;
import id.technow.solve.Model.ResponseMenuHome;
import id.technow.solve.Model.UserModel;
import id.technow.solve.Storage.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardChooseCategory extends AppCompatActivity {
    private ArrayList<MenuHomeModel> categories;
    private RecyclerView typeRV;
    LeadChooseCategoryAdapter adapter;
    int idCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_choose_category);
        typeRV = findViewById(R.id.typeRV);
        typeRV.setLayoutManager(new LinearLayoutManager(LeaderboardChooseCategory.this));
        idCategory = getIntent().getIntExtra("idCategory", 1);
        getListCategory();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LeaderboardChooseCategory.this, Main2Activity.class));
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3952453830525109/1168174801");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }


    public void getListCategory() {
        UserModel user = SharedPrefManager.getInstance(this).getUser();
        String token = "Bearer " + user.getToken();
        String school = user.getSchool_id();

        Call<ResponseMenuHome> call = RetrofitClient.getInstance().getApi().category(token, "application/json", school);
        call.enqueue(new Callback<ResponseMenuHome>() {
            @Override
            public void onResponse(Call<ResponseMenuHome> call, Response<ResponseMenuHome> response) {
                ResponseMenuHome model = response.body();
                if (response.isSuccessful()) {
                    int size = model.getResult().size();
                    categories = response.body().getResult();/*

                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL);*/
                    if (categories.isEmpty()) {
                        Toast.makeText(LeaderboardChooseCategory.this, "Bidang Tidak Tersedia", Toast.LENGTH_SHORT).show();

                    } else {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LeaderboardChooseCategory.this, RecyclerView.VERTICAL, false);
                        adapter = new LeadChooseCategoryAdapter(LeaderboardChooseCategory.this, categories);
                        typeRV.setLayoutManager(new LinearLayoutManager(LeaderboardChooseCategory.this));
                        typeRV.setLayoutManager(linearLayoutManager);
                        typeRV.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(LeaderboardChooseCategory.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMenuHome> call, Throwable t) {
                Toast.makeText(LeaderboardChooseCategory.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
