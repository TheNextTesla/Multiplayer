package independent_study.multiplayer.comm;

import android.content.pm.PackageManager;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import independent_study.multiplayer.nfc.InterpreterNFC;
import independent_study.multiplayer.util.Utilities;

public class GameConnection extends Thread
{
    private volatile boolean isRunning;
    private volatile boolean isConnected;
    private ArrayList<GameServerThread> serverThreads;
    private ArrayList<GameMessage> receivedMessages;
    private boolean isHost;

    private byte[] ipAddressOther;
    private ServerSocket serverSocket;

    public GameConnection()
    {
        isHost = true;
        isRunning = true;
        isConnected = false;
        serverThreads = new ArrayList<>();
        receivedMessages = new ArrayList<>();
        try
        {
            serverSocket = new ServerSocket(Utilities.CONNECTION_PORT);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public GameConnection(byte[] ipAddressOther)
    {
        isHost = false;
        isRunning = true;
        isConnected = false;
        serverThreads = new ArrayList<>();
        this.ipAddressOther = ipAddressOther;
    }

    @Override
    public void run()
    {
        if(isHost)
            runIfHost();
        else
            runIfConnector();
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
                serverThreads.add(gst);
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
        serverThreads.add(gst);

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

    }
}
