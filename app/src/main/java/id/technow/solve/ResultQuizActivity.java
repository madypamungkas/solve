package id.technow.solve;

import androidx.appcompat.app.AppCompatActivity;
import id.technow.solve.Model.UserModel;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.technow.solve.Model.ResponsePostAnswer;

import id.technow.solve.R;

import id.technow.solve.Storage.SharedPrefManager;

import java.lang.reflect.Type;

public class ResultQuizActivity extends AppCompatActivity {
    TextView point, textCorrect, textSumQues;
    MaterialButton play, btn_home, btn_pembahasan;
    UserModel user = SharedPrefManager.getInstance(this).getUser();
    int total_score, idquiz;
    String namaSoal, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_quiz);
        idquiz = getIntent().getIntExtra("idsoal", 1);
        point = findViewById(R.id.point);
        namaSoal = getIntent().getStringExtra("namaSoal");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ResultQuizActivity.this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("answerPost", "answerPost");
        //  String json = getArguments().getString("question");
        Type type = new TypeToken<ResponsePostAnswer>() {
        }.getType();
        ResponsePostAnswer responseAnswer = gson.fromJson(json, type);


        point.setText(responseAnswer.getResult().getTotal_score() + "  pts");
        total_score = getIntent().getIntExtra("points", 0);
        textSumQues = findViewById(R.id.textSumQues);
        textCorrect = findViewById(R.id.textCorrect);
        String correct = String.valueOf(responseAnswer.getResult().getTrue_sum());
        int sum = getIntent().getIntExtra("sum", 10);
        textCorrect.setText(correct+" ");
        category = getIntent().getStringExtra("category");
        textSumQues.setText(responseAnswer.getResult().getAnswer_save().size() + " Pertanyaan");
        btn_pembahasan = findViewById(R.id.btn_pembahasan);
        btn_pembahasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultQuizActivity.this, PembahasanQuizActivity.class);
                intent.putExtra("idsoal", idquiz);
                intent.putExtra("namaSoal", namaSoal);
                startActivity(intent);
            }
        });

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultQuizActivity.this, Main2Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        play = findViewById(R.id.btn_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(ResultQuizActivity.this, QuizActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("category", category);
                intent.putExtra("namaSoal", getIntent().getStringExtra("namaSoal") + " ");
                intent.putExtra("idsoal", idquiz);
                Toast.makeText(ResultQuizActivity.this, idquiz+" ", Toast.LENGTH_LONG).show();
                intent.putExtra("namaSoal", namaSoal);
                startActivity(intent);*/
            }


        });
    }

    @Override
    public void onBackPressed() {
        confirmOnBackPressed();
    }

    private void confirmOnBackPressed() {

        final Dialog dialog = new Dialog(ResultQuizActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_confirmation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        FloatingActionButton mDialogNo = dialog.findViewById(R.id.fbNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        FloatingActionButton mDialogOk = dialog.findViewById(R.id.fbYes);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent play = new Intent(ResultQuizActivity.this, TypeChooseActivity.class);
                startActivity(play);
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }
}