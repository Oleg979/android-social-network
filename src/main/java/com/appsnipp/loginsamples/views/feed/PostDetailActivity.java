package com.appsnipp.loginsamples.views.feed;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class PostDetailActivity extends AppCompatActivity {
    @BindView(R.id.fab) FloatingActionButton likeButton;
    @BindView(R.id.delete) FloatingActionButton deleteButton;

    private boolean isLiked = false;
    private ApiService apiService = HttpClient.getApiService();
    private List<Disposable> subscriptions = new ArrayList<>();

    @OnClick(R.id.delete)
    public void deletePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder
                .setMessage("Вы уверены, что хотите удалить этот пост?")
                .setPositiveButton("Да", (dialog, id) -> {
                    subscriptions.add(apiService
                            .deletePostById(Storage.currentPost)
                            .observeOn(SchedulerProvider.ui())
                            .subscribeOn(SchedulerProvider.io())
                            .subscribe(post -> {
                                Intent intent = new Intent(this, PostListActivity.class);
                                startActivity(intent);
                            })
                    );
                })
                .setNegativeButton("Нет", (dialog, id) -> {

                });
        builder.create().show();
    }

    @OnClick(R.id.fab)
    public void like() {
        displaySnack(isLiked ? "Вам больше не нравится!" : "Вам понравилось!");
        isLiked = !isLiked;
        likeButton.setBackgroundTintList(ColorStateList.valueOf(isLiked ? Color.RED : Color.GRAY));
        subscriptions.add(apiService
                .like(Storage.currentPost, Storage.currentUser)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribe(post -> {
                    Storage.updatePostSubject.onNext(post);
                }));
    }

    public void displaySnack(String text) {
        Snackbar
                .make(likeButton, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);
        subscriptions.add(Storage.updatePostSubject.subscribe(post -> {
            isLiked = Arrays.asList(post.getLikes()).contains(Storage.currentUser);
            deleteButton.setVisibility(post.getUsername().equals(Storage.currentUser) ? View.VISIBLE : View.INVISIBLE);
            likeButton.setBackgroundTintList(ColorStateList.valueOf(isLiked ? Color.RED : Color.GRAY));
        }));
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            PostDetailFragment fragment = new PostDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.post_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, PostListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Disposable::dispose);
    }
}
