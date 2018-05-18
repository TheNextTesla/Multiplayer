package independent_study.multiplayer.gui;

import android.os.Bundle;

import org.msgpack.core.MessageUnpacker;

import java.util.ArrayList;

import independent_study.multiplayer.R;
import independent_study.multiplayer.comm.GameConnection;
import independent_study.multiplayer.comm.GameInitiationMessage;
import independent_study.multiplayer.comm.GameMessage;
import independent_study.multiplayer.comm.NetworkGameListener;
import independent_study.multiplayer.util.DispatchActivity;

public class GameActivity extends DispatchActivity implements NetworkGameListener
{
    public static final String IS_HOST_BUNDLE_KEY = "isHost";
    public static final String IP_BYTE_BUNDLE_KEY = "ip";
    public static final String GAME_SETUP_BUNDLE_KEY = "GameSetup";

    private GameConnection gameConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle gameSetup = getIntent().getExtras();
        boolean isHost = gameSetup.getBoolean(IS_HOST_BUNDLE_KEY);

        if(isHost)
        {
            setUpHost();
        }
        else
        {
            setUpConnection(gameSetup);
        }
    }

    private void setUpHost()
    {
        gameConnection = new GameConnection(this, this);
    }

    private void setUpConnection(Bundle gameSetup)
    {
        gameConnection = new GameConnection(this, this, gameSetup.getByteArray(IP_BYTE_BUNDLE_KEY));
    }

    @Override
    public void onGameConnectionStarted()
    {

    }

    @Override
    public void onGameUpdateReceived(GameMessage gameMessage, ArrayList<GameMessage> pastMessages)
    {

    }

    @Override
    public void onGameConnectionLost()
    {

    }

    @Override
    public void onGameConnectionFound()
    {

    }

    @Override
    public void onGameConnectionStopped()
    {
        finish();
    }
}
