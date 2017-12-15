package com.application.chat;

import java.util.Date;

/**
 * Created by suvampramanik on 16/12/17.
 */

public class ChatMessage {
    private String messageText;
    private long messageTime;
    private String messageUser;


    //required for FireBase
    public ChatMessage(){}

    public ChatMessage(String text, String user){
        messageText = text;
        messageUser = user;
        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
}
