package com.linho.nomoreq.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linho.nomoreq.MainActivity;
import com.linho.nomoreq.MessageResultState;
import com.linho.nomoreq.R;
import com.linho.nomoreq.connection.QueuesDataAsyncTask;
import com.linho.nomoreq.connection.PrendiTicketAsyncTask;
import com.linho.nomoreq.objects.Queue;
import com.linho.nomoreq.objects.Queues;
import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.utils.ConnectionUtils;
import com.linho.nomoreq.utils.DialogUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class QueuesFragment extends Fragment {

    private static final String TAG = QueuesFragment.class.getSimpleName();
    private static final String QUEUES_DATA_KEY = "queues";
    private ViewGroup queuesContainerLayout;
    private LinkedHashMap<String, ViewGroup> queuesLayout = new LinkedHashMap<>();
    private Handler handler;
    private Queues queues;
    private HashMap<String, Queue.State> queuesState = new HashMap<>();

    /**
     * Crea una nuova istanza
     *
     * @return Una nuova istanza di SummaryFragment.
     */
    public static QueuesFragment newInstance() {

        return new QueuesFragment();
    }

    public QueuesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){
                    case MainActivity.QUEUES_UPDATE:
                        analyzeQueuesMessage(msg);
                        break;
                    case MainActivity.NEW_TICKET_REQUEST:
                        analyzeNewTicketMessage(msg);
                        break;
                    default:
                        Log.e(TAG, "Valore del parametro switch non previsto: " + msg.what);
                }
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Controlla se il fragment è già stato creato (abbiamo i dati delle code disponibili)...
        if (savedInstanceState != null) {
            queues = (Queues) savedInstanceState.getSerializable(QUEUES_DATA_KEY);
            return;
        }

        //...altrimenti controlla se è disponibile una connessione internet: se no visualizza un alert dialog e poi esce...
        if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getActivity().getApplicationContext())){
            AlertDialog alertDialog = DialogUtils.buildYesAlertDialog(getActivity(), "Nessuna connessione internet", "Nessuna connessione internet presente. Attivarne una e riprovare", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                }
            });
            alertDialog.show();
            return;
        }

        //...altrimenti scarica le informazioni relative alla coda (uso l'Asinc Task in modo che
        //l'utente sappia cosa succede se c'è un ritardo nella visualizzazione dei dati)
        QueuesDataAsyncTask queuesDataAsyncTask = new QueuesDataAsyncTask(getActivity(), handler);
        queuesDataAsyncTask.execute();
    }

    private void analyzeNewTicketMessage(Message msg){
        if(msg.arg1 == MessageResultState.OK.ordinal()) {
            //Il ticket firm non è stata trovata
            if(!msg.getData().containsKey(MainActivity.KEY_FOR_DATA_IN_MESSAGE)) {
                Toast.makeText(getActivity(), "Si è verificato un errore nel parsing del ticket", Toast.LENGTH_SHORT).show();
                return;
            }

            Ticket ticket = (Ticket) msg.getData().getSerializable("data");
            ((MainActivity) getActivity()).launchNewTicketFragment(ticket);
        }
        else{
            Log.i(TAG, "Errore durante il download dal server");
            QueuesFragment.this.getActivity().onBackPressed();
        }
    }

    private void analyzeQueuesMessage(Message msg){

        if(msg.arg1 == MessageResultState.OK.ordinal()) {
            queues = (Queues) msg.getData().getSerializable("data");
            List<Queue> queuesList = queues.getQueues();
            LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(Queue queue : queuesList){
                if(!queuesLayout.containsKey(queue.getLetter())) {
                    ViewGroup viewGroup = addQueueLayout(layoutInflater);
                    queuesLayout.put(queue.getLetter(), viewGroup);
                    queuesState.put(queue.getLetter(), queue.getState());
                }
            }

            //Chiama il metodo di MainActivity che permette di aggiornare il Service in modo da
            //aggiornare costantemente i dati delle code. Bisogna spostarlo da qui, viene chiamato
            //tante volte inutilmente
            ((MainActivity)getActivity()).updateBackgroundServiceForQueues();
        }
        //Si è verificato un errore e non ci sono code da visualizzare all'utente: è inutile
        //visualizzare una schermata vuota perciò esce...
        else if (queues == null){
            Log.i(TAG, "Errore durante il download dal server e code non disponibili");
            QueuesFragment.this.getActivity().onBackPressed();
        }
        //...o ci sono code da visualizzare e possiamo mostrarle all'utente: gli diamo un messaggio di errore
        else
            Log.i(TAG, "Errore durante il download dal server e code disponibili");
    }

    private ViewGroup addQueueLayout(LayoutInflater layoutInflater) {

        ViewGroup queueLayout = (ViewGroup) layoutInflater.inflate(R.layout.coda_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 30, 10, 30);
        queueLayout.setLayoutParams(layoutParams);
        queuesContainerLayout.addView(queueLayout);

        return queueLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mainLayout = (ViewGroup) inflater.inflate(R.layout.prendi_ticket_layout, null);
        queuesContainerLayout = (ViewGroup) mainLayout.findViewById(R.id.container);

        return mainLayout;
    }

    public void requestTicket(String letter){

        if(!ConnectionUtils.checkIfInternetConnectionIsAvailable(getActivity().getApplicationContext())){
            AlertDialog alertDialog = DialogUtils.buildYesAlertDialog(getActivity(), "Nessuna connessione internet", "Nessuna connessione internet presente. Attivarne una e riprovare", null);
            alertDialog.show();
            return;
        }

        //Richiede un nuovo ticket
        PrendiTicketAsyncTask prendiTicketAsyncTask = new PrendiTicketAsyncTask(getActivity(), handler);
        prendiTicketAsyncTask.execute(letter);
    }

    public void updateQueueInfo(final Queue queue) throws Exception {

        ViewGroup queueLayout = (ViewGroup) queuesLayout.get(queue.getLetter());
        TextView idTextView = (TextView) queueLayout.findViewById(R.id.id_coda);
        idTextView.setText(queue.getLetter());
        TextView titleTextView = (TextView) queueLayout.findViewById(R.id.titolo_coda);
        if(queue.getState() == Queue.State.C) {
            String textForClosedQueue = queue.getName() + " (coda chiusa)";
            SpannableString spannableString = new SpannableString(textForClosedQueue);
            int closeStringPosition = spannableString.toString().indexOf("(coda chiusa)");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
            spannableString.setSpan(foregroundColorSpan, closeStringPosition, textForClosedQueue.length(), 0);
            titleTextView.setText(spannableString);
        }
        else
            titleTextView.setText(queue.getName());
        TextView descriptionTextView = (TextView) queueLayout.findViewById(R.id.descrizione_coda);
        descriptionTextView.setText(queue.getDescription());

        //Non so quanti stati ci siano perciò per adesso ne considero solo 2
        if(queue.getState() != Queue.State.O && queue.getState() != Queue.State.C)
            throw new Exception("Stato della coda non previsto: " + queue.getState());

        if(!queueLayout.hasOnClickListeners() && queue.getState() == Queue.State.O) {
            queueLayout.setOnClickListener(new QueueOnClickListenerForOpenedQueue(this, queue.getLetter()));
        }
        else if (!queueLayout.hasOnClickListeners() && queue.getState() == Queue.State.C){
            queueLayout.setOnClickListener(new QueueOnClickListenerForClosedQueue(this, queue.getLetter()));
        }
        else {
            Queue.State actualStateOfQueue = queuesState.get(queue.getLetter());
            if(actualStateOfQueue != queue.getState()){
                queuesState.put(queue.getLetter(), queue.getState());
                if(queue.getState() == Queue.State.O)
                    queueLayout.setOnClickListener(new QueueOnClickListenerForOpenedQueue(this, queue.getLetter()));
                else
                    queueLayout.setOnClickListener(new QueueOnClickListenerForClosedQueue(this, queue.getLetter()));
            }
        }
        //Elimina i decimali
        int dotPosition = queue.getEstimatedWaitingTime().indexOf(".");
        String normalizedWaitingTime = queue.getEstimatedWaitingTime().substring(0, dotPosition);
        String queueInfo = String.format(getActivity().getResources().getString(R.string.info_coda), queue.getBeforeYou(), normalizedWaitingTime);
        TextView infoCodaTextView = (TextView) queueLayout.findViewById(R.id.stima_coda);
        infoCodaTextView.setText(queueInfo);
    }

    public void updateQueues(Queues queues) throws Exception {

        for(Queue queue : queues.getQueues())
            updateQueueInfo(queue);
    }
}
