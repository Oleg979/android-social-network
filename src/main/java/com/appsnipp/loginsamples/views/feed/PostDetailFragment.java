package com.appsnipp.loginsamples.views.feed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class PostDetailFragment extends Fragment {
    @BindView(R.id.post_detail) TextView postText;
    @BindView(R.id.post_date) TextView postDate;
    @BindView(R.id.post_author) TextView postAuthor;
    @BindView(R.id.post_likes) TextView postLikes;

    private BehaviorSubject<Post> postBehaviorSubject = Storage.updatePostSubject;
    private List<Disposable> subscriptions = new ArrayList<>();
    private ApiService apiService = HttpClient.getApiService();


    public PostDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
        subscriptions.add(postBehaviorSubject.subscribe(post -> {
            if (appBarLayout != null) {
                appBarLayout.setTitle(post.getTitle());
            }
        }));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.post_detail, container, false);
        ButterKnife.bind(this, rootView);
        subscriptions.add(postBehaviorSubject.subscribe(post -> {
            postText.setText(post.getText());
            postAuthor.setText("Автор поста - " + post.getUsername());
            postDate.setText("Пост был создан " + post.getCreationDate());
            postLikes.setText("Всего лайков: " + post.getLikes().length);
        }));
        subscriptions
                .add(this.apiService.getPostById(Storage.currentPost)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribe(post -> Storage.updatePostSubject.onNext(post)));
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Disposable::dispose);
    }
}
