package com.denuinc.bookxchange.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import com.denuinc.bookxchange.R;

public class ActivityUtils {

    private static ActivityUtils instance;

    private ActivityUtils() {
    }

    public static ActivityUtils activityUtils() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    public void displayInformation(Context context, String title, String details, DialogInterface.OnClickListener yesAction, DialogInterface.OnClickListener noAction) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        if (noAction == null) {
            builder.setTitle(title)
                    .setMessage(details)
                    .setPositiveButton(android.R.string.yes, yesAction)
                    .setIcon(R.drawable.ic_email)
                    .show();
        } else {
            builder.setTitle(title)
                    .setMessage(details)
                    .setPositiveButton(android.R.string.yes, yesAction)
                    .setNegativeButton(android.R.string.no, noAction)
                    .setIcon(R.drawable.ic_email)
                    .show();
        }

    }

    public void emailIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, R.string.personal_email_address);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        Intent mailer = Intent.createChooser(intent, context.getString(R.string.app_name ) + context.getString(R.string.email));
        context.startActivity(mailer);
    }
}
