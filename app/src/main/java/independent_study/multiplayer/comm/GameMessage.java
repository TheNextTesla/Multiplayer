package independent_study.multiplayer.comm;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.buffer.MessageBuffer;

import java.util.ArrayList;

abstract class GameMessage
{
    protected enum COMMUNICATION_METHOD { SMS, NFC, LAN, NETWORK }
    protected static final MessageBufferPacker messagePack = MessagePack.newDefaultBufferPacker();

    private String messageType;
    private byte[] messageContents;

    GameMessage(String messageType, byte[] messageContents)
    {
        this.messageType = messageType;
        this.messageContents = messageContents;
    }

    public byte[] generateOutputForSMS()
    {
        return null;
    }

    public byte[] generateOutputForNFC()
    {
        return null;
    }

    public byte[] generateOutputForLAN()
    {
        return null;
    }

    public byte[] generateOutputForNETWORK()
    {
        return null;
    }

    public abstract boolean isComMethodValid(COMMUNICATION_METHOD method);
}
