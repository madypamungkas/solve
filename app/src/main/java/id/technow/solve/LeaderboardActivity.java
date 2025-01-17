package id.technow.solve;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.technow.solve.Api.RetrofitClient;
import id.technow.solve.Model.LeaderboarModel;
import id.technow.solve.Model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import id.technow.solve.Adapter.LeaderboardAdapter;
import id.technow.solve.Model.ResponseLeaderboard;

import id.technow.solve.R;

import id.technow.solve.Storage.SharedPrefManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView leaders;
    private LeaderboardAdapter adapter;
    private List<LeaderboarModel> leaModel;
    private List<LeaderboarModel> leaModelPodium;
    int idQuiz;
    CircleImageView avatar3, avatar2, avatar1;
    UserModel user = SharedPrefManager.getInstance(this).getUser();
    ProgressDialog loading;
    TextView name1, name2, name3, score1, score2, score3, gameName;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        leaders = findViewById(R.id.leaders);
        leaders.setLayoutManager(new LinearLayoutManager(this));
        name1 = findViewById(R.id.name1);
        name2 = findViewById(R.id.name2);
        name3 = findViewById(R.id.name3);
        score1 = findViewById(R.id.score1);
        score2 = findViewById(R.id.score2);
        score3 = findViewById(R.id.score3);
        gameName = findViewById(R.id.gameName);
        gameName.setText(getIntent().getStringExtra("namasoal"));
        idQuiz = getIntent().getIntExtra("idsoal", 1);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
         mInterstitialAd = new InterstitialAd(this);
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


        //loadData1();
        loadData();

        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

    }

    public void loadData() {
        loading = ProgressDialog.show(LeaderboardActivity.this, null, "Loading...", true, false);
        String token = "Bearer " + user.getToken();
        Call<ResponseLeaderboard> call = RetrofitClient.getInstance().getApi().getnonPodium(token, "application/json", idQuiz);
        call.enqueue(new Callback<ResponseLeaderboard>() {
            @Override
            public void onResponse(Call<ResponseLeaderboard> call, Response<ResponseLeaderboard> response) {
                loading.dismiss();
                if (response.isSuccessful()) {
                    leaModel = response.body().getResult();
                    adapter = new LeaderboardAdapter(LeaderboardActivity.this, leaModel);
                    leaders.setAdapter(adapter);
                } else {
              //      Toast.makeText(LeaderboardActivity.this, response.code() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLeaderboard> call, Throwable t) {
                loading.dismiss();
             //   Toast.makeText(LeaderboardActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
        //String token = "Bearer " + user.getToken();
        Call<ResponseLeaderboard> call2 = RetrofitClient.getInstance().getApi().getPodium(token, "application/json", idQuiz);
        call2.enqueue(new Callback<ResponseLeaderboard>() {
            @Override
            public void onResponse(Call<ResponseLeaderboard> call2, Response<ResponseLeaderboard> response) {
                loading.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getResult().size() == 0) {

                    } else if (response.body().getResult().size() == 1) {
                        leaModel = response.body().getResult();
                        leaModelPodium = response.body().getResult();
                        LeaderboarModel model1, model2, model3;
                        model1 = leaModelPodium.get(0);
                        name1.setText(model1.getUsername());
                        score1.setText(model1.getTotal_score());
                        String linkdefault = "http://185.210.144.115:8080/storage/";
                        String link1 = linkdefault + "user/" + model1.getUser_id();
                        Picasso.get().load(link1).error(R.drawable.ic_userprofile)
                                .into(avatar1);
                    } else if (response.body().getResult().size() == 2) {
                        leaModel = response.body().getResult();
                        leaModelPodium = response.body().getResult();
                        LeaderboarModel model1, model2, model3;
                        model1 = leaModelPodium.get(0);
                        model2 = leaModelPodium.get(1);
                        name1.setText(model1.getUsername());
                        name2.setText(model2.getUsername());
                        score1.setText(model1.getTotal_score());
                        score2.setText(model2.getTotal_score());
                        String linkdefault = "http://185.210.144.115:8080/storage/";
                        String link1 = linkdefault + "user/" + model1.getUser_id();
                        String link2 = linkdefault + "user/" + model2.getUser_id();
                        Picasso.get().load(link1).error(R.drawable.ic_userprofile)
                                .into(avatar1);
                        Picasso.get().load(link2).error(R.drawable.ic_userprofile)
                                .into(avatar2);
                    } else {
                        leaModel = response.body().getResult();
                        leaModelPodium = response.body().getResult();
                        LeaderboarModel model1, model2, model3;
                        model1 = leaModelPodium.get(0);
                        model2 = leaModelPodium.get(1);
                        model3 = leaModelPodium.get(2);
                        name1.setText(model1.getUsername());
                        name2.setText(model2.getUsername());
                        name3.setText(model3.getUsername());
                        score1.setText(model1.getTotal_score());
                        score2.setText(model2.getTotal_score());
                        score3.setText(model3.getTotal_score());
                        String linkdefault = "http://185.210.144.115:8080/storage/";
                        String link1 = linkdefault + "user/" + model1.getUser_id();
                        String link2 = linkdefault + "user/" + model2.getUser_id();
                        String link3 = linkdefault + "user/" + model3.getUser_id();
                        Picasso.get().load(link1).error(R.drawable.ic_userprofile)
                                .into(avatar1);
                        Picasso.get().load(link2).error(R.drawable.ic_userprofile)
                                .into(avatar2);
                        Picasso.get().load(link3).error(R.drawable.ic_userprofile)
                                .into(avatar3);
                    }
                } else {
             //       Toast.makeText(LeaderboardActivity.this, response.code() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLeaderboard> call2, Throwable t) {
                loading.dismiss();
                Toast.makeText(LeaderboardActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
