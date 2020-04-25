package com.appsnipp.loginsamples.views.feed;

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

import com.appsnipp.loginsamples.models.feed.Post;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;
import com.appsnipp.loginsamples.views.auth.LoginActivity;
import com.appsnipp.loginsamples.views.chats.ChatListActivity;
import com.appsnipp.loginsamples.views.chats.MessageListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class PostListActivity extends AppCompatActivity {
    @BindView(R.id.post_list) RecyclerView recyclerView;

    private ApiService apiService = HttpClient.getApiService();
    private List<Disposable> subscriptions = new ArrayList<>();

    public void fetchPosts() {
        subscriptions.add(apiService
                        .getFeed()
                        .observeOn(SchedulerProvider.ui())
                        .subscribeOn(SchedulerProvider.io())
                        .subscribe(posts -> recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Arrays.asList(posts))))
        );
    }

    @OnClick(R.id.logout)
    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.to_messages)
    public void toMessages() {
        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.update_posts)
    public void updatePosts() {
        fetchPosts();
        Snackbar.make(recyclerView, "Посты обновлены", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddPostActivity.class);
            startActivity(intent);
        });
        fetchPosts();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Disposable::dispose);
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Post> mValues;
        private final View.OnClickListener mOnClickListener = view -> {
            Post item = (Post) view.getTag();
            Storage.currentPost = item.get_id();
            Context context = view.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);
            context.startActivity(intent);
        };

        SimpleItemRecyclerViewAdapter(List<Post> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getCreationDate());
            holder.mContentView.setText(mValues.get(position).getTitle());

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
