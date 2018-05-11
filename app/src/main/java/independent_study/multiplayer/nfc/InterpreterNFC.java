package independent_study.multiplayer.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.provider.Settings;

import java.lang.reflect.Array;
import java.util.ArrayList;

import independent_study.multiplayer.util.DispatchActivity;
import independent_study.multiplayer.util.DispatchReceiver;

public class InterpreterNFC implements DispatchReceiver, NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback
{
    private final ArrayList<ListenerNFC> listeners;
    private NfcAdapter nfcAdapter;
    private Activity activity;
    private volatile byte[] payload;
    private final Object payloadLock;
    private boolean isNfcAdapterAvailable;
    private boolean isNfcAdapterSetup;

    public InterpreterNFC(Activity activity)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
        listeners = new ArrayList<>();
        this.activity = activity;
        payloadLock = new Object();

        if(activity instanceof DispatchActivity)
        {
            ((DispatchActivity) activity).addDispatchReceivers(this);
        }

        checkNfcStatus();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        byte[] tempNfcMessage = getNfcMessage();
        if(tempNfcMessage == null)
            return null;

        NdefRecord[] records = new NdefRecord[2];
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], tempNfcMessage);

        records[0] = record;
        records[1] = NdefRecord.createApplicationRecord(activity.getApplicationContext().getPackageName());

        setNfcMessage(null);

        return new NdefMessage(records);
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent)
    {
        synchronized (listeners)
        {
            for(ListenerNFC listenerNFC : listeners)
            {
                listenerNFC.onNFCSent();
            }
        }
    }

    public static String onNewNFCIntent(Intent intent, Context context)
    {
        Parcelable[] receivedArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if(receivedArray != null)
        {
            NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
            NdefRecord[] attachedRecords = receivedMessage.getRecords();

            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < attachedRecords.length; i++)
            {
                String tempString = new String(attachedRecords[i].getPayload());

                if (!tempString.equals(context.getPackageName()))
                    builder.append(tempString);
            }

            return builder.toString();
        }

        return null;
    }

    private void checkNfcStatus()
    {
        isNfcAdapterAvailable = nfcAdapter != null && nfcAdapter.isEnabled();
        isNfcAdapterSetup = nfcAdapter != null && nfcAdapter.isNdefPushEnabled();

        if(isNfcAdapterSetup && isNfcAdapterAvailable)
        {
            nfcAdapter.setOnNdefPushCompleteCallback(this, activity);
            nfcAdapter.setNdefPushMessageCallback(this, activity);
        }
    }

    public boolean addListener(ListenerNFC listenerNFC)
    {
        synchronized (listeners)
        {
            for (ListenerNFC listener : listeners)
            {
                if (listener == listenerNFC)
                    return false;
            }
            listeners.add(listenerNFC);
            return true;
        }
    }

    public boolean removeListener(ListenerNFC listenerNFC)
    {
        synchronized (listeners)
        {
            return listeners.remove(listenerNFC);
        }
    }

    @Override
    public void onResume()
    {
        checkNfcStatus();
    }

    @Override
    public void onPause()
    {
        //TODO: Add Lifecycle method
    }

    @Override
    public void onStop()
    {
        setNfcMessage(null);
    }

    @Override
    public void onDestroy()
    {
        nfcAdapter = null;
    }

    public void goToNFCSettings()
    {
        if(!isNfcAdapterAvailable)
        {
            activity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        else if(!isNfcAdapterSetup)
        {
            activity.startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else
        {
            nfcAdapter.setOnNdefPushCompleteCallback(this, activity);
            nfcAdapter.setNdefPushMessageCallback(this, activity);
        }
    }

    public void setNfcMessage(byte[] nfcMessage)
    {
        synchronized (payloadLock)
        {
            payload = nfcMessage;
        }
    }

    public byte[] getNfcMessage()
    {
        synchronized (payloadLock)
        {
            return payload;
        }
    }

    public NfcAdapter getNfcAdapter()
    {
        return nfcAdapter;
    }

    public boolean isNfcWorking()
    {
        return isNfcAdapterAvailable && isNfcAdapterSetup;
    }
}
