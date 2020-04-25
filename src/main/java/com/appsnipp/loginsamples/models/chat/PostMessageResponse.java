package com.appsnipp.loginsamples.models.chat;

public class PostMessageResponse {
    private boolean success;
    private Message[] messages;

    public PostMessageResponse(boolean success, Message[] messages) {
        this.success = success;
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }
}
