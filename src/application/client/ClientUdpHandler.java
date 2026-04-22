package application.client;

import java.net.*;

import java.util.Scanner;


import application.core.*;
import javafx.application.Platform;;

public class ClientUdpHandler implements UdpHandler 
{
    private Chat chat = Chat.getInstance();
		
    // method to read packet header and decide how to handle received packets
    @Override
    public void handle(DatagramPacket packet) 
    {
    	byte[] data = packet.getData(); // variable not currently used
    	// header is always the 0th byte
    	byte header = data[0];

    	switch (header) {
    	        case 0x01:
    	            handleMessage(packet);	// someone sent you a message!
    	            break;
    	        case 0x02:
    	            handleMap(packet);		// the server is updating your map
    	            break;
    	    	case 0x05 : 
    	    		handleKick(packet);		// you just got kicked
    	        default:
    	            System.out.println("Unknown packet type: " + header);
    	    }
    	}
    
    // handle the kicking process happens for inactivity or if you type q in chat
    private void handleKick(DatagramPacket packet) 
    {
    	// pass the runnable to the javafx application to run later
        Platform.runLater(() -> {
        	// getting kicked simply re-prompts a user to enter their username
        	UdpTransmitter udpT = UdpTransmitter.getInstance();
        	udpT.signOn(Main.serverAddress, Main.port, Main.showUsernameDialog());
        });
	}

    // handle messages
	public void handleMessage(DatagramPacket packet) 
	{
		// disect the message from the raw bytes, start at index 1, because 0 is the header
        String message = new String(packet.getData(), 1, packet.getLength() - 1);
        
        // update chat object
        Platform.runLater(() -> {
            chat.add(message); 
            chat.updateGridPaneChat(); 
        });
    }
	// handle map updates
    public void handleMap(DatagramPacket packet) {
        MapState state = MapState.read(packet); // helper to read the object from packet
        
        // ask the javafx application to update your mapstate
        Platform.runLater(() -> {
            Map.setInstance(state); // This method should handle the actual player movement
        });
        
        
    }
}