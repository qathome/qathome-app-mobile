package com.linho.nomoreq.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linho.nomoreq.MainActivity;
import com.linho.nomoreq.MessageResultState;
import com.linho.nomoreq.R;
import com.linho.nomoreq.connection.CancellaTicketAsyncTask;
import com.linho.nomoreq.connection.PrendiTicketAsyncTask;
import com.linho.nomoreq.connection.manager.DeleteTicketManager;
import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.utils.ConnectionUtils;
import com.linho.nomoreq.utils.DialogUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Carlo on 29/05/2015.
 */
public class TicketFragment extends Fragment {

    private final static String TAG = TicketFragment.class.getSimpleName();
    public static String KEY_FOR_TICKET_PARAMETER = "ticket";
    private static final String CLICKABLE_STRING = "qui";
    private Ticket ticket;
    private TextView ticketId;
    private Button annullaTicket;
    private TextView infoCoda;
    private TextView aggiornaCoda;
    private Handler handler;

    public static TicketFragment newInstance(Ticket ticket){

        TicketFragment ticketFragment = new TicketFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FOR_TICKET_PARAMETER, ticket);
        ticketFragment.setArguments(bundle);

        return ticketFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            ticket = (Ticket) savedInstanceState.getSerializable(KEY_FOR_TICKET_PARAMETER);
        else if(getArguments() != null)
            ticket = (Ticket) getArguments().getSerializable(KEY_FOR_TICKET_PARAMETER);

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){
                    case MainActivity.DELETE_TICKET_REQUEST:
                        analyzeDeleteTicketMessage(msg);
                        break;
                    default:
                        Log.e(TAG, "Valore del parametro switch non previsto: " + msg.what);
                }
            }
        };
    }

    private void analyzeDeleteTicketMessage(Message msg) {

        if(msg.arg1 == MessageResultState.OK.ordinal()) {
            if(!msg.getData().containsKey(MainActivity.KEY_FOR_DATA_IN_MESSAGE)) {
                Toast.makeText(getActivity(), "Si è verificato un errore nel parsing del ticket", Toast.LENGTH_SHORT).show();
                return;
            }

            Ticket ticket = (Ticket) msg.getData().getSerializable("data");
            if(ticket.getState() != Ticket.State.D)
                Log.e(TAG, "Il ticket cancellato non risulta esserlo dal messaggio");

            ((MainActivity) getActivity()).onTicketDeleted();
        }
        else{
            Log.i(TAG, "Errore durante il download dal server");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Chiama il metodo di MainActivity che permette di aggiornare il Service in modo da
        //aggiornare costantemente i dati del Ticket (il waitingtime è costante dato che non viene passato)
        ((MainActivity)getActivity()).updateBackgroundServiceForTicket(ticket.getUniqueUrl(), ticket.getEstimatedWaitingTime());
    }

    public void updateTicketInfo(Ticket ticket){

        this.ticket = ticket;
        //Elimina i decimali
        int dotPosition = ticket.getEstimatedWaitingTime().indexOf(".");
        String normalizedWaitingTime = ticket.getEstimatedWaitingTime().substring(0, dotPosition);
        infoCoda.setText(String.format(getActivity().getResources().getString(R.string.info_coda_ticket), ticket.getBeforeYou(), normalizedWaitingTime));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.ticket_layout, container, false);
        ticketId = (TextView) layout.findViewById(R.id.idTicket);
        ticketId.setText(String.valueOf(ticket.getUserCode()));
        annullaTicket = (Button) layout.findViewById(R.id.annullaTicket);
        annullaTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle(R.string.annullamento_ticket_titolo_alert_dialog);
                ad.setMessage(R.string.annullamento_ticket_messaggio_alert_dialog);

                AlertDialog alertDialog = ad.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getActivity().getApplicationContext())){
                            AlertDialog alertDialog = DialogUtils.buildYesAlertDialog(getActivity(), "Nessuna connessione internet", "Nessuna connessione internet presente. Attivarne una e riprovare", null);
                            alertDialog.show();
                            return;
                        }

                        try {
                            annullaTicket();
                        } catch (IOException | JSONException e) {
                            Log.e(TAG, "Impossibile annullare il ticket: " + e.getMessage());
                        }
                    }
                }).setNegativeButton(android.R.string.no, null).create();
                alertDialog.show();
            }
        });
        infoCoda = (TextView) layout.findViewById(R.id.infoCoda);
        /*
        aggiornaCoda = (TextView) layout.findViewById(R.id.aggiornaCoda);
        String messageForLink = getActivity().getResources().getString(R.string.aggiorna_info_coda_link);
        int clickableCharFrom = messageForLink.indexOf(CLICKABLE_STRING);
        int clickableCharTo = clickableCharFrom + CLICKABLE_STRING.length();
        SpannableString link = new SpannableString(messageForLink);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                downloadQueueInfo();
            }
        };
        link.setSpan(clickableSpan, clickableCharFrom, clickableCharTo, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        aggiornaCoda.setText(link);
        aggiornaCoda.setMovementMethod(LinkMovementMethod.getInstance());
        */
        updateTicketInfo(ticket);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_FOR_TICKET_PARAMETER, ticket);
    }

    private void annullaTicket() throws IOException, JSONException {

        if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getActivity().getApplicationContext())){
            AlertDialog alertDialog = DialogUtils.buildYesAlertDialog(getActivity(), "Nessuna connessione internet", "Nessuna connessione internet presente. Attivarne una e riprovare", null);
            alertDialog.show();
            return;
        }

        //Richiede un nuovo ticket
        CancellaTicketAsyncTask cancellaTicketAsyncTask = new CancellaTicketAsyncTask(getActivity(), handler);
        cancellaTicketAsyncTask.execute(ticket.getUniqueUrl());
    }
}
