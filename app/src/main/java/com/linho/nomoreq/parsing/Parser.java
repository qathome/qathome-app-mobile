package com.linho.nomoreq.parsing;

import org.json.JSONException;

/**
 * Created by Carlo on 29/06/2015.
 */
public interface Parser<T> {

    public T parse(String json) throws JSONException;
}
