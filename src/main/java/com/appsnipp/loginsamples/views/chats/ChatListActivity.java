package com.appsnipp.loginsamples.views.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsnipp.loginsamples.R;

import com.appsnipp.loginsamples.models.chat.Chat;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;
import com.appsnipp.loginsamples.views.auth.LoginActivity;
import com.appsnipp.loginsamples.views.feed.PostListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class ChatListActivity extends AppCompatActivity {
    @BindView(R.id.post_list) RecyclerView recyclerView;

    private ApiService apiService = HttpClient.getApiService();
    private List<Disposable> subscriptions = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fetchChats() {
        subscriptions.add(apiService
                .getChats()
                .map(chats -> Arrays
                        .stream(chats)
                        .filter(chat -> chat.getUser1().equals(Storage.currentUser) || chat.getUser2().equals(Storage.currentUser))
                        .collect(Collectors.toList()))
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribe(chats -> recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(chats)))
        );
    }

    @OnClick(R.id.logout)
    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.to_messages)
    public void toPosts() {
        Intent intent = new Intent(this, PostListActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.update_posts)
    public void updateChats() {
        fetchChats();
        Snackbar.make(recyclerView, "Диалоги обновлены", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, StartChatActivity.class);
            startActivity(intent);
        });
        fetchChats();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Disposable::dispose);
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Chat> mValues;
        private final View.OnClickListener mOnClickListener = view -> {
            Chat item = (Chat) view.getTag();
            Storage.currentChat = item.getId();
            Context context = view.getContext();
            Intent intent = new Intent(context, MessageListActivity.class);
            context.startActivity(intent);
        };

        SimpleItemRecyclerViewAdapter(List<Chat> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_list_content, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getMessages()[mValues.get(position).getMessages().length - 1].getCreatedAt());
            holder.mContentView.setText(
                    "Диалог с " + (Storage.currentUser.equals(mValues.get(position).getUser1()) ?
                    mValues.get(position).getUser2() :
                    mValues.get(position).getUser1()));

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
            }
        }
    }
}
