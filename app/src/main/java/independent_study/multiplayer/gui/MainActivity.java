package independent_study.multiplayer.gui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dynamitechetan.flowinggradient.FlowingGradient;
import com.dynamitechetan.flowinggradient.FlowingGradientClass;
import com.skyfishjy.library.RippleBackground;

import independent_study.multiplayer.R;
import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.nfc.ListenerNFC;
import independent_study.multiplayer.sms.BroadcastReceiverSMS;
import independent_study.multiplayer.sms.ListenerSMS;
import independent_study.multiplayer.util.DispatchActivity;

public class MainActivity extends DispatchActivity implements ListenerSMS, ListenerNFC
{
    private BroadcastReceiverSMS receiverSMS;
    private InterpreterNFC interpreterNFC;

    private FlowingGradient flowingGradient;
    private RippleBackground rippleBackground;
    private Button hostButton;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiverSMS = BroadcastReceiverSMS.getInstance();
        receiverSMS.addListener(this);

        //interpreterNFC = new InterpreterNFC(this);
        //interpreterNFC.addListener(this);

        hostButton = findViewById(R.id.buttonHost);
        connectButton = findViewById(R.id.buttonConnect);
        rippleBackground = findViewById(R.id.rippleView);
        flowingGradient = findViewById(R.id.mainGradient);

        rippleBackground.startRippleAnimation();

        hostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goToActivity(HostActivityNFC.class);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goToActivity(WaitForConnectionActivity.class);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(!rippleBackground.isRippleAnimationRunning())
            rippleBackground.startRippleAnimation();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(rippleBackground.isRippleAnimationRunning())
            rippleBackground.stopRippleAnimation();
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
    public void onStop()
    {
        super.onStop();
        if(rippleBackground.isRippleAnimationRunning())
            rippleBackground.stopRippleAnimation();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //interpreterNFC.removeListener(this);
        receiverSMS.removeListener(this);
    }
}
