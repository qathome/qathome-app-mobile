package com.linho.nomoreq.connection.manager;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Carlo on 26/06/2015.
 */
public interface RequestAndResponseManagerInterface<T> {

    public T executeRequest() throws IOException, JSONException;
}
