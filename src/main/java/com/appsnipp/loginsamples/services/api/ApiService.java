package com.appsnipp.loginsamples.services.api;

import com.appsnipp.loginsamples.models.auth.LoginBody;
import com.appsnipp.loginsamples.models.auth.LoginResponse;
import com.appsnipp.loginsamples.models.auth.RegistrationBody;
import com.appsnipp.loginsamples.models.auth.RegistrationResponse;
import com.appsnipp.loginsamples.models.chat.Chat;
import com.appsnipp.loginsamples.models.chat.Message;
import com.appsnipp.loginsamples.models.chat.PostMessageResponse;
import com.appsnipp.loginsamples.models.chat.StartChatResponse;
import com.appsnipp.loginsamples.models.feed.Post;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Flowable<LoginResponse> loginUser(@Body LoginBody loginBody);

    @POST("auth/register")
    Flowable<RegistrationResponse> registerUser(@Body RegistrationBody registrationBody);

    @GET("chat")
    Flowable<Chat[]> getChats();

    @GET("chat/{chatId}")
    Flowable<Chat> getChatById(@Path("chatId") String chatId);

    @POST("chat/{chatId}")
    Flowable<PostMessageResponse> postMessageToChat(@Path("chatId") String chatId, @Body Message message);

    @GET("chat/start/{user1}/{user2}")
    Flowable<StartChatResponse> startChatBetween(@Path("user1") String user1, @Path("user2") String user2);

    @GET("feed")
    Flowable<Post[]> getFeed();

    @GET("feed/{postId}")
    Flowable<Post> getPostById(@Path("postId") String postId);

    @POST("feed")
    Flowable<Post> addPost(@Body Post post);

    @GET("feed/like/{postId}/{userId}")
    Flowable<Post> like(@Path("postId") String postId, @Path("userId") String userId);

    @GET("feed/delete/{postId}")
    Flowable<Post> deletePostById(@Path("postId") String postId);
}
