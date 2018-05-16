package independent_study.multiplayer.comm;

import android.util.Log;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

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
                if(unpacker.hasNext())
                {
                    try
                    {
                        String messageType = unpacker.unpackString();
                        switch (messageType)
                        {
                            case GameInitiationMessage.type:
                                GameInitiationMessage gim = GameInitiationMessage.generateInitiationMessage(unpacker);
                                Log.d("GameServerThread", "Lost GIM Found @ " + Arrays.toString(gim.getIpAddress()));
                                break;
                            case GameHeartbeatMessage.type:
                                GameHeartbeatMessage ghm = GameHeartbeatMessage.generateHeartbeatMessage(unpacker);
                                lastHeartbeat = System.currentTimeMillis();
                                write(ghm.closeAndGetMessageContent());
                                break;
                            case GameContentMessage.type:
                                GameContentMessage gcm = GameContentMessage.generateContentMessage(unpacker);
                                gameConnection.onGameUpdateReceived(gcm);
                            default:
                                //TODO: How to react to unknown messages?
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

    public long getLastHeartbeat()
    {
        return lastHeartbeat;
    }

    public void stopConnection()
    {
        isRunning = false;
    }
}