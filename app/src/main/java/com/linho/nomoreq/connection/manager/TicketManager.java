package com.linho.nomoreq.connection.manager;

import android.content.Context;

import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.parsing.JSONParserForTicket;

import org.json.JSONException;

/**
 * Created by Carlo on 30/06/2015.
 */
public class TicketManager extends AbstractRequestAndResponseManager<Ticket> {

    public TicketManager(Context context, String serviceUrl) {
        super(context, "get");
        this.serviceUrl = serviceUrl;
    }

    @Override
    protected Ticket parseResponse(String response) throws JSONException {

        JSONParserForTicket jsonParserForTicket = new JSONParserForTicket();
        return jsonParserForTicket.parseJSON(response);
    }
}
