package independent_study.multiplayer.comm;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import independent_study.multiplayer.util.DispatchActivity;
import independent_study.multiplayer.util.DispatchReceiver;
import independent_study.multiplayer.util.Utilities;

public class GameConnection extends Thread implements DispatchReceiver
{
    private volatile boolean isRunning;
    private volatile boolean isConnected;
    private final ArrayList<GameServerThread> serverThreads;
    private final ArrayList<GameMessage> receivedMessages;
    private boolean isHost;

    private byte[] ipAddressOther;
    private ServerSocket serverSocket;
    private NetworkGameListener ngl;

    public GameConnection(NetworkGameListener ngl, DispatchActivity dispatchActivity)
    {
        isHost = true;
        isRunning = true;
        isConnected = false;
        serverThreads = new ArrayList<>();
        receivedMessages = new ArrayList<>();

        this.ngl = ngl;
        dispatchActivity.addDispatchReceivers(this);

        try
        {
            serverSocket = new ServerSocket(Utilities.CONNECTION_PORT);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public GameConnection(NetworkGameListener ngl, DispatchActivity dispatchActivity, byte[] ipAddressOther)
    {
        isHost = false;
        isRunning = true;
        isConnected = false;
        serverThreads = new ArrayList<>();
        receivedMessages = new ArrayList<>();
        this.ipAddressOther = ipAddressOther;

        this.ngl = ngl;
        dispatchActivity.addDispatchReceivers(this);
    }

    @Override
    public void run()
    {
        if(isHost)
            runIfHost();
        else
            runIfConnector();
    }

    public void onGameUpdateReceived(GameMessage gcm)
    {
        synchronized (receivedMessages)
        {
            receivedMessages.add(gcm);
        }
        ngl.onGameUpdateReceived(gcm, getGameMessages());
    }

    private void runIfHost()
    {
        while(isRunning)
        {
            try
            {
                Socket socket = serverSocket.accept();
                GameServerThread gst = new GameServerThread(socket, this);
                gst.start();

                synchronized (serverThreads)
                {
                    serverThreads.add(gst);
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            finally
            {
                try
                {
                    sleep(50);
                    maintainConnection();
                }
                catch (InterruptedException ie)
                {
                    ie.printStackTrace();
                }
            }
        }
    }

    private void runIfConnector()
    {
        Socket connectionSocket = null;
        while(connectionSocket == null && isRunning)
        {
            try
            {
                connectionSocket = new Socket(Inet4Address.getByAddress(ipAddressOther), Utilities.CONNECTION_PORT);
                connectionSocket.setSoTimeout(100);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                connectionSocket = null;
            }
        }

        GameServerThread gst = new GameServerThread(connectionSocket, this);
        gst.start();

        synchronized (serverThreads)
        {
            serverThreads.add(gst);
        }

        while(isRunning)
        {
            try
            {
                sleep(100);
                maintainConnection();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void maintainConnection()
    {
        long lastHeartbeatMessageReceived = 0;
        synchronized (serverThreads)
        {
            for (GameServerThread thread : serverThreads)
            {
                if (thread.getLastHeartbeat() > lastHeartbeatMessageReceived)
                    lastHeartbeatMessageReceived = thread.getLastHeartbeat();
            }
        }

        isConnected = System.currentTimeMillis() - lastHeartbeatMessageReceived < 500;

        if(!isConnected)
        {
            Log.d("GameConnection", "Not Connected");
        }
        else
        {
            synchronized (serverThreads)
            {
                if(serverThreads.size() > 1)
                {
                    for (int i = serverThreads.size() - 1; i > 0; i--)
                    {
                        if (System.currentTimeMillis() - serverThreads.get(i).getLastHeartbeat() > 550)
                        {
                            serverThreads.remove(i);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        maintainConnection();
    }

    @Override
    public void onPause()
    {
        //TODO: Functionality on Pause?
    }

    @Override
    public void onStop()
    {
        //TODO: Functionality on Stop?
    }

    @Override
    public void onDestroy()
    {
        isRunning = false;
    }

    public ArrayList<GameMessage> getGameMessages()
    {
        ArrayList<GameMessage> gameMessages = new ArrayList<>();
        synchronized (receivedMessages)
        {
            gameMessages.addAll(receivedMessages);
        }
        return gameMessages;
    }

    public boolean isConnected()
    {
        return isConnected;
    }
}
