package com.linho.nomoreq.objects;

import java.io.Serializable;

/**
 * Created by Carlo on 29/06/2015.
 */
public class Ticket implements Serializable {

    public enum State {W, D};
    public enum Type {N};

    private int idForFirm;
    private Queue queue;
    private String uniqueUrl;
    private String userCode;
    private int sequentialCode;
    private State state;
    private Type type;
    private int beforeYou;
    private String estimatedWaitingTime;

    public int getIdForFirm() {
        return idForFirm;
    }

    public void setIdForFirm(int idForFirm) {
        this.idForFirm = idForFirm;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public String getUniqueUrl() {
        return uniqueUrl;
    }

    public void setUniqueUrl(String uniqueUrl) {
        this.uniqueUrl = uniqueUrl;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getSequentialCode() {
        return sequentialCode;
    }

    public void setSequentialCode(int sequentialCode) {
        this.sequentialCode = sequentialCode;
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
}
