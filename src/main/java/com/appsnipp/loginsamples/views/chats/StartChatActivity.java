package com.appsnipp.loginsamples.views.chats;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.models.chat.Message;
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

public class StartChatActivity extends AppCompatActivity {
    @BindView(R.id.editTextTitle)
    EditText inputTitle;
    @BindView(R.id.editTextBody) EditText inputBody;
    @BindView(R.id.createButton)
    Button createButton;

    private ApiService apiService = HttpClient.getApiService();
    private Disposable subscription;

    @OnClick(R.id.resetForm) void resetForm() {
        this.inputTitle.setText("");
        this.inputBody.setText("");
    }

    @OnClick(R.id.createButton) void login() {
        Log.i("Start Chat Activity", "starting chat");
        String person = inputTitle.getText().toString();
        String body =  inputBody.getText().toString();
        if(person.trim().isEmpty() || body.trim().isEmpty()) {
            this.showErrorDialog("Заполните все поля!");
            return;
        }
        this.createButton.setText("Создание...");
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("HH:mm").format(new Date());
        Message message = new Message(body, Storage.currentUser, person, date);
        subscription = this.apiService
                .startChatBetween(Storage.currentUser, person)
                .switchMap(response -> apiService.postMessageToChat(response.getChatId(), message))
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .doFinally(() -> this.createButton.setText("Начать диалог"))
                .subscribe(chat -> {
                    resetForm();
                    Intent intent = new Intent(this, ChatListActivity.class);
                    startActivity(intent);
                }, error -> {
                    Log.i("Start Chat Activity", error.toString());
                    this.showErrorDialog(error.getMessage());
                });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartChatActivity.this);
        builder.setMessage("Ошибка создания диалога: " + message)
                .setPositiveButton("Ok", (dialog, id) -> {

                });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);
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
