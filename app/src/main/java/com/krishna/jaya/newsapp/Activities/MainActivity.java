package com.krishna.jaya.newsapp.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.krishna.jaya.newsapp.Adapter.Adapter;
import com.krishna.jaya.newsapp.ApiClient;
import com.krishna.jaya.newsapp.Model.Articles;
import com.krishna.jaya.newsapp.Model.Headlines;
import com.krishna.jaya.newsapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etQuery;
    Button btnSearch, btnAboutUs;
    Dialog dialog;
    final String API_KEY = "8e66568d05e040e8b3d293e5c47854a3";
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);

        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);

        dialog = new Dialog(MainActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country = getCountry();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("", country, API_KEY);
            }
        });
        retrieveJson("", country, API_KEY);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etQuery.getText().toString().equals("")) {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson(etQuery.getText().toString(), country, API_KEY);
                        }
                    });
                    retrieveJson(etQuery.getText().toString(), country, API_KEY);
                } else {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson("", country, API_KEY);
                        }
                    });
                    retrieveJson("", country, API_KEY);
                }
            }
        });




    }

    public void retrieveJson(String query, String country, String apiKey) {


        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        if (!etQuery.getText().toString().equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        } else {
            call = ApiClient.getInstance().getApi().getHeadlines(country, apiKey);
        }

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}

    /*

        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });





    public void showDialog(){
        Button btnClose;
        dialog.setContentView(R.layout.about_us_pop_up);
        dialog.show();
        btnClose = dialog.findViewById(R.id.close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
     */
