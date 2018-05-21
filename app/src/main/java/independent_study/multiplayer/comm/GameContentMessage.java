package independent_study.multiplayer.comm;

import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class GameContentMessage extends GameMessage
{
    public static final String type = "content";

    public static GameContentMessage generateContentMessage(MessageUnpacker messageUnpacker)
    {
        try
        {
            waitUntilNextUnpack(messageUnpacker);
            byte[] content = new byte[messageUnpacker.unpackArrayHeader()];
            for(int i = 0; i < content.length; i++)
            {
                waitUntilNextUnpack(messageUnpacker);
                content[i] = messageUnpacker.unpackByte();
            }
            return new GameContentMessage(content);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    private byte[] gameContent;

    public GameContentMessage(byte[] gameContent)
    {
        super(type);
        this.gameContent = gameContent;
        try
        {
            mbp.packArrayHeader(gameContent.length);
            for(int i = 0; i < gameContent.length; i++)
            {
                mbp.packByte(gameContent[i]);
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    @Override
    public boolean isComMethodValid(COMMUNICATION_METHOD method)
    {
        return true;
    }

    public byte[] getGameContent()
    {
        return gameContent;
    }
}
