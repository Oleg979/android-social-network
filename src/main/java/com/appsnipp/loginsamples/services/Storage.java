package com.appsnipp.loginsamples.services;

import com.appsnipp.loginsamples.models.feed.Post;

import io.reactivex.subjects.BehaviorSubject;

public class Storage {
    public static String currentUser;
    public static String currentChat = "5e946767b9ca1431bcc03540";
    public static String currentPost = "5e9c07e252d9b100175f2bb7";
    public static BehaviorSubject<Post> updatePostSubject = BehaviorSubject.create();
}
