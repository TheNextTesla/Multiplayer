package independent_study.multiplayer;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.sms.BroadcastReceiverSMS;
import independent_study.multiplayer.sms.ListenerSMS;
import independent_study.multiplayer.util.DispatchActivity;

public class MainActivity extends DispatchActivity implements ListenerSMS, NfcAdapter.OnNdefPushCompleteCallback
{
    private BroadcastReceiverSMS receiverSMS;
    private InterpreterNFC interpreterNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiverSMS = BroadcastReceiverSMS.getInstance();
        receiverSMS.addListener(this);

        interpreterNFC = new InterpreterNFC(this);
        this.addDispatchReceivers(interpreterNFC);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    public void onSMSReceived(SmsMessage message)
    {

    }

    @Override
    public void onNewIntent(Intent intent)
    {
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
        {
            String nfcOuput = interpreterNFC.onNewNFCIntent(intent);
        }
    }

    public void onNdefPushComplete(NfcEvent nfcEvent)
    {

    }
}
