package com.edis.eschool.notification;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notifications implements Serializable {
    @SerializedName("Idnotification")
    private int Idnotification;
    @SerializedName("Titre")
    private String Titre;
    @SerializedName("Message")
    private String Message;
    @SerializedName("type")
    private String type;
    @SerializedName("date")
    private String date;
    @SerializedName("Lu")
    private int Lu;

    public Notifications() {
    }

    public int getIdnotification() {
        return Idnotification;
    }

    public void setIdnotification(int idnotification) {
        Idnotification = idnotification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLu() {
        return Lu;
    }

    public void setLu(int lu) {
        Lu = lu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitre() {
        return Titre;
    }

    public void setTitre(String titre) {
        Titre = titre;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
