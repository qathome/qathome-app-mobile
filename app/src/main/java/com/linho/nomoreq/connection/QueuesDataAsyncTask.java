package com.linho.nomoreq.connection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.linho.nomoreq.MainActivity;
import com.linho.nomoreq.MessageResultState;
import com.linho.nomoreq.R;
import com.linho.nomoreq.connection.manager.QueuesManager;
import com.linho.nomoreq.objects.Queues;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Carlo Todaro on 28/05/2015.
 */
public class QueuesDataAsyncTask extends AsyncTask<Void, Void, Queues>{

    private static final String TAG = QueuesDataAsyncTask.class.getSimpleName();
    private Activity activity;
    private Handler handler;
    private ProgressDialog progressDialog;

    public QueuesDataAsyncTask(Activity activity, Handler handler){
        this.activity = activity;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(R.string.titolo_alert_dialog_per_download_firms);
        progressDialog.setMessage(activity.getResources().getString(R.string.messaggio_alert_dialog_per_download_firms));
        progressDialog.show();
    }

    @Override
    protected Queues doInBackground(Void... voids) {

        QueuesManager queuesManager = new QueuesManager(activity.getApplicationContext());
        try {
            return queuesManager.executeRequest();
        }
        catch (JSONException e) {
            Log.e(TAG, "Si è verificato un errore durante il parsing JSON della responsee: " + e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Si è verificato un errore durante il download dei dati delle code: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Queues result) {
        super.onPostExecute(result);

        progressDialog.dismiss();
        if(handler != null) {
            Message message =  handler.obtainMessage(MainActivity.QUEUES_UPDATE);
            if(result == null){
                message.arg1 = MessageResultState.ERROR.ordinal();
            }
            else {
                message.arg1 = MessageResultState.OK.ordinal();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", result);
                message.setData(bundle);
            }
            message.sendToTarget();
        }
    }
}
