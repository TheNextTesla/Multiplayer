package independent_study.multiplayer.gui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.WindowManager;
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
    private static final int PERMISSIONS_KEY = 24703;

    private FlowingGradient flowingGradient;
    private RippleBackground rippleBackground;
    private Button hostButton;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        checkAndCallPermissions();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkAndCallPermissions();
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

    private boolean checkAndCallPermissions()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.NFC}, PERMISSIONS_KEY);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PERMISSIONS_KEY)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Please Authorize Permissions", Toast.LENGTH_SHORT).show();
            }
            else
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
