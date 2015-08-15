package com.linho.nomoreq.connection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.linho.nomoreq.MainActivity;
import com.linho.nomoreq.MessageResultState;
import com.linho.nomoreq.connection.manager.DeleteTicketManager;
import com.linho.nomoreq.objects.Ticket;

/**
 * Created by Carlo Todaro on 28/05/2015.
 */
public class CancellaTicketAsyncTask extends AsyncTask<String, Void, Ticket>{

    private static final String TAG = CancellaTicketAsyncTask.class.getSimpleName();
    private static final String CHARSET = "UTF-8";
    private Activity activity;
    private Handler handler;
    private ProgressDialog progressDialog;

    public CancellaTicketAsyncTask(Activity activity, Handler handler){
        this.activity = activity;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Cancellazione del ticket");
        progressDialog.setMessage("Cancellazione del ticket in corso. Attendere");
        progressDialog.show();
    }

    @Override
    protected Ticket doInBackground(String... strings) {

        try {
            String ticketUrl = strings[0];
            DeleteTicketManager deleteTicketManager = new DeleteTicketManager(activity.getApplicationContext(), ticketUrl);
            return (Ticket) deleteTicketManager.executeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Ticket result) {
        super.onPostExecute(result);

        progressDialog.dismiss();
        if(handler != null) {
            Message message =  handler.obtainMessage(MainActivity.DELETE_TICKET_REQUEST);
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
