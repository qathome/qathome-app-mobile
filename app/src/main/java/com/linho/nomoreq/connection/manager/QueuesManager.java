package com.linho.nomoreq.connection.manager;

import android.content.Context;
import android.content.res.Resources;

import com.linho.nomoreq.R;
import com.linho.nomoreq.objects.Queues;
import com.linho.nomoreq.parsing.JSONParserForQueues;

import org.json.JSONException;

/**
 * Created by Carlo on 28/06/2015.
 */
public class QueuesManager extends AbstractRequestAndResponseManager<Queues> {

    public QueuesManager(Context context) {
        super(context, "get");
        Resources resources = context.getResources();
        serviceUrl = resources.getString(R.string.firm_url) + resources.getString(R.string.queues_suffix);
    }

    @Override
    protected Queues parseResponse(String response) throws JSONException {

        JSONParserForQueues jsonParserForQueues = new JSONParserForQueues();
        return jsonParserForQueues.parse(response);
    }
}
