package id.technow.solve.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.technow.solve.HistoryDetailActivity;
import id.technow.solve.Model.HistoryModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.technow.solve.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryVH> {
    ArrayList<HistoryModel> historyModels;
    Context mCtx;

    public HistoryAdapter(ArrayList<HistoryModel> historyModels, Context mCtx) {
        this.historyModels = historyModels;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public HistoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history, parent, false);

        return new HistoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryVH holder, int position) {
        final HistoryModel history = historyModels.get(position);
        holder.quizName.setText(history.getQuiz().getTitle());
        holder.txtDateTime.setText(history.getCreated_at());
        holder.txtScore.setText(history.getTotal_score() + " ");
        holder.txtTrueAns.setText(history.getTrue_sum() + " ");
        holder.txtSumQues.setText(history.getQuiz().getSum_question() + " ");
        holder.txtFalse.setText(history.getFalse_sum() + " ");

        holder.idHistory = historyModels.get(position).getId();
        holder.gameName = historyModels.get(position).getQuiz().getTitle();

        holder.historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mCtx, HistoryDetailActivity.class);
                i.putExtra("idHistory", holder.idHistory);
                i.putExtra("gameName", holder.gameName);
                mCtx.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }


    class HistoryVH extends RecyclerView.ViewHolder {
        private TextView quizName, txtDateTime, txtScore, txtTrueAns, txtSumQues, txtFalse;
        private ImageView imgLiveReport;
        private int idHistory;
        private String gameName;
        LinearLayout historyLayout;

        public HistoryVH(@NonNull View itemView) {
            super(itemView);
            historyLayout = itemView.findViewById(R.id.historyLayout);
            quizName = itemView.findViewById(R.id.quizName);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtTrueAns = itemView.findViewById(R.id.txtTrueAns);
            txtSumQues = itemView.findViewById(R.id.txtSumQues);
            imgLiveReport = itemView.findViewById(R.id.imgLiveReport);
            txtFalse = itemView.findViewById(R.id.txtFalse);
        }

    }
}