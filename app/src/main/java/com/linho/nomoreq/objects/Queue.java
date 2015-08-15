package com.linho.nomoreq.objects;

import java.io.Serializable;

/**
 * Created by Carlo on 29/06/2015.
 */
public class Queue implements Serializable {

    public enum State {O, C}

    ;

    private int idForFirm;
    private String letter;
    private String name;
    private String description;
    private String url;
    private State state;
    private int beforeYou;
    private String estimatedWaitingTime;
    //private Firm firm;

    public int getIdForFirm() {
        return idForFirm;
    }

    public void setIdForFirm(int idForFirm) {
        this.idForFirm = idForFirm;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getBeforeYou() {
        return beforeYou;
    }

    public void setBeforeYou(int beforeYou) {
        this.beforeYou = beforeYou;
    }

    public String getEstimatedWaitingTime() {
        return estimatedWaitingTime;
    }

    public void setEstimatedWaitingTime(String estimatedWaitingTime) {
        this.estimatedWaitingTime = estimatedWaitingTime;
    }

    /*
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }
    */


}
