// UdpReceiver defines a Thread for receiving datagrams and passing them to the handler.

package application.core;

import java.io.IOException;
import java.net.*;

public class UdpReceiver extends Thread {
    private final UdpHandler handler;
    private final DatagramSocket socket;

    public UdpReceiver(DatagramSocket socket, UdpHandler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[8192];

        while (true) {
            	// create datagram from the buffer
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length); 
                // waits for datagrams
                try {
					socket.receive(datagram);
				} catch (IOException e) {
					System.out.println("Failed to read datagram packet from buffer: " + e);
				}	
                // handles the datagrams
                handler.handle(datagram); 	

        }
    }
}