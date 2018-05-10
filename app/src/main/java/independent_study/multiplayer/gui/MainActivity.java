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

public class MainActivity extends DispatchActivity
{
    private FlowingGradient flowingGradient;
    private RippleBackground rippleBackground;
    private Button hostButton;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
