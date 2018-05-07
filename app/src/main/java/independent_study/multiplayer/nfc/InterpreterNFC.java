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

import independent_study.multiplayer.util.DispatchReceiver;

public class InterpreterNFC implements DispatchReceiver, NfcAdapter.CreateNdefMessageCallback
{
    private NfcAdapter nfcAdapter;
    private Context context;
    private byte[] payload;
    private final Object payloadLock;
    private boolean isNfcAdapterAvailable;
    private boolean isNfcAdapterSetup;

    public InterpreterNFC(Context context)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        this.context = context;
        payloadLock = new Object();

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
        records[1] = NdefRecord.createApplicationRecord(context.getPackageName());

        setNfcMessage(null);

        return new NdefMessage(records);
    }

    public String onNewNFCIntent(Intent intent)
    {
        Parcelable[] receivedArray =intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if(receivedArray != null)
        {
            NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
            NdefRecord[] attachedRecords = receivedMessage.getRecords();

            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < attachedRecords.length; i++)
            {
                String tempString = new String(attachedRecords[i].getPayload());

                if (tempString.equals(context.getPackageName()))
                    continue;
                else
                    builder.append(tempString);
            }

            return builder.toString();
        }

        return null;
    }

    private void checkNfcStatus()
    {
        isNfcAdapterAvailable = nfcAdapter == null || !nfcAdapter.isEnabled();
        isNfcAdapterSetup = nfcAdapter == null || !nfcAdapter.isNdefPushEnabled();

        if(!isNfcAdapterAvailable)
        {
            context.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        else if(!isNfcAdapterSetup)
        {
            context.startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
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
}
