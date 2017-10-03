package com.medziku.motoresponder.redux.state;

import com.medziku.motoresponder.redux.ArrayList;

public class Responses implements Cloneable {
    public ArrayList<Response> list = new ArrayList<>();
    public int nextId = 0;

    public Responses clone() {
        try {
            return (Responses) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public static abstract class Response {
    }

    public static class SmsResponse extends Response {
        public String message;
        public String phoneNumber;
        public int id;

        public SmsResponse(String message, String phoneNumber, int id) {
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.id = id;
        }
    }
}
