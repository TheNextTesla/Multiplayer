package independent_study.multiplayer.comm;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameServerThread extends Thread
{
    private Socket socket;
    private GameConnection gameConnection;
    private volatile long lastHeartbeat;
    private volatile boolean isRunning;

    public GameServerThread(Socket socket, GameConnection gameConnection)
    {
        this.socket = socket;
        this.gameConnection = gameConnection;
        isRunning = true;
        lastHeartbeat = 0;
    }

    @Override
    public void run()
    {
        try
        {
            InputStream inputStream = socket.getInputStream();
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputStream);
            while(isRunning)
            {
                GameMessage gim = null;
                if(unpacker.hasNext())
                {
                    try
                    {
                        String messageType = unpacker.unpackString();
                        switch (messageType)
                        {
                            case GameInitiationMessage.type:
                                gim = GameInitiationMessage.generateInitiationMessage(unpacker);
                                //TODO: Ignore Network-Sent Game Initiation Messages
                                break;
                            case GameHeartbeatMessage.type:
                                gim = GameHeartbeatMessage.generateHeartbeatMessage(unpacker);
                                lastHeartbeat = System.currentTimeMillis();
                                write(gim.closeAndGetMessageContent());
                                break;
                            default:
                                //TODO: How to react to unknown messages
                                break;
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void write(byte[] bytes)
    {
        try
        {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void stopConnection()
    {
        isRunning = false;
    }
}