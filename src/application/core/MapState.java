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
    private static final long serialVersionUID = 1L; 	// veriosn ID is required by all serializable objects
    // Just store names and positions, NOT Circles
    public HashSet<User> users = new HashSet<>();		// HashSet of users
    public static MapState instance;					// static instance of MapState for each client and server

    // singleton design pattern
	public static MapState getInstance()
	{
		if (instance == null)
		{
			MapState mapState = new MapState();
			instance = mapState;
		}
			return instance;
	}
    
	// method to read in MapStates from bytes in packets
	public static MapState read(DatagramPacket packet) {

        // capture all the packet data in a byte array input stream
        ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 1, packet.getLength() - 1); // skip header
        
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
	
	// write the MapState to a Packet for the specified sadd
	public DatagramPacket write(SocketAddress sadd) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0x02); // prepend type byte

		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(instance);
			oos.flush();

		} catch (IOException e) {
			System.out.println("Failed to write MapState to object: " + e);
			return null;
		}
		
		byte[] data = baos.toByteArray();
		return new DatagramPacket(data, data.length, sadd);
	}
	
	public byte[] toBytes()
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.write(0x02); // prepend type byte
	    try {
	    	ObjectOutputStream oos = new ObjectOutputStream(baos);
	    	oos.writeObject(getInstance());
	    	oos.reset(); 
	    	oos.flush();
	    } catch (IOException e) {
	    	
	    }

	    return baos.toByteArray();

	}
}
