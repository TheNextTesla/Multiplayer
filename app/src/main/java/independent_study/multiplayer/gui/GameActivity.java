package independent_study.multiplayer.gui;

import android.os.Bundle;

import org.msgpack.core.MessageUnpacker;

import independent_study.multiplayer.R;
import independent_study.multiplayer.comm.GameConnection;
import independent_study.multiplayer.util.DispatchActivity;

public class GameActivity extends DispatchActivity
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

        Bundle gameSetup = savedInstanceState.getBundle(GAME_SETUP_BUNDLE_KEY);
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
        gameConnection = new GameConnection();
    }

    private void setUpConnection(Bundle gameSetup)
    {
        gameConnection = new GameConnection(gameSetup.getByteArray(IP_BYTE_BUNDLE_KEY));
    }
}
