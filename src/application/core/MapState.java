package application.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;

import javafx.application.Platform;
import javafx.scene.shape.Circle;


public class MapState implements Serializable 
{
    private static final long serialVersionUID = 1L;
    // Just store names and positions, NOT Circles
    public HashSet<User> users = new HashSet<>();
    public static MapState instance;

	public static MapState getInstance()
	{
		if (instance == null)
		{
			MapState mapState = new MapState();
			instance = mapState;
		}
			return instance;
	}
    
	public static MapState read(DatagramPacket packet) {
        byte type = packet.getData()[0];

        // make sure its a map state change
        if (type != 2) {
            System.out.println("Unexpected packet type: " + type);
            return null;
        }

        // capture all the packet data in a byte array input stream
        ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 1, packet.getLength() - 1);
        instance = new MapState();
	    
        // try to pass the bis into an object input stream to be casted into a MapState Object
        try {
	        ObjectInputStream ois = new ObjectInputStream(bis);

	        instance = (MapState) ois.readObject();
	        return instance;

	    } catch (IOException e) {
			System.out.println("Failed to read MapState bytes from server into a MapState object");
	        return null;
	    } catch (ClassNotFoundException e) {
			System.out.println("Could not find the class of the MapState packet?");
			e.printStackTrace();
		}
        System.out.println("FAILED TO READ MAPSTATE");
		return null;
	}
	
	public void setInstance(MapState state) {
	    instance = state;
	}
	
	public DatagramPacket write(SocketAddress sadd) {
		try 
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(0x02); // prepend type byte
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(getInstance());
			oos.flush();

			byte[] data = baos.toByteArray();
			return new DatagramPacket(data, data.length, sadd);
		} catch (IOException e)
		{
			System.err.print(e);
			return null;
		}
	}
	
	public byte[] toBytes()
	{
		try 
		{
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    baos.write(0x02); // prepend type byte
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(getInstance());
	        oos.reset(); 
		    oos.flush();

		    return baos.toByteArray();
		} catch (IOException e)
		{
			System.err.print(e);
			return null;
		}
	}
}
