package independent_study.multiplayer.gui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        receiverSMS.removeListener(this);
        interpreterNFC.removeListener(this);
    }
}
