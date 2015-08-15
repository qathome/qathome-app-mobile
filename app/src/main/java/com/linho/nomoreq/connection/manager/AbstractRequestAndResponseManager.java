package com.linho.nomoreq.connection.manager;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Carlo on 26/06/2015.
 */
public abstract class AbstractRequestAndResponseManager<T> implements RequestAndResponseManagerInterface {

    protected static final String TAG = AbstractRequestAndResponseManager.class.getSimpleName();
    protected static final String CHARSET = "UTF-8";
    protected Context context;
    protected String method;
    protected String serviceUrl;
    protected String parameters;

    protected AbstractRequestAndResponseManager(Context context, String method){

        this(context, method, null, null);
    }

    protected AbstractRequestAndResponseManager(Context context, String method, String serviceUrl){

        this(context, method, serviceUrl, null);
    }

    protected AbstractRequestAndResponseManager(Context context, String method, String serviceUrl, String parameters){

        this.context = context;
        this.method = method;
        this.serviceUrl = serviceUrl;
        this.parameters = parameters;
    }

    protected String readStream(InputStream in) throws IOException {
        String value = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                value += line;
            }
            return value;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    protected String sendRequest() throws IOException {

        URL url = new URL(serviceUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        if(method.equalsIgnoreCase("get")) {
            con.setRequestMethod("GET");
        }
        else if(method.equalsIgnoreCase("patch")) {
            con.setRequestMethod("PATCH");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Charset", CHARSET);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            if(parameters != null) {
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
                writer.write(parameters);
                writer.flush();
                writer.close();
                os.close();
            }
        }
        else if(method.equalsIgnoreCase("POST")){
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Charset", CHARSET);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            if(parameters != null) {
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
                writer.write(parameters);
                writer.flush();
                writer.close();
                os.close();
            }
        }

        int responseCode = con.getResponseCode();
        InputStream inputStream = con.getInputStream();

        return readStream(inputStream);
    }

    public T executeRequest() throws IOException, JSONException {

        String response = sendRequest();
        return parseResponse(response);
    }

    protected abstract T parseResponse(String response) throws JSONException;
}
