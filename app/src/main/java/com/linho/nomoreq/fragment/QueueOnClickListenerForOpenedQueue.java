package com.linho.nomoreq.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.linho.nomoreq.objects.Queue;
import com.linho.nomoreq.utils.DialogUtils;

/**
 * Created by Carlo on 04/07/2015.
 */
public class QueueOnClickListenerForOpenedQueue implements View.OnClickListener {

    private QueuesFragment queuesFragment;
    private String queueLetter;

    public QueueOnClickListenerForOpenedQueue(QueuesFragment queuesFragment, String queueLetter) {
        this.queuesFragment = queuesFragment;
        this.queueLetter = queueLetter;
    }

    @Override
    public void onClick(View view) {

        queuesFragment.requestTicket(queueLetter);
    }
}
