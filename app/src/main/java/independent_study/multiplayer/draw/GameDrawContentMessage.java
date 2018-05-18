package independent_study.multiplayer.draw;

import android.graphics.PointF;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.ArrayList;

import independent_study.multiplayer.comm.GameContentMessage;

public class GameDrawContentMessage extends GameContentMessage
{
    public GameDrawContentMessage(ArrayList<PointF> points)
    {
        super(pointsToBytes(points));
    }

    private static byte[] pointsToBytes(ArrayList<PointF> points)
    {
        byte[] packedBytes;
        try
        {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packArrayHeader(points.size());
            for (PointF point : points)
            {
                packer.packFloat(point.x);
                packer.packFloat(point.y);
            }
            packer.flush();
            packedBytes = packer.toByteArray();
            packer.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            packedBytes = new byte[0];
        }
        return packedBytes;
    }

    public static ArrayList<PointF> bytesToPoints(byte[] bytes)
    {
        ArrayList<PointF> points = new ArrayList<>();
        try
        {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
            int length = unpacker.unpackArrayHeader();
            for(int i = 0; i < length; i++)
            {
                float x = unpacker.unpackFloat();
                float y = unpacker.unpackFloat();
                points.add(new PointF(x, y));
            }
            unpacker.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return points;
    }
}
