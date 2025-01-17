package id.technow.solve.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.technow.solve.Model.OptionModel;
import id.technow.solve.Model.QuestionModel;
import id.technow.solve.Model.ResponseQuestion;
import id.technow.solve.QuizActivity;
import id.technow.solve.QuizActivity_viewpager;
import id.technow.solve.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionVH> {
    private int mSelectedItem = -1;
    private List<OptionModel> optionModel;
    private Context mCtx;
    private QuestionModel question;
    QuizViewPagerAdapter adapter;
    String link = "http://185.210.144.115:8080/storage/answer/";
    String content;
    String[] strings;
    private boolean onBind;

    public OptionsAdapter(List<OptionModel> optionModel, Context mCtx, QuestionModel question) {
        this.optionModel = optionModel;
        this.mCtx = mCtx;
        this.question = question;
    }

    @NonNull
    @Override
    public OptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_option, parent, false);
        OptionsAdapter.OptionVH holder = new OptionsAdapter.OptionVH(view);
        return new OptionsAdapter.OptionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionVH holder, int position) {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final Gson gson = new Gson();
        final SharedPreferences.Editor editorList = sharedPrefs.edit();
        final OptionModel option = optionModel.get(position);
        holder.id = option.getId();
        content = option.getContents();
        Picasso.get().load(link + option.getId())
                .into(holder.imgOption);
        holder.rbChoose.setChecked(position == mSelectedItem);
        if (option.getContents().length() > 25) {
            holder.jawaban.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCtx.getResources().getDimension(R.dimen._14ssp));
        } else if (option.getContents().length() > 30) {
            holder.jawaban.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCtx.getResources().getDimension(R.dimen._13ssp));
        } else {
            holder.jawaban.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCtx.getResources().getDimension(R.dimen._15ssp));
        }
        holder.jawaban.setText(option.getContents() + " ");
        String choosen = sharedPrefs.getString("id-" + option.getQuestion_id(), "question");
        if (position == mSelectedItem) {
            holder.placeA.setCardBackgroundColor(Color.parseColor("#64b5f6"));
            if (!choosen.equals("question")) {
                if (choosen.equals(option.getOption())) {
                    holder.rbChoose.setChecked(true);
                } else {
                    editorList.putString("id-" + option.getQuestion_id(), option.getOption());
                    holder.placeA.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            } else {

            }
        } else {
            holder.placeA.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            if (!choosen.equals("question")) {
                if (choosen.equals(option.getOption())) {
                    holder.rbChoose.setChecked(true);
                } else {
                    editorList.putString("id-" + option.getQuestion_id(), option.getOption());
                    holder.placeA.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            } else {

            }
        }

      /*  if (!choosen.equals("question")) {
            if (choosen.equals(option.getOption())) {
                holder.rbChoose.setChecked(true);
            } else {
                editorList.putString("id-" + option.getQuestion_id(), option.getOption());
                 holder.placeA.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        } else {

        }*/

        if (holder.rbChoose.isChecked()) {
            holder.placeA.setCardBackgroundColor(Color.parseColor("#64b5f6"));

            String json = sharedPrefs.getString("response", "response");
            Type type = new TypeToken<ResponseQuestion>() {
            }.getType();
            ResponseQuestion responseQuestion = gson.fromJson(json, type);

            String json2 = sharedPrefs.getString("question", "question");
            Type type2 = new TypeToken<ArrayList<QuestionModel>>() {
            }.getType();
            ArrayList<QuestionModel> questionSave = gson.fromJson(json2, type2);


            option.setChoosen("yes");

            editorList.putString("userAnswer", option.getOption());
            editorList.putString("userAnswerContent", option.getContents());
            editorList.putString("id-" + option.getQuestion_id(), option.getOption());

            String questionSt = gson.toJson(questionSave);
            editorList.putString("question", questionSt);

            responseQuestion.setQuestion(questionSave);
            String responseQuiz = gson.toJson(responseQuestion);
            editorList.putString("response", responseQuiz);

            editorList.commit();

            if (mCtx instanceof QuizActivity) {
                ((QuizActivity) mCtx).saveOption();
            }
            if (!choosen.equals("question")) {
                if (choosen.equals(option.getOption())) {
                    holder.rbChoose.setChecked(true);
                } else {
                    editorList.putString("id-" + option.getQuestion_id(), option.getOption());
                    holder.placeA.setCardBackgroundColor(Color.parseColor("#545454"));
                }
            }
            /*if (mCtx instanceof QuizActivity) {
                ((QuizActivity) mCtx).navigationSoal(position+1);
            }*/

        } else {
            holder.placeA.setCardBackgroundColor(Color.parseColor("#545454"));

        }

        if (holder.id == 1) {
          /*  if (mCtx instanceof QuizActivity) {
                ((QuizActivity) mCtx).nextSoalAuto();
            }*/
        } else {

        }

    }

    @Override
    public int getItemCount() {
        return optionModel.size();
    }

    class OptionVH extends RecyclerView.ViewHolder {
        TextView jawaban;
        RadioButton rbChoose;
        public int id;
        CardView placeA;
        br.com.felix.imagezoom.ImageZoom imgOption;

        public OptionVH(@NonNull View itemView) {
            super(itemView);
            jawaban = itemView.findViewById(R.id.jawaban);
            rbChoose = itemView.findViewById(R.id.rbChoose);
            imgOption = itemView.findViewById(R.id.imgOption);
            placeA = itemView.findViewById(R.id.placeA);

            View.OnClickListener l = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, optionModel.size());
                    notifyDataSetChanged();


                }
            };

            itemView.setOnClickListener(l);
            rbChoose.setOnClickListener(l);
            placeA.setOnClickListener(l);

        }
    }


}
