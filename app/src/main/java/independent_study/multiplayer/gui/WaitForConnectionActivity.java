package independent_study.multiplayer.gui;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import org.msgpack.core.MessageUnpacker;

import independent_study.multiplayer.R;
import independent_study.multiplayer.comm.GameInitiationMessage;
import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.nfc.ListenerNFC;
import independent_study.multiplayer.sms.BroadcastReceiverSMS;
import independent_study.multiplayer.sms.ListenerSMS;
import independent_study.multiplayer.util.DispatchActivity;

import static independent_study.multiplayer.sms.TransmitterSMS.SENT;

public class WaitForConnectionActivity extends DispatchActivity implements ListenerSMS, ListenerNFC
{
    private BroadcastReceiverSMS receiverSMS;
    private InterpreterNFC interpreterNFC;

    private RippleBackground rippleBackground;
    private ImageView phoneButton;
    private Snackbar nfcSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_connection);

        receiverSMS = BroadcastReceiverSMS.getInstance();
        receiverSMS.addListener(this);

        interpreterNFC = new InterpreterNFC(this);
        interpreterNFC.addListener(this);

        rippleBackground = findViewById(R.id.rippleViewConnection);
        phoneButton = findViewById(R.id.phoneButtonConnection);

        nfcSnackbar = Snackbar.make(rippleBackground, "NFC Not Enabled - Listening Only on SMS", Snackbar.LENGTH_INDEFINITE);

        phoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!interpreterNFC.isNfcWorking())
                {
                    interpreterNFC.goToNFCSettings();
                }
                else if(!rippleBackground.isRippleAnimationRunning())
                {
                    rippleBackground.startRippleAnimation();
                }
                else if(nfcSnackbar.isShown())
                {
                    nfcSnackbar.dismiss();
                }
            }
        });

        if(!interpreterNFC.isNfcWorking())
        {
            nfcSnackbar.show();
        }
    }

    @Override
    public void onSMSReceived(SmsMessage message)
    {
        Toast.makeText(this, "SMS Tag Received!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNFCSent()
    {
        Log.e("WaitForConnection", "NFC SENT FROM WRONG ACTIVITY");
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        try
        {
            if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
            {
                //Effectively onNFCReceived
                MessageUnpacker nfcOutput = InterpreterNFC.onNewNFCIntent(intent, this);

                if(nfcOutput != null)
                {
                    Toast.makeText(this, "NFC Tag Received!", Toast.LENGTH_SHORT).show();
                    GameInitiationMessage gim = GameInitiationMessage.generateInitiationMessage(nfcOutput);

                    String hostname = gim.getHostname();
                    byte[] ipAddress = gim.getIpAddress();

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    
                    if(hostname.equals(wifiInfo.getSSID()))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putByteArray(GameActivity.IP_BYTE_BUNDLE_KEY, ipAddress);
                        goToActivity(GameActivity.class, new Pair<>(GameActivity.GAME_SETUP_BUNDLE_KEY, bundle));
                    }
                    else
                    {
                        displayNotification("You Must Be On the Same Network!", null, this);
                    }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String response = null;
        switch (requestCode)
        {
            case SENT :
                //Effectively on Data SMS Sent
                switch (resultCode)
                {
                    case RESULT_OK :
                        response = "OK";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE :
                        response = "Generic Failure";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF :
                        response = "Radio Off";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU :
                        response = "Null Pdu";
                        break;
                }
                break;
        }

        if (response != null)
        {
            Log.d("WaitForConnection", "Response: " + response);
        }
        else
        {
            Log.d("WaitForConnection", "ERROR SMS!");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(rippleBackground.isRippleAnimationRunning())
            rippleBackground.stopRippleAnimation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(interpreterNFC.isNfcWorking())
        {
            if(!rippleBackground.isRippleAnimationRunning())
                rippleBackground.startRippleAnimation();
            if(nfcSnackbar.isShown())
                nfcSnackbar.dismiss();
        }
        else
        {
            if(rippleBackground.isRippleAnimationRunning())
                rippleBackground.stopRippleAnimation();
            if(!nfcSnackbar.isShown())
                nfcSnackbar.show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        rippleBackground.stopRippleAnimation();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        receiverSMS.removeListener(this);
        interpreterNFC.removeListener(this);
    }
}
