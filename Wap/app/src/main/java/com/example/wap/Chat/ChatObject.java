package com.example.wap.Chat;

import com.example.wap.User.UserObject;

import java.util.ArrayList;

public class ChatObject {
    private String chatId;
    private ArrayList<UserObject> userObjectList = new ArrayList<>();

    public ChatObject(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public ArrayList<UserObject> getUserObjectList() {
        return userObjectList;
    }
    public void addUserToArrayList(UserObject mUser) {
        userObjectList.add(mUser);
    }
}
