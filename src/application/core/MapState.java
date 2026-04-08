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
	    try {
	        byte type = packet.getData()[0];

	        if (type != 2) {
	            System.out.println("Unexpected packet type: " + type);
	            return null;
	        }

	        ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 1, packet.getLength() - 1);
	        ObjectInputStream ois = new ObjectInputStream(bis);

	        return (MapState) ois.readObject();

	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
