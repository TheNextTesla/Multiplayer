package independent_study.multiplayer.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.provider.Settings;

import java.nio.charset.Charset;

import independent_study.multiplayer.util.DispatchActivity;
import independent_study.multiplayer.util.DispatchReceiver;

public class InterpreterNFC implements DispatchReceiver, NfcAdapter.CreateNdefMessageCallback
{
    private NfcAdapter nfcAdapter;
    private boolean isNfcAdapterAvailable;
    private boolean isNfcAdapterSetup;

    public InterpreterNFC(Context context)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
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
        else
        {

        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        if(editText.getText().toString().equals(lastEditTextContent))
        {
            return null;
        }

        NdefRecord[] records = new NdefRecord[2];

        byte[] payload = editText.getText().toString().getBytes(Charset.forName("UTF-8"));
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        records[0] = record;
        records[1] = NdefRecord.createApplicationRecord(getPackageName());

        return new NdefMessage(records);
    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onStop()
    {

    }

    @Override
    public void onDestroy()
    {

    }
}
