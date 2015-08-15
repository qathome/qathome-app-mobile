package com.linho.nomoreq.connection.manager;

import android.content.Context;

import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.parsing.JSONParserForTicket;

import org.json.JSONException;

/**
 * Created by Carlo on 02/07/2015.
 */
public class DeleteTicketManager extends AbstractRequestAndResponseManager<Ticket> {


    public DeleteTicketManager(Context context, String serviceUrl) {
        super(context, "patch", serviceUrl, "state=D");
    }

    @Override
    protected Ticket parseResponse(String response) throws JSONException {

        JSONParserForTicket jsonParserForTicket = new JSONParserForTicket();
        return jsonParserForTicket.parseJSON(response);
    }
}
