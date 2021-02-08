package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.models.tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class timelineActivity extends AppCompatActivity {
    twitterClient client;
    List<tweet> arr;
    RecyclerView rv;
    adapter Adapter;
    Context context;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        client = twitterAPP.getRestClient(this);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("timeline","fetching new data");
                populateTimeline();
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.bar);

        rv = findViewById(R.id.rvTweets);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        arr = new ArrayList<>();
        context = this;
        Adapter = new adapter(context,arr);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(Adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i("onloadmore",String.format("%d",page));
                loadMoreData();
            }
        };
        rv.addOnScrollListener(scrollListener);
        populateTimeline();
    }

    private void loadMoreData() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("time/LMD","onSuccess for loadMoreData");
                JSONArray array = json.jsonArray;
                try {
                    Adapter.addAll( tweet.fromJsonArray(array));
                    Log.i("time/LMD","onSuccess add all");
                }
                catch(JSONException e){
                    Log.e("timeline/failed", String.valueOf(e));
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("time/LMD","onFailre for loadMoreData "+statusCode);
            }
        },arr.get(arr.size()-1).id);
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("timeline","onSuccess" + json.toString());
                JSONArray array = json.jsonArray;
                try {
                    Adapter.clear();
                    Adapter.addAll( tweet.fromJsonArray(array));
                    swipeContainer.setRefreshing(false);
                    Log.i("timeline","try block");
                }
                catch(JSONException e){
                    Log.e("timeline/failed", String.valueOf(e));
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("timeline","onFailure "+ response);
            }
        });
    }
}