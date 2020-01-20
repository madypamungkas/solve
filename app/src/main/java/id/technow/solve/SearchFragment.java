package id.technow.solve;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import id.technow.solve.Api.RetrofitClient;
import id.technow.solve.Model.ResponseTypeList;
import id.technow.solve.Model.UserModel;

import id.technow.solve.R;

import id.technow.solve.Storage.SharedPrefManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    View view;
    TextInputLayout layoutSearch;
    TextInputEditText inputSearch;
    Context mContext;
    ProgressDialog loading;

    public SearchFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        mContext = getActivity().getWindow().getContext();

        layoutSearch = view.findViewById(R.id.layoutSearch);
        inputSearch = view.findViewById(R.id.inputSearch);

        inputSearch.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchQuiz();
                    return true;
                }
                return false;
            }
        });

        layoutSearch.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuiz();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = inputSearch.getText().toString();

                if (code.length() > 0) {
                    layoutSearch.setError(null);
                }
            }
        });

        return view;

    }

    public void searchQuiz() {
        inputSearch.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
        loading = ProgressDialog.show(getActivity(), null, getString(R.string.please_wait), true, false);
        String code = inputSearch.getText().toString().trim();

        if (code.isEmpty()) {
            loading.dismiss();
            layoutSearch.setError("Kode Quiz Harus Diisi");
            inputSearch.requestFocus();
            return;
        }

        UserModel user = SharedPrefManager.getInstance(getActivity()).getUser();
        String token = "Bearer " + user.getToken();

        Call<ResponseTypeList> call = RetrofitClient.getInstance().getApi().code(token, "application/json", code);
        call.enqueue(new Callback<ResponseTypeList>() {
            @Override
            public void onResponse(Call<ResponseTypeList> call, Response<ResponseTypeList> response) {
                ResponseTypeList model = response.body();
                loading.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("failed") ) {
                        Toast.makeText(getActivity(), response.body().getMessage() , Toast.LENGTH_LONG).show();
                    } else {
                        Intent i = new Intent(getActivity(), QuizActivity.class);
                        i.putExtra("idsoal", response.body().getResult().get(0).getId());
                        i.putExtra("idsoal", response.body().getResult().get(0).getPic_url());
                        startActivity(i);
                        Toast.makeText(getActivity(), response.body().getStatus(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "eror " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseTypeList> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void checkConnection() {
        if (isNetworkAvailable()) {
            detailUser();
        } else {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_no_internet);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            dialog.getWindow().setLayout((9 * width) / 10, height);

            Button btnRetry = dialog.findViewById(R.id.btnRetry);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    checkConnection();
                }
            });
            dialog.show();
        }
    }

     */

}
