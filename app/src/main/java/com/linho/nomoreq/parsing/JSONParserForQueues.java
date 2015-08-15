package com.linho.nomoreq.parsing;

import com.linho.nomoreq.objects.Firm;
import com.linho.nomoreq.objects.Queue;
import com.linho.nomoreq.objects.Queues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlo on 29/06/2015.
 */
public class JSONParserForQueues implements Parser<Queues>{

    //TAG SPECIFICI PER LA LISTA DELLE CODE CREATE
    private static final String COUNT_TAG = "count";
    private static final String NEXT_TAG = "next";
    private static final String PREVIOUS_TAG = "previous";
    private static final String RESULTS_TAG = "results";
    private static final String ID_FOR_FIRM_TAG = "id_for_firm";
    private static final String LETTER_TAG = "letter";
    private static final String NAME_TAG = "name";
    private static final String DESCRIPTION_TAG = "description";
    private static final String URL_TAG = "url";
    private static final String STATE_TAG = "state";
    private static final String BEFORE_TOU_TAG = "before_you";
    private static final String ESTIMATED_WAITING_TIME_TAG = "time_estimated_waiting";

    private static final String FIRM_TAG = "firm";
    private static final String FIRM_NAME_TAG = "name";
    private static final String FIRM_DESCRIPTION_TAG = "description";
    private static final String FIRM_URL_TAG = "url";


    @Override
    public Queues parse(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        int count = jsonObject.getInt(COUNT_TAG);
        String next = jsonObject.getString(NEXT_TAG);
        String previous = jsonObject.getString(PREVIOUS_TAG);

        Queues queues = new Queues();
        queues.setCount(count);
        queues.setNext(next);
        queues.setPrevious(previous);

        JSONArray resultsJsonObject = jsonObject.getJSONArray(RESULTS_TAG);
        List<Queue> queuesList = new ArrayList<>(count);
        for(int i=0; i<count; i++){

            JSONObject queueJsonObject = (JSONObject) resultsJsonObject.get(i);
            int idForFirm = queueJsonObject.getInt(ID_FOR_FIRM_TAG);
            String letter = queueJsonObject.getString(LETTER_TAG);
            String name = queueJsonObject.getString(NAME_TAG);
            String description = queueJsonObject.getString(DESCRIPTION_TAG);
            //String url = queueJsonObject.getString(URL_TAG);
            Queue.State state = Queue.State.valueOf(queueJsonObject.getString(STATE_TAG));
            int beforeYou = queueJsonObject.getInt(BEFORE_TOU_TAG);
            String estimatedWaitingTime = queueJsonObject.getString(ESTIMATED_WAITING_TIME_TAG);
            /*
            JSONObject firmJsonObject = queueJsonObject.getJSONObject(FIRM_TAG);
            String firmName = firmJsonObject.getString(FIRM_NAME_TAG);
            String firmDescription = firmJsonObject.getString(FIRM_DESCRIPTION_TAG);
            String firmUrl = firmJsonObject.getString(FIRM_URL_TAG);
            */

            Queue queue = new Queue();
            queue.setIdForFirm(idForFirm);
            queue.setLetter(letter);
            queue.setName(name);
            queue.setDescription(description);
            //queue.setUrl(url);
            queue.setState(state);
            queue.setBeforeYou(beforeYou);
            queue.setEstimatedWaitingTime(estimatedWaitingTime);
            /*
            Firm firm = new Firm();
            firm.setName(firmName);
            firm.setDescription(firmDescription);
            firm.setUrl(firmUrl);
            queue.setFirm(firm);
            */

            queuesList.add(queue);
        }
        queues.setQueues(queuesList);

        return queues;
    }
}
