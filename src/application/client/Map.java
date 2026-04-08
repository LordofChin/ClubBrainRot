package application.client;

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashSet;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import application.core.MapState;
import application.core.User;


public class Map extends StackPane{
	public static Map instance;
	public static Chat chat = Chat.getInstance();
	
	private Map (){
		super();
	}

	
	public static Map getInstance(Map map)
	{
		if (instance == null)
		{
			instance = map;
			map.setAlignment(Pos.CENTER);
			return instance;
		}
		else 
		{
			return instance;
		}
	}
		
	public static Map getInstance()
	{
		if (instance == null)
		{
			Map map = new Map();
			instance = map;
			return map;
		}
		else 
		{
			return instance;
		}
	}
	
	public static void setInstance(MapState state) {
	    // 1. Move to the JavaFX Thread
	    Platform.runLater(() -> {
	        HashSet<User> users = state.users;
	        
	        instance.getChildren().clear(); // clear all old circles

	        for (User u : users) {
	            //System.out.println("Updating player: " + u.getUsername());
	            
	            Double[] coords = new Double [] {u.x, u.y};
	            Circle circle = new Circle(40); // Smaller radius looks better
	            
	            circle.setTranslateX(coords[0]);
	            circle.setTranslateY(coords[1]);
	            Label lbl = new Label();
	            lbl.setText(u.username);
	            lbl.setTranslateX(coords[0]);
	            lbl.setTranslateY(coords[1]);
	            
	            if (u.moving) {
		            lbl.setTextFill(Color.rgb(u.r,u.g, u.b));
	            	circle.setFill(Color.rgb(255 - u.r, 255 - u.g,255 - u.b));
	            }
	            else {
		            lbl.setTextFill(Color.rgb(255 - u.r,255 - u.g,255 - u.b));
	            	circle.setFill(Color.rgb(u.r,u.g,u.b));
	            }
	            
	            
	            //System.out.println(coords[0]);
	            //System.out.println(coords[1]);
	            
	            instance.getChildren().addAll(circle, lbl);
	        }
	    });
	}
	
	public void add(Circle circle)
	{
		instance.getChildren().add(circle);
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
		    oos.flush();

		    return baos.toByteArray();
		} catch (IOException e)
		{
			System.err.print(e);
			return null;
		}
	}

	public Map read(DatagramPacket packet) {
	    try {
	        byte type = packet.getData()[0];

	        if (type != 2) {
	            System.out.println("Unexpected packet type: " + type);
	            return null;
	        }

	        ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 1, packet.getLength() - 1);
	        ObjectInputStream ois = new ObjectInputStream(bis);

	        /*
	        Map newMap = (Map) ois.readObject();

	        // copy data into current instance
	        this.width = newMap.width;
	        this.height = newMap.height;
	        this.tiles = newMap.tiles;
	        */


	        return this;

	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
