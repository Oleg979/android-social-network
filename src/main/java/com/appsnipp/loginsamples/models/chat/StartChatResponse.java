package com.appsnipp.loginsamples.models.chat;

public class StartChatResponse {
    private boolean success;
    private String chatId;

    public StartChatResponse(boolean success, String chatId) {
        this.success = success;
        this.chatId = chatId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
