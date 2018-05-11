package independent_study.multiplayer.gui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import independent_study.multiplayer.R;
import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.nfc.ListenerNFC;
import independent_study.multiplayer.util.DispatchActivity;

public class HostActivityNFC extends DispatchActivity implements ListenerNFC
{
    private InterpreterNFC interpreterNFC;
    private RippleBackground rippleBackground;
    private ImageView phoneButton;
    private Snackbar nfcSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_nfc);

        interpreterNFC = new InterpreterNFC(this);
        interpreterNFC.addListener(this);

        rippleBackground = findViewById(R.id.rippleViewHostNFC);
        phoneButton = findViewById(R.id.phoneButtonHostNFC);
        nfcSnackbar = Snackbar.make(rippleBackground, "NFC Not Enabled - Cannot Transmit", Snackbar.LENGTH_INDEFINITE);

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
    public void onNFCSent()
    {
        Toast.makeText(this, "NFC Tag Sent!", Toast.LENGTH_SHORT).show();
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
        interpreterNFC.removeListener(this);
    }
}
