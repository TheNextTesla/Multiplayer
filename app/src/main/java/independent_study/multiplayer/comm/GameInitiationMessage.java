package independent_study.multiplayer.comm;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.IOException;

import static independent_study.multiplayer.comm.GameMessage.COMMUNICATION_METHOD.NFC;
import static independent_study.multiplayer.comm.GameMessage.COMMUNICATION_METHOD.SMS;

public class GameInitiationMessage extends GameMessage
{
    protected static final COMMUNICATION_METHOD[] validMethods = {SMS, NFC};
    protected static final String type = "init";

    //https://stackoverflow.com/questions/8811315/how-to-get-current-wifi-connection-info-in-android
    public GameInitiationMessage(String hostname, byte[] ipAddress)
    {
        super(type);

        try
        {
            mbp.packString(hostname);
            mbp.packByte(ipAddress[0]);
            mbp.packByte(ipAddress[1]);
            mbp.packByte(ipAddress[2]);
            mbp.packByte(ipAddress[3]);
            mbp.flush();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
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
