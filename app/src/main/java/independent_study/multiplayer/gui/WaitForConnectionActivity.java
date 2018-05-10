package independent_study.multiplayer.gui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import independent_study.multiplayer.R;
import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.nfc.ListenerNFC;
import independent_study.multiplayer.sms.BroadcastReceiverSMS;
import independent_study.multiplayer.sms.ListenerSMS;
import independent_study.multiplayer.util.DispatchActivity;

public class WaitForConnectionActivity extends DispatchActivity implements ListenerSMS, ListenerNFC
{
    private BroadcastReceiverSMS receiverSMS;
    private InterpreterNFC interpreterNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_connection);

        receiverSMS = BroadcastReceiverSMS.getInstance();
        receiverSMS.addListener(this);

        interpreterNFC = new InterpreterNFC(this);
        interpreterNFC.addListener(this);
    }

    @Override
    public void onSMSReceived(SmsMessage message)
    {
        Toast.makeText(this, "SMS Tag Received!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNFCSent()
    {
        Toast.makeText(this, "NFC Tag Sent!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        try
        {
            if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
            {
                //Effectively onNFCReceived
                String nfcOutput = interpreterNFC.onNewNFCIntent(intent);

                if(nfcOutput != null)
                {
                    Toast.makeText(this, "NFC Tag Received!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                super.onNewIntent(intent);
            }
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        receiverSMS.removeListener(this);
        interpreterNFC.removeListener(this);
    }
}
