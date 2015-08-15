package com.linho.nomoreq.objects;

import java.io.Serializable;

/**
 * Created by Carlo on 29/06/2015.
 */
public class Firm implements Serializable {

    private String name;
    private String description;
    private String url;

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
}
