package independent_study.multiplayer.comm;


import org.msgpack.core.MessageUnpacker;

public class GameHeartbeatMessage extends GameMessage
{
    public static final String type = "beat";

    public static GameHeartbeatMessage generateHeartbeatMessage(MessageUnpacker messageUnpacker)
    {
        return new GameHeartbeatMessage();
    }

    public GameHeartbeatMessage()
    {
        super(type);
    }

    public boolean isComMethodValid(COMMUNICATION_METHOD method)
    {
        return true;
    }
}
