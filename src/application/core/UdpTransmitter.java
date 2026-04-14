package application.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Set;

import application.client.NoInterenetGame;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class UdpTransmitter {
    private static UdpTransmitter instance = null;
    private DatagramSocket socket;

    // private constructor for getInstance method
    private UdpTransmitter(DatagramSocket socket) {
        this.socket = socket;
    }

    // returns a single static instance of UdpTransmitter per server/client
    public static UdpTransmitter getInstance(DatagramSocket socket) {
        if (instance == null) {
            instance = new UdpTransmitter(socket);
        }
        return instance;
    }
    
    public static UdpTransmitter getInstance() {
        return instance;
    }

    // used by sever and client for sending a message
    public void send(InetAddress ip, int port, String message) {
        if (instance == null) {
            System.err.println("DatagramSender instance is not initialized.");
            return;
        }
        try {
    	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	    baos.write(0x01);
    	    baos.write(message.getBytes());

    	    byte[] data = baos.toByteArray();
            DatagramPacket datagram = new DatagramPacket(data, data.length, ip, port);
            socket.send(datagram);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    // used by server for sending maps updates
    public void send(DatagramPacket packet) {
        if (instance == null) {
            System.err.println("DatagramSender instance is not initialized.");
            return;
        }
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    // used for by client for player movement
    public void move(InetAddress ip, int port, char direction) {
        if (instance == null) {
            System.err.println("DatagramSender instance is not initialized.");
            return;
        }
        try {
    	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	    baos.write(0x03);
    	    baos.write( (byte) direction);

    	    byte[] data = baos.toByteArray();
            DatagramPacket datagram = new DatagramPacket(data, data.length, ip, port);
            socket.send(datagram);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    // used by client for sign-on
    public void signOn(InetAddress ip, int port, String message) {
        if (instance == null) {
            System.err.println("DatagramSender instance is not initialized.");
            return;
        }
        try {
    	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	    baos.write(0x04);
    	    baos.write(message.getBytes());

    	    byte[] data = baos.toByteArray();
            DatagramPacket datagram = new DatagramPacket(data, data.length, ip, port);
            socket.send(datagram);
        } catch (IOException e) {
			// start no internet game if the client can't create a socket
    		Platform.runLater(() -> {
    		    NoInterenetGame game = new NoInterenetGame(Color.RED, "player");
    		    Stage gameStage = new Stage();
    		    game.start(gameStage);
    		});
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    @Override
    public String toString()
    {
    	return (instance != null) ? "instantiated" : "not instantiated";
    }
    
    //broadcasts/multicasts raw byte data to arraylists of ips and ports 
    public void multicast(byte[] data, Set<SocketAddress> sockets) 
    {
    	
    	for (SocketAddress sadd : sockets)
    	{
    		DatagramPacket p = new DatagramPacket(data, data.length, sadd);
    		send(p);
    	}
    }
}
