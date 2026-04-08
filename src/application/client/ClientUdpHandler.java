package application.client;

import java.net.*;

import java.util.Scanner;


import application.core.*;
import javafx.application.Platform;;

public class ClientUdpHandler implements UdpHandler {
    private DatagramSocket socket;
    private UdpTransmitter UdpT;
    private Map map = Map.getInstance();
    private static Chat chat = Chat.getInstance();
	
    public ClientUdpHandler(DatagramSocket socket) {
    	this.socket = socket;
    	UdpT = UdpTransmitter.getInstance(socket);
    }
	
    @Override
    public void handle(DatagramPacket packet) {
    	byte[] data = packet.getData();
    	byte header = data[0];

    	switch (header) {
    	        case 0x01:
    	            handleMessage(packet);
    	            break;
    	        case 0x02:
    	            handleMap(packet);
    	            break;
    	    	case 0x05 : 
    	    		handleKick(packet);
    	        default:
    	            System.out.println("Unknown packet type: " + header);
    	    }
    	}
    private void handleKick(DatagramPacket packet) {
        Platform.runLater(() -> {
        	UdpTransmitter udpT = UdpTransmitter.getInstance();
        	udpT.signOn(Main.serverAddress, Main.port, Main.showUsernameDialog());
        });
	}

	public void handleMessage(DatagramPacket packet) {
        String message = new String(packet.getData(), 1, packet.getLength() - 1);
        
        Platform.runLater(() -> {
            chat.add(message); 
            chat.getGridPane(); 
            /*
            System.out.printf("[Server] %s \n", message);
            System.out.printf("chat:");

            for(String s : chat.chat) {
                System.out.println("/t" + s);
            }
            */
        });
    }
    public void handleMap(DatagramPacket packet) {
        MapState state = MapState.read(packet); // helper to read the object
        Platform.runLater(() -> {
            Map.setInstance(state); // This method should handle the actual circle movement
        });
        
    }
}