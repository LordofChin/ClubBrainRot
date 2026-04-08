package application.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;

import application.core.*;

public class Main {
	static Users users = Users.getInstance();

	public static void main(String[] args) 
	{
			int port = 3478;
            try 
            {
            	MapState map = MapState.getInstance();
                DatagramSocket socket = new DatagramSocket(port);
                ServerUdpHandler udpHandler = new ServerUdpHandler(socket);
    			UdpReceiver receiver = new UdpReceiver(socket, udpHandler);
    			receiver.start();


            } 
            catch (SocketException e) {
                System.err.println("Error initializing server socket: " + e.getMessage());
            }

            // map mulicasting logic
            Thread mapMulticaster = new Thread(() -> {
            	while(true)
            	{
            		try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		UdpTransmitter udpT = UdpTransmitter.getInstance();
            		
            		// update players positions
                    MapState state = new MapState();
                    for (User p : ServerUdpHandler.players.values()) {
                    	Double xv = p.velocity[0];
                    	Double yv = p.velocity[1];
                    	
                    	// update position and decrement velocity magnitude
                    	if (xv > 0)
                    	{
                    		p.x += xv;	
                			p.setVelocity(new Double [] {p.velocity[0] - 1, p.velocity[1]});
                    	}
                    	if (xv < 0)
                    	{
                    		p.x += xv;  
                			p.setVelocity(new Double [] {p.velocity[0] + 1, p.velocity[1]});

                    	}
                    	if (yv > 0)		
                    	{
                    		p.y += yv; 	
                			p.setVelocity(new Double [] {p.velocity[0], p.velocity[1] - 1});
                    	}
                    	if (yv < 0)		
                    	{
                    		p.y += yv; 
                			p.setVelocity(new Double [] {p.velocity[0], p.velocity[1] + 1});
                    	}
                        state.users.add(p);
                    }
            		MapState map = MapState.getInstance();                    
                    map.setInstance(state);
                    
        			udpT.multicast(map.toBytes(), ServerUdpHandler.players.keySet());
            	}
            });
            mapMulticaster.start();
            
            // time to live ticker finds inactive users and kicks them.
            // it also handles moving animation logic
            Thread ttlTicker = new Thread(() -> {
            	while(true)
            	{
            		try {
						Thread.sleep(250); // wait one full human reaction time period 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		HashMap<SocketAddress, User> players = ServerUdpHandler.players;
        			for (Entry<SocketAddress, User> e : players.entrySet()) 
        			{
        				SocketAddress sadd = e.getKey();
        				User u = e.getValue();
        				if(u.velocity[0] == 0 && u.velocity[1] == 0)// 	👀		// if your velocity is zero
        				{
        					u.moving = false; 									// then you aren't moving
        				}
        				if (u.ttl > 0)
        				{
        					u.ttl--;
        				}
        				else
        					kickUser(e);
        			}
            	}
            });
            ttlTicker.start();
    }
	private static void kickUser(Entry<SocketAddress, User> e)
	{
	// helpful fields
		SocketAddress sadd = e.getKey();
		User u = e.getValue();
		UdpTransmitter udpT = UdpTransmitter.getInstance();
		
	// notification logic
		// notify everyone
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.write(0x01); 	// message header
	    try {
			baos.write(("[SERVER] User kicked: " + u.getUsername()).getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    byte[] data = baos.toByteArray();
		udpT.multicast(data, ServerUdpHandler.players.keySet());
		
		//notify individual
		byte bootcode[] = {0x05};
		DatagramPacket packet = new DatagramPacket(bootcode, 1, sadd);
		udpT.send(packet);
		
	// removal logic
		ServerUdpHandler.players.remove(sadd);
		MapState map = MapState.getInstance();
		map.users.remove(u);
	}
}
