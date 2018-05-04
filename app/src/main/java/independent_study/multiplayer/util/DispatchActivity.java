package independent_study.multiplayer.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class DispatchActivity extends AppCompatActivity
{
    protected final static ArrayList<DispatchReceiver> dispatchReceivers = new ArrayList<>();

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
    }

    protected void addDispatchReceivers(DispatchReceiver... receivers)
    {
        synchronized (dispatchReceivers)
        {
            dispatchReceivers.addAll(Arrays.asList(receivers));
        }
    }
}
