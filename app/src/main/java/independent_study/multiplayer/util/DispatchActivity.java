package independent_study.multiplayer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class DispatchActivity extends AppCompatActivity
{
    protected final ArrayList<DispatchReceiver> dispatchReceivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        synchronized (dispatchReceivers)
        {
            for (DispatchReceiver receiver : dispatchReceivers)
            {
                receiver.onResume();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        synchronized (dispatchReceivers)
        {
            for (DispatchReceiver receiver : dispatchReceivers)
            {
                receiver.onPause();
            }
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        synchronized (dispatchReceivers)
        {
            for (DispatchReceiver receiver : dispatchReceivers)
            {
                receiver.onStop();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        synchronized (dispatchReceivers)
        {
            for (DispatchReceiver receiver : dispatchReceivers)
            {
                receiver.onDestroy();
            }
        }
        removeDispatchReceivers();
    }

    public void addDispatchReceivers(DispatchReceiver... receivers)
    {
        synchronized (dispatchReceivers)
        {
            dispatchReceivers.addAll(Arrays.asList(receivers));
        }
    }

    protected ArrayList<DispatchReceiver> getDispatchReceivers()
    {
        synchronized (dispatchReceivers)
        {
            return new ArrayList<>(dispatchReceivers);
        }
    }

    protected void removeDispatchReceivers()
    {
        synchronized (dispatchReceivers)
        {
            dispatchReceivers.clear();
        }
    }

    protected void goToActivity(Class<? extends Activity> activityClass)
    {
        Intent intent = new Intent(this.getBaseContext(), activityClass);
        startActivity(intent);
    }

    protected void goToActivity(Class<? extends Activity> activityClass, Pair<String, Bundle>... pairs)
    {
        Intent intent = new Intent(this.getBaseContext(), activityClass);
        for(Pair<String, Bundle> pair : pairs)
        {
            intent.putExtra(pair.first, pair.second);
        }
        startActivity(intent);
    }

    protected void displayNotification(String notification, DialogInterface.OnClickListener listener, Context context)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        }
        else
        {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(notification);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNeutralButton("OK", listener);
        builder.show();
    }
}
