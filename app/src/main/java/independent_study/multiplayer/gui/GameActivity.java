package independent_study.multiplayer.gui;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.msgpack.core.MessageUnpacker;

import java.util.ArrayList;

import independent_study.multiplayer.R;
import independent_study.multiplayer.comm.GameConnection;
import independent_study.multiplayer.comm.GameContentMessage;
import independent_study.multiplayer.comm.GameInitiationMessage;
import independent_study.multiplayer.comm.GameMessage;
import independent_study.multiplayer.comm.NetworkGameListener;
import independent_study.multiplayer.draw.GameDrawContentMessage;
import independent_study.multiplayer.draw.PaintView;
import independent_study.multiplayer.util.DispatchActivity;

public class GameActivity extends DispatchActivity implements NetworkGameListener
{
    public static final String IS_HOST_BUNDLE_KEY = "isHost";
    public static final String IP_BYTE_BUNDLE_KEY = "ip";
    public static final String GAME_SETUP_BUNDLE_KEY = "GameSetup";

    private GameConnection gameConnection;
    private PaintView paintView;

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
        paintView = new PaintView(this, null, this);
        setContentView(paintView);
    }

    @Override
    public void onGameUpdateReceived(GameMessage gameMessage, ArrayList<GameMessage> pastMessages)
    {
        if(gameMessage instanceof GameContentMessage)
        {
            byte[] content = ((GameContentMessage) gameMessage).getGameContent();
            ArrayList<PointF> networkPoints = GameDrawContentMessage.bytesToPoints(content);
            paintView.drawPointsPath(networkPoints);
        }
        else
        {
            Log.d("GameActivity", "Game Update Not Recognized");
        }
    }

    @Override
    public void onGameConnectionLost()
    {
        Toast.makeText(this, "Connection Lost", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameConnectionFound()
    {
        Toast.makeText(this, "Connection Found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameConnectionStopped()
    {
        finish();
    }

    public void onDrawPathCreated(ArrayList<PointF> points)
    {
        GameDrawContentMessage gscm = new GameDrawContentMessage(points);
        gameConnection.send(gscm);
    }
}
