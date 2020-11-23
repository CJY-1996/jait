package com.example.jait.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {
    private String doucumentId;
    private String nickname;
    private String title;
    private String contents;
    @ServerTimestamp
    private Date date;
    private String postId;
    public Post() {
    }

    public Post(String doucumentId, String nickname, String title, String contents, String postId) {
        this.doucumentId = doucumentId;
        this.nickname = nickname;
        this.title = title;
        this.contents = contents;
        this.postId = postId;
    }

    public String getDoucumentId() {
        return doucumentId;
    }

    public void setDoucumentId(String doucumentId) {
        this.doucumentId = doucumentId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "doucumentId='" + doucumentId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", date=" + date +
                ", postId='" + postId + '\'' +
                '}';
    }
}
