// UdpHandler defines an interface for handling datagrams. 

package application.core;

import java.net.DatagramPacket;

public interface UdpHandler {
	
	public void handle(DatagramPacket datagram);
}