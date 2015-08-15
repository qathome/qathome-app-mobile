package com.linho.nomoreq.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.linho.nomoreq.utils.DialogUtils;

/**
 * Created by Carlo on 04/07/2015.
 */
public class QueueOnClickListenerForClosedQueue implements View.OnClickListener {

    private QueuesFragment queuesFragment;
    private String queueLetter;

    public QueueOnClickListenerForClosedQueue(QueuesFragment queuesFragment, String queueLetter) {
        this.queuesFragment = queuesFragment;
        this.queueLetter = queueLetter;
    }

    @Override
    public void onClick(View view) {

        AlertDialog alertDialog = DialogUtils.buildYesAlertDialog(queuesFragment.getActivity(), "Coda chiusa", "La coda " + queueLetter + " è chiusa", null);
        alertDialog.show();
    }
}
