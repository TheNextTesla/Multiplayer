package independent_study.multiplayer.comm;

import static independent_study.multiplayer.comm.GameMessage.COMMUNICATION_METHOD.NFC;
import static independent_study.multiplayer.comm.GameMessage.COMMUNICATION_METHOD.SMS;

public class GameInitiationMessage extends GameMessage
{
    protected static final COMMUNICATION_METHOD[] validMethods = {SMS, NFC};
    protected static final String type = "init";

    public GameInitiationMessage(byte[] conditions)
    {
        super(type, conditions);
    }

    public boolean isComMethodValid(COMMUNICATION_METHOD method)
    {
        for(COMMUNICATION_METHOD loopMethod : validMethods)
        {
            if(method == loopMethod)
                return true;
        }
        return false;
    }
}
