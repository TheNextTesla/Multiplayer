package independent_study.multiplayer.comm;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.MessageBuffer;

import java.io.IOException;
import java.util.ArrayList;

abstract class GameMessage
{
    protected enum COMMUNICATION_METHOD { SMS, NFC, LAN, NETWORK }

    protected MessageBufferPacker mbp;

    GameMessage(String messageType)
    {
        mbp = MessagePack.newDefaultBufferPacker();

        try
        {
            mbp.packString(messageType);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public byte[] closeAndGetMessageContent()
    {
        try
        {
            mbp.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        return mbp.toByteArray();
    }

    public MessageBufferPacker getMbp()
    {
        return mbp;
    }

    public abstract boolean isComMethodValid(COMMUNICATION_METHOD method);

    protected static void waitUntilNextUnpack(MessageUnpacker unpacker)
    {
        boolean isInterrupted = false;
        try
        {
            while(!unpacker.hasNext() && !isInterrupted)
            {
                try
                {
                    Thread.sleep(25);
                }
                catch (Exception ex)
                {
                    isInterrupted = true;
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
