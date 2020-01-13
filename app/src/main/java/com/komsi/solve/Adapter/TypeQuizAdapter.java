package com.komsi.solve.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.komsi.solve.Model.TypeListModel;
import com.komsi.solve.QuizActivity;
import com.komsi.solve.QuizActivity_viewpager;
import com.komsi.solve.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TypeQuizAdapter extends RecyclerView.Adapter<TypeQuizAdapter.QuizVH> {

    private ArrayList<TypeListModel> type;
    List<String> colors;
    private Context mCtx;

    public TypeQuizAdapter(ArrayList<TypeListModel> type, Context mCtx) {
        this.type = type;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public QuizVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quiz_choose, parent, false);
        TypeQuizAdapter.QuizVH holder = new TypeQuizAdapter.QuizVH(view);
        return new TypeQuizAdapter.QuizVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizVH holder, int position) {
        final TypeListModel types = type.get(position);

        holder.titleMenu.setText(types.getTitle());
        holder.tvDesc.setText(types.getDescription());
        holder.tvSum.setText("Jumlah Soal : " + types.getSum_question());
        holder.layoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(mCtx, QuizActivity.class);
                i.putExtra("idCategory", types.getId());
                i.putExtra("Type", types.getPic_url());

                mCtx.startActivity(i);

            }
        });

        holder.tvLetter.setText(types.getTitle().substring(0, 1));

        colors.add("#e51c23");
        colors.add("#e91e63");
        colors.add("#9c27b0");
        colors.add("#673ab7");
        colors.add("#3f51b5");
        colors.add("#5677fc");
        colors.add("#03a9f4");
        colors.add("#00bcd4");
        colors.add("#009688");
        colors.add("#259b24");
        colors.add("#8bc34a");
        colors.add("#cddc39");
        colors.add("#ffeb3b");
        colors.add("#ff9800");
        colors.add("#ff5722");
        colors.add("#795548");
        colors.add("#9e9e9e");
        colors.add("#607d8b");

        Random r = new Random();
        int i1 = r.nextInt(17 - 0) + 0;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);
        draw.setColor(Color.parseColor(colors.get(i1)));
        holder.tvLetter.setBackground(draw);
    }

    @Override
    public int getItemCount() {
        return type.size();
    }

    class QuizVH extends RecyclerView.ViewHolder {
        TextView titleMenu, tvDesc, tvSum, tvLetter;
        LinearLayout layoutCard;
        FrameLayout.LayoutParams params;

        public QuizVH(@NonNull View itemView) {
            super(itemView);
            titleMenu = itemView.findViewById(R.id.titleMenu);
            // layoutCard = itemView.findViewById(R.id.layoutCard);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            tvSum = itemView.findViewById(R.id.tvSum);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            layoutCard = itemView.findViewById(R.id.layoutCard);
//            params = (FrameLayout.LayoutParams) layoutCard.getLayoutParams();

        }
    }
}
