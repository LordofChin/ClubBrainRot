package application.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
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
        System.out.printf("Server started on port %s. Waiting for players to join.\n", socket.getLocalSocketAddress());
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
        byte[] rawData = packet.getData();
        byte header = rawData[0];
        byte[] data = Arrays.copyOfRange(rawData, 1, packet.getLength());;
        String message = new String(packet.getData(), 1, packet.getLength() - 1);

        
        // switch case dependent on message header
        switch (header) {
	    	case 0x01 -> {
	    		System.out.printf("message received from %s\n", sadd);
	    		handleMessage(message,sadd);//Arrays.copyOfRange(data, 1, data.length), ip, port);
	    	}
	    	case 0x03 -> {
	    		System.out.printf("movement received from %s\n", sadd);
	    		handleMovement(data[0],sadd);
	        	}
	    	case 0x04 -> {
	    		System.out.printf("sign-on request received from %s\n", sadd);
	    		handleSignOn(message,sadd);
	    	}
	    	case 0x05 -> {
	    		System.out.printf("user customization received from %s\n", sadd);
	    		handleUserCustomization(data, sadd);
	    	}
	    	default ->
	        	System.out.println("Unknown packet type: " + header);
	    	}
	}
    
    private void handleUserCustomization(byte [] data, SocketAddress sadd) {
    	ByteBuffer wrapped = ByteBuffer.wrap(data); // big-endian by default
    	int r = wrapped.getInt(); // 1
    	int g = wrapped.getInt(); // 2
    	int b = wrapped.getInt(); // 3
    	StringBuilder sb = new StringBuilder();

    	for (int i = 12; i < data.length; i += 2) {
    	    char c = wrapped.getChar(i);
    	    if (c == '\0') break;
    	    sb.append(c);
    	}

    	String username = sb.toString();
        
    	User user = players.get(sadd);
    	
    	/*
    	System.out.println(r);
    	System.out.println(g);
    	System.out.println(b);
    	System.out.println(username);
    	*/
    	System.out.println(map.users);
		map.users.remove(user);		// refresh the map or have the worst debug nightmare of your life
    	user.setR(r);
    	user.setG(g);
    	user.setB(b);
    	user.setUsername(username);
		map.users.add(user);
    	System.out.println(map.users);
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
    	System.out.println(msg);
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