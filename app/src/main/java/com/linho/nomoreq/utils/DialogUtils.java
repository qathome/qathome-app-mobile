package com.linho.nomoreq.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.linho.nomoreq.R;

/**
 * Created by Carlo on 19/06/2015.
 */
public class DialogUtils {

    public static AlertDialog buildYesNoAlertDialog(Activity activity, String title, String message, DialogInterface.OnClickListener onclickListenerForPositiveButton, DialogInterface.OnClickListener onclickListenerForNegativeButton){

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setNegativeButton(android.R.string.no, onclickListenerForNegativeButton);
        ad.setPositiveButton(android.R.string.yes, onclickListenerForPositiveButton);

        return ad.create();
    }

    public static AlertDialog buildYesAlertDialog(Activity activity, String title, String message, DialogInterface.OnClickListener onclickListenerForPositiveButton){

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(android.R.string.yes, onclickListenerForPositiveButton);

        return ad.create();
    }
}
