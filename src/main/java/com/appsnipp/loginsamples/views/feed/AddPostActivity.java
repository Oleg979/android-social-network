package com.appsnipp.loginsamples.views.feed;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.models.feed.Post;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class AddPostActivity extends AppCompatActivity {
    @BindView(R.id.editTextTitle) EditText inputTitle;
    @BindView(R.id.editTextBody) EditText inputBody;
    @BindView(R.id.createButton) Button createButton;

    private ApiService apiService = HttpClient.getApiService();
    private Disposable subscription;

    @OnClick(R.id.resetForm) void resetForm() {
        this.inputTitle.setText("");
        this.inputBody.setText("");
    }

    @OnClick(R.id.createButton) void login() {
        Log.i("Add Post Activity", "adding post");
        String title = inputTitle.getText().toString();
        String body =  inputBody.getText().toString();
        if(title.trim().isEmpty() || body.trim().isEmpty()) {
            this.showErrorDialog("Заполните все поля!");
            return;
        }
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("dd.MM.yy").format(new Date());
        Post post = new Post(null, Storage.currentUser, title, body, null, date);
        this.createButton.setText("Создание...");
        subscription = this.apiService
                .addPost(post)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .doFinally(() -> {
                    this.createButton.setText("Создать");
                })
                .subscribe(createdPost -> {
                        resetForm();
                        Intent intent = new Intent(this, PostListActivity.class);
                        startActivity(intent);
                }, error -> {
                    Log.i("Add Post Activity", error.toString());
                    this.showErrorDialog(error.getMessage());
                });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
        builder.setMessage("Ошибка создания поста: " + message)
                .setPositiveButton("Ok", (dialog, id) -> {

                });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null) {
            subscription.dispose();
        }
    }
}
