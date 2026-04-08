// UdpReceiver defines a Thread for receiving datagrams and passing them to the handler.

package application.core;

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
            try {
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length); // creates datagram from the buffer

                socket.receive(datagram);	// waits for datagrams

                handler.handle(datagram); 	// handles the datagrams

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}