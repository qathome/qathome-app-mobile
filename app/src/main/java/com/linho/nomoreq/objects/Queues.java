package com.linho.nomoreq.objects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Carlo on 29/06/2015.
 */
public class Queues implements Serializable {

    private int count;
    private String next;
    private String previous;
    private List<Queue> queues;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }
}
