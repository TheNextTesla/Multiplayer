package independent_study.multiplayer.comm;

import java.util.ArrayList;

public interface NetworkGameListener
{
    void onGameConnectionStarted();
    void onGameConnectionLost();
    void onGameUpdateReceived(GameMessage gameUpdate, ArrayList<GameMessage> pastMessages);
    void onGameConnectionFound();
    void onGameConnectionStopped();
}
