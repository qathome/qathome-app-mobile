package com.linho.nomoreq.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.linho.nomoreq.MainActivity;
import com.linho.nomoreq.MessageResultState;
import com.linho.nomoreq.connection.manager.QueuesManager;
import com.linho.nomoreq.connection.manager.TicketManager;
import com.linho.nomoreq.objects.Queues;
import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.utils.ConnectionUtils;
import com.linho.nomoreq.utils.ExceptionHandler;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Carlo on 03/06/2015.
 */
public class CheckStatusCodeService extends Service {

    private static final String TAG = CheckStatusCodeService.class.getSimpleName();

    /* Chiavi usate nel bundle del messaggio verso il service */
    public static final String URL_KEYWORD = "URL";
    public static final String WAITING_TIME_KEY = "waitingTime";

    /* Url e waitingTime utilizzate dal TimerTask. Fragment che richiede l'aggiornamento dei dati  */
    private String url;
    private final static long waitingTimeForQueues = 30000;
    private double waitingTimeForTicket;
    private int actualOperation = -100;

    /* Tempo in secondi al di sotto del quale il refresh della coda passa da AUTOREFRESH_TIME_1 sec a AUTOREFRESH_TIME_2 sec */
    private static final int WAITING_TRESHOLD = 300000; //5 min

    /* Tempo di refresh della coda se il tempo di attesa è > di WAITING_TRESHOLD sec */
    private static final int AUTOREFRESH_TIME_1 = 60000;   //1 min

    /* Tempo di refresh della coda se il tempo di attesa è <= di WAITING_TRESHOLD sec */
    private static final int AUTOREFRESH_TIME_2 = 20000;   //20 sec

    /* Messenger che permettono di ricevere ed inviare i messaggi dall'activity che si connette al Service */
    private final Messenger messengerFromActivity = new Messenger(new ServiceHandler());
    private WeakReference<Messenger> messengerToActivity;

    /* TimerTask e Timer per effettuare l'aggiornamento ogni tot secondi. Variabile che tiene conto di eventuali errori nella creazione del timer */
    private TimerTask timerTask;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    @Override
    public IBinder onBind(Intent intent) {

        return messengerFromActivity.getBinder();
    }

    /**
     * Gestisce i messaggi provenienti dall'activity
     */
    class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            //Se stiamo già eseguendo l'operazione, esce
            if(msg.what == actualOperation)
                return;

            Bundle bundle = msg.getData();
            try {
                switch (msg.what) {
                    case MainActivity.BIND:
                        messengerToActivity = new WeakReference<Messenger>(msg.replyTo);
                        break;
                    case MainActivity.QUEUES_UPDATE:
                        actualOperation = MainActivity.QUEUES_UPDATE;
                        setTimerForQueuesInfoUpdate();
                        break;
                    case MainActivity.TICKET_UPDATE:
                        url = bundle.getString(URL_KEYWORD);
                        waitingTimeForTicket = bundle.getDouble(WAITING_TIME_KEY) * 1000;
                        actualOperation = MainActivity.TICKET_UPDATE;
                        setTimerForTicketInfoUpdate();
                        break;
                    default:
                        Log.e(TAG, "Errore: tipo di operazione richiesta non prevista");
                        sendMessageToActivity(Message.obtain(null, MainActivity.GENERIC_ERROR));
                        return;
                }
            }
            catch(Exception e){
                Log.e(TAG, "Si è verificato un errore non previsto nel ServiceHandler: " + e.getMessage());
                sendMessageToActivity(Message.obtain(null, MainActivity.GENERIC_ERROR));
            }
        }
    }

    private void sendMessageToActivity(Message message){
        try {
            Messenger messenger = messengerToActivity.get();
            if(messenger != null)
                messenger.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Impossibile inviare il messaggio con parametro what " + message.what + " all'activity: " + e.getMessage());
        }
    }

    private void setTimerForTicketInfoUpdate() throws Exception {

        if(timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {

                if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getApplicationContext()))
                    return;

                Ticket ticket = null;
                Message message = Message.obtain(null, MainActivity.TICKET_UPDATE);
                try {
                    //Ottenere in qualche modo la stima dei minuti di attesa e se < WAITING_TRESHOLD, settare nuovamente il timer per avere un tempo di refresh inferiore
                    //Al momento inviamo un messaggio all'activity in attesa di avere le info riguardanti il numero di persone in coda ed il tempo di attesa
                    TicketManager ticketManager = new TicketManager(getApplicationContext(), url);
                    ticket = ticketManager.executeRequest();
                }
                catch (JSONException e) {
                    Log.e(TAG, "Si è verificato un errore durante il parsing JSON della responsee: " + e.getMessage());
                }
                catch (IOException e) {
                    Log.e(TAG, "Si è verificato un errore durante il download dei dati delle code: " + e.getMessage());
                    message.arg1 = MessageResultState.ERROR.ordinal();
                }

                if(ticket != null){
                    message.arg1 = MessageResultState.OK.ordinal();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MainActivity.KEY_FOR_DATA_IN_MESSAGE, ticket);
                    message.setData(bundle);
                }
                else{
                    message.arg1 = MessageResultState.ERROR.ordinal();
                }
                sendMessageToActivity(message);
            }
        };

        if(timer != null){
            timer.cancel();
        }

        //Crea un nuovo Timer e lo setta in base al tempo stimato di attesa
        timer = new Timer();
        if(waitingTimeForTicket > WAITING_TRESHOLD)
            timer.schedule(timerTask, 0, AUTOREFRESH_TIME_1);
        else
            timer.schedule(timerTask, 0, AUTOREFRESH_TIME_2);
    }

    private void setTimerForQueuesInfoUpdate() {

        if(timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {

            @Override
            public void run() {

                if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getApplicationContext()))
                    return;

                QueuesManager queuesManager = new QueuesManager(getApplicationContext());
                Queues queues = null;
                try {
                    queues = queuesManager.executeRequest();
                }
                catch (JSONException e) {
                    Log.e(TAG, "Si è verificato un errore durante il parsing JSON della responsee: " + e.getMessage());
                }
                catch (IOException e) {
                    Log.e(TAG, "Si è verificato un errore durante il download dei dati delle code: " + e.getMessage());
                }

                Message message = Message.obtain(null, MainActivity.QUEUES_UPDATE);
                if(queues != null) {
                    message.arg1 = MessageResultState.OK.ordinal();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MainActivity.KEY_FOR_DATA_IN_MESSAGE, queues);
                    message.setData(bundle);
                }
                else{
                    message.arg1 = MessageResultState.ERROR.ordinal();
                }
                sendMessageToActivity(message);
            }
        };

        if(timer != null){
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(timerTask, 0, waitingTimeForQueues);
    }
}
