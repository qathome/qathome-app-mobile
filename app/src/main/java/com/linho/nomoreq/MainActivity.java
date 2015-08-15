package com.linho.nomoreq;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.linho.nomoreq.fragment.QueuesFragment;
import com.linho.nomoreq.fragment.TicketFragment;
import com.linho.nomoreq.objects.Queues;
import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.service.CheckStatusCodeService;
import com.linho.nomoreq.utils.ExceptionHandler;


public class MainActivity extends ActionBarActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_FOR_QUEUES_FRAGMENT = "queues";
    private static final String KEY_FOR_TICKET_FRAGMENT = "ticket";

    //Key per il passaggio di dati nei messaggi
    public final static String KEY_FOR_DATA_IN_MESSAGE = "data";

    //Costanti per i messaggi
    public static final int GENERIC_ERROR = -1;
    public static final int BIND = 0;
    public final static int QUEUES_UPDATE = 1;
    public final static int TICKET_UPDATE = 2;
    public final static int NEW_TICKET_REQUEST = 10;
    public static final int DELETE_TICKET_REQUEST = 11;

    //Messenger per la ricezione e l'invio dei mssaggi da e verso il service
    private final Messenger messengerFromService = new Messenger(new IncomingHandler());
    private Messenger messengerToService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, QueuesFragment.newInstance(), KEY_FOR_QUEUES_FRAGMENT)
                    .commit();
        }

        //throw new RuntimeException("Ciao");
        bindService(new Intent(this, CheckStatusCodeService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void launchNewTicketFragment(Ticket ticket){

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TicketFragment.newInstance(ticket), KEY_FOR_TICKET_FRAGMENT).commit();
    }

    public void onTicketDeleted() {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, QueuesFragment.newInstance(), KEY_FOR_QUEUES_FRAGMENT).commit();
    }

    /**
     * Gestisce i messaggi provenienti dal service
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            try {
                switch (msg.what) {
                    case QUEUES_UPDATE:
                        Queues queues = (Queues) msg.getData().getSerializable(KEY_FOR_DATA_IN_MESSAGE);
                        QueuesFragment queuesFragment = (QueuesFragment) getSupportFragmentManager().findFragmentByTag(KEY_FOR_QUEUES_FRAGMENT);
                        if (queuesFragment != null)
                            queuesFragment.updateQueues(queues);
                        break;
                    case TICKET_UPDATE:
                        Ticket ticket = (Ticket) msg.getData().getSerializable(KEY_FOR_DATA_IN_MESSAGE);
                        TicketFragment ticketFragment = (TicketFragment) getSupportFragmentManager().findFragmentByTag(KEY_FOR_TICKET_FRAGMENT);
                        if(ticketFragment != null)
                            ticketFragment.updateTicketInfo(ticket);
                        break;
                    case 100:
                        Toast.makeText(MainActivity.this, "Binding col service effettuato", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
            catch(Exception e){
                Log.e(TAG, "Si è verificato un errore: " + e.getMessage());
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            messengerToService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, 0);
                msg.replyTo = messengerFromService;
                messengerToService.send(msg);
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            messengerToService = null;
        }
    };

    public void updateBackgroundServiceForQueues(){

        Message message = Message.obtain(null, QUEUES_UPDATE);
        try {
            messengerToService.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Impossibile aggiornare il service per la firm: " + e.getMessage());
        }
    }

    public void updateBackgroundServiceForTicket(String url, String waitingTime){

        Message message = Message.obtain(null, TICKET_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putString(CheckStatusCodeService.URL_KEYWORD, url);
        bundle.putDouble(CheckStatusCodeService.WAITING_TIME_KEY, Double.valueOf(waitingTime));
        message.setData(bundle);
        try {
            messengerToService.send(message);
        } catch (RemoteException e) {
            Log.e(TAG, "Impossibile aggiornare il service per la il ticket: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
