package com.linho.nomoreq.parsing;

import com.linho.nomoreq.objects.Queue;
import com.linho.nomoreq.objects.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Carlo on 29/05/2015.
 */
public class JSONParserForTicket {

    //TAG SPECIFICI PER IL TICKET
    //private static final String ID_FOR_FIRM_TAG = "id_for_firm";
    private static final String QUEUE_TAG = "queue";
    private static final String QUEUE_LETTER_TAG = "letter";
    private static final String QUEUE_NAME_TAG = "name";
    private static final String QUEUE_URL_TAG = "url";
    private static final String UNIQUE_URL_TAG = "unique_url";
    private static final String USER_CODE_TAG = "user_code";
    private static final String SEQUENTIAL_CODE_TAG = "sequential_code";
    private static final String STATE_TAG = "state";
    private static final String TYPE_TAG = "type";
    private static final String BEFORE_TOU_TAG = "before_you";
    private static final String ESTIMATED_WAITING_TIME_TAG = "time_estimated_waiting";

    public Ticket parseJSON(String jsonStr){

        if(jsonStr == null)
            return null;

        try {
            //Estrae i parametri della response...
            JSONObject jsonObj = new JSONObject(jsonStr);
            //int idForFirm = jsonObj.getInt(ID_FOR_FIRM_TAG);
            JSONObject queueJsonObject = jsonObj.getJSONObject(QUEUE_TAG);
            String queueLetter = queueJsonObject.getString(QUEUE_LETTER_TAG);
            String queueName = queueJsonObject.getString(QUEUE_NAME_TAG);
            String queueUrl = queueJsonObject.getString(QUEUE_URL_TAG);
            Queue queue = new Queue();
            queue.setLetter(queueLetter);
            queue.setName(queueName);
            queue.setUrl(queueUrl);
            String uniqueUrl = jsonObj.getString(UNIQUE_URL_TAG);
            String userCode = jsonObj.getString(USER_CODE_TAG);
            int sequentialCode = jsonObj.getInt(SEQUENTIAL_CODE_TAG);
            Ticket.State state = Ticket.State.valueOf(jsonObj.getString(STATE_TAG));
            Ticket.Type type = Ticket.Type.valueOf(jsonObj.getString(TYPE_TAG));
            int beforeYou = -1;
            if(!jsonObj.isNull(BEFORE_TOU_TAG))
                beforeYou = jsonObj.getInt(BEFORE_TOU_TAG);

            String estimatedWaitingTime = null;
            if(!jsonObj.isNull(ESTIMATED_WAITING_TIME_TAG))
                estimatedWaitingTime = jsonObj.getString(ESTIMATED_WAITING_TIME_TAG);

            //...e crea il relativo ogegtto Ticket
            Ticket ticket = new Ticket();
            //ticket.setIdForFirm(idForFirm);
            ticket.setQueue(queue);
            ticket.setUniqueUrl(uniqueUrl);
            ticket.setUserCode(userCode);
            ticket.setSequentialCode(sequentialCode);
            ticket.setState(state);
            ticket.setType(type);
            ticket.setBeforeYou(beforeYou);
            ticket.setEstimatedWaitingTime(estimatedWaitingTime);

            return ticket;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
