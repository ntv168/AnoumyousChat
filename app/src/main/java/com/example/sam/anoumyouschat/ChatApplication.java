package com.example.sam.anoumyouschat;

import android.app.Application;
import android.util.Log;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class ChatApplication extends Application {
    private String conversationId = "";

    private Socket mSocket;

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Socket getSocket() {
            try {
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.query = "conversationId=" + conversationId;
                mSocket = IO.socket(Constants.CHAT_SERVER_URL, opts);
//            mSocket = IO.socket("https://socketio-chat.now.sh");
                Log.d("--------------", "instance initializer: ChatApplication" + conversationId);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        return mSocket;
    }
}
