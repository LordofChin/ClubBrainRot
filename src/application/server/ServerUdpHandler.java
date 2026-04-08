package application.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import application.core.MapState;
import application.core.UdpHandler;
import application.core.UdpTransmitter;
import application.core.User;
import javafx.scene.shape.Circle;



public class ServerUdpHandler implements UdpHandler {
	// object for tracking historical users
    private static ArrayList<User> users = new ArrayList<>();
    // object for tracking logged in users
    public static HashMap<SocketAddress, User> players = new HashMap<>();
    
    //public static Users histUsers = Users.getInstance();
    // socket of the server (exists on a port)
    private static DatagramSocket socket;
    // for transmitting datagrams to all users
    private static UdpTransmitter udpT;
    
    // game logic
	static MapState map = MapState.getInstance();      // map instance to track lobby map
	static String [] chat = new String [10]; // chat array list to show last 10 messages
	
    public ServerUdpHandler(DatagramSocket socket) {
    	this.socket = socket;
    	udpT = UdpTransmitter.getInstance(socket);
        System.out.printf("Server Properly started on port %s. Waiting for players to join.\n", socket.getLocalSocketAddress());
    }

    @Override
    public void handle(DatagramPacket packet) {
    	// sender fields
    	/*
        InetAddress ip = packet.getAddress();
        int port = packet.getPort();
        */
        SocketAddress sadd = packet.getSocketAddress();
        
        // payload fields 
        byte[] data = packet.getData();
        byte header = data[0];
        String message = new String(packet.getData(), 1, packet.getLength() - 1);

        
        // switch case dependent on message header
        switch (header) {
	    	case 0x01 -> {
	    		System.out.printf("message received from %s\n", sadd);
	    		handleMessage(message,sadd);//Arrays.copyOfRange(data, 1, data.length), ip, port);
	    	}
	    	case 0x03 -> {
	    		System.out.printf("movement received from %s\n", sadd);
	    		handleMovement(data[1],sadd);
	        	}
	    	case 0x04 -> {
	    		System.out.printf("sign-on request received from %s\n", sadd);
	    		handleSignOn(message,sadd);
	    	}
	    	default ->
	        	System.out.println("Unknown packet type: " + header);
	    	}
	}
    
    //method to handle movements
    public static void handleMovement(byte b, SocketAddress sadd) 
    {
    	User user = players.get(sadd);
    	user.ttl = 1000;		// ~1000 ttls = 4 minutes * 60 seconds * 4 reaction frames
    	user.moving = true;
    	char move = (char) b;
        switch (b) 
        {
        	case 'w' -> updateVelocity(user, user.getVelocity()[0], -10);
        	case 'a' -> updateVelocity(user, -10, user.getVelocity()[1]);
        	case 's' -> updateVelocity(user, user.getVelocity()[0], 10);
        	case 'd' -> updateVelocity(user, 10, user.getVelocity()[1]);
        	default -> System.out.println("Not a valid move: ");
        }
    }
    
    // helper method to handleMovement method
    public static void updateVelocity(User user, double deltax, double deltay) 
    {    	
    	user.velocity = new Double [] {deltax, deltay};
    	// System.out.printf("Player moved %.2f horizontally and %.2f vertically\n", deltax, deltay);
    }
    
    // method to handle messages
    public static void handleMessage(String message, SocketAddress sadd) 
    {
    	User user = players.get(sadd);

    	if (message.contains("quit")) // base case 
    	{
    		user.ttl = 0;
    		return;
    	}

    	user.ttl = 1000;		// ~1000 ttls = 4 minutes * 60 seconds * 4 reaction frames
    	String msg = String.format("%s: %s", user.getUsername(), message);
    	System.out.printf(msg);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.write(0x01);
	    try {
			baos.write(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	    byte[] data = baos.toByteArray();
    	udpT.multicast(data, players.keySet());
    }
    
    // method to handle sign-ons
    public static void handleSignOn(String uname, SocketAddress sadd) 
    {
    	for (User u : players.values())
    	{
    		if(uname.equalsIgnoreCase(u.getUsername()))
    		{
    			System.out.printf("%s LOGGED IN:\n%s\n\n", uname, u);
    			users.add(u);
    			players.put(sadd,u);
    			map.users.add(u);
    			
    			// notify everyone
    		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		    baos.write(0x01); 	// message header
    		    try {
    				baos.write(("[SERVER] User joined: " + u.getUsername()).getBytes());
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		    byte[] data = baos.toByteArray();
    			udpT.multicast(data, ServerUdpHandler.players.keySet());
    			
    			return;
    		}
    	}
    	
		User nUsr = new User(uname);
		users.add(nUsr);
		players.put(sadd, nUsr);
		map.users.add(nUsr);
        System.out.printf("USER CREATED:\n%s\n", nUsr);
        
		// notify everyone
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.write(0x01); 	// message header
	    try {
			baos.write(("[SERVER] User created: " + nUsr.getUsername()).getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    byte[] data = baos.toByteArray();
		udpT.multicast(data, ServerUdpHandler.players.keySet());
    }
}