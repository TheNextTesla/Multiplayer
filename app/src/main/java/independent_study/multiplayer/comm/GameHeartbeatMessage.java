package independent_study.multiplayer.comm;


public class GameHeartbeatMessage extends GameMessage
{
    protected static final String type = "beat";

    public GameHeartbeatMessage()
    {
        super(type);
    }

    public boolean isComMethodValid(COMMUNICATION_METHOD method)
    {
        return true;
    }
}
