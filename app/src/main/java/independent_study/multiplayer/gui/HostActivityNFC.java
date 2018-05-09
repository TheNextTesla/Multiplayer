package independent_study.multiplayer.gui;

import android.os.Bundle;

import independent_study.multiplayer.R;
import independent_study.multiplayer.util.DispatchActivity;

public class HostActivityNFC extends DispatchActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_nfc);
    }
}
