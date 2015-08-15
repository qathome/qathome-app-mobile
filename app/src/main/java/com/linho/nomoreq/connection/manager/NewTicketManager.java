package com.linho.nomoreq.connection.manager;

import android.content.Context;
import android.content.res.Resources;

import com.linho.nomoreq.R;
import com.linho.nomoreq.objects.Ticket;
import com.linho.nomoreq.parsing.JSONParserForTicket;

import org.json.JSONException;

/**
 * Created by Carlo on 30/06/2015.
 */
public class NewTicketManager extends AbstractRequestAndResponseManager<Ticket> {

    public NewTicketManager(Context context, String codeLetter) {
        super(context, "post");
        Resources resources = context.getResources();
        this.serviceUrl = resources.getString(R.string.firm_url) + resources.getString(R.string.tickets_suffix);
        this.parameters = "type=N&queue_choices=" + codeLetter;
    }

    @Override
    protected Ticket parseResponse(String response) throws JSONException {

        JSONParserForTicket jsonParserForTicket = new JSONParserForTicket();
        return jsonParserForTicket.parseJSON(response);
    }
}
