import java.io.*;
import java.net.*;

public class RouterSender extends Thread {
	private MulticastSocket socket;
	private RoutingTableAlgo sendingTable;
	private int port;
	DatagramPacket outPacket = null;

	public RouterSender(MulticastSocket updateSocket, RoutingTableAlgo rta, int portnumber) {
		this.socket = updateSocket;
		this.sendingTable = rta;
		this.port = portnumber;
	}

	public void run() {
		
		int Count = 0;
		try {
			while (true) {
				Thread.sleep(15000);
				sendingTable.updateOnCostChange();
				InetAddress address = InetAddress.getByName("239.255.255.250");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(sendingTable);
				byte[] buf = baos.toByteArray();
				outPacket = new DatagramPacket(buf, buf.length, address, port);
				socket.send(outPacket);
				System.out.println("Output Number:" + ++Count + ":");
				sendingTable.displayRoutingTable(sendingTable.getRoutingTables());
				System.out.println();
//				try {
//					Thread.sleep(500);
//
//				} catch (InterruptedException e) {
//				}
			}

		} catch (IOException e) {
			System.out.println(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
