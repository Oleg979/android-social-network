package com.appsnipp.loginsamples.views.chats;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ScrollView;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.models.chat.Message;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;
import com.appsnipp.loginsamples.services.chats.MessageListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class MessageListActivity extends AppCompatActivity {
    @BindView(R.id.reyclerview_message_list) RecyclerView mMessageRecycler;
    @BindView(R.id.edittext_chatbox) EditText messageEditText;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.scrollView) ScrollView scrollView;

    private MessageListAdapter mMessageAdapter;
    private ApiService apiService = HttpClient.getApiService();
    private List<Disposable> subscriptions = new ArrayList<>();
    private String chatId = Storage.currentChat;
    private String currentUser = Storage.currentUser;
    private String companion;

    @OnClick(R.id.updateMessages)
    public void updateMessages() {
        toolbar.setTitle("Обновление...");
        fetchMessages();
    }

    @OnClick(R.id.button_chatbox_send)
    public void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) {
            this.showErrorDialog("Сообщение не может быть пустым!");
            return;
        }
        toolbar.setTitle("Отправка...");
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("HH:mm").format(new Date());
        Message message = new Message(text, currentUser, companion, date);
        subscriptions.add(
                this.apiService
                .postMessageToChat(chatId, message)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribe(response -> {
                    Log.i("Successful posting", chatId);
                    messageEditText.setText("");
                    fetchMessages();
                }, error -> {
                    Log.i("Chat Activity", error.toString());
                    this.showErrorDialog(error.getMessage());
                })
        );
    }

    public void fetchMessages() {
        subscriptions.add(
                this.apiService
                .getChatById(chatId)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribe(response -> {
                        Log.i("Successful fetching", chatId);
                        if (response.getUser1().equals(currentUser)) {
                            companion = response.getUser2();
                        } else {
                            companion = response.getUser1();
                        }
                        toolbar.setTitle("Диалог с " + companion);
                        List<Message> list = Arrays.asList(response.getMessages());
                        Collections.reverse(list);
                        mMessageAdapter = new MessageListAdapter(this, list);
                        mMessageRecycler.setAdapter(mMessageAdapter);
                }, error -> {
                    Log.i("Chat Activity", error.toString());
                    this.showErrorDialog(error.getMessage());
                })
        );
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageListActivity.this);
        builder.setMessage("Ошибка чата: " + message)
                .setPositiveButton("Ok", (dialog, id) -> {

                });
        builder.create().show();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        fetchMessages();
        messageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
               mMessageRecycler.setPadding(0, 150, 0, 0);
            } else {
               mMessageRecycler.setPadding(0, 0, 0, 0);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Disposable::dispose);
    }
}
