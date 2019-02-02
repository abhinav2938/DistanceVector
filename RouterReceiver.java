import java.io.*;
import java.net.*;
import java.util.*;

public class RouterReceiver {
	public static void main(String[] args) throws SocketException, ClassNotFoundException{
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		if(args.length<2) {
			System.out.println("Incorrect invocation");
			System.exit(0);
		}
		ByteArrayInputStream byteArrayInputStream;
		ObjectInputStream objectInputStream;
		MulticastSocket multiCastSocket;

		InetAddress groupAddress;
		byte [] buffer = new byte[10240];
		DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
		try {
			multiCastSocket = new MulticastSocket(Integer.parseInt(args[0]));
			groupAddress = InetAddress.getByName("239.255.255.250");
			multiCastSocket.joinGroup(groupAddress);
			RoutingTableAlgo routingTable = new RoutingTableAlgo();
			routingTable.initialRoutingTable(args[1]);
		
			new RouterSender(multiCastSocket, routingTable, Integer.parseInt(args[0])).start();

			while(true) {
				multiCastSocket.receive(datagramPacket);
				byte[] receivedData = datagramPacket.getData();
				byteArrayInputStream = new ByteArrayInputStream(receivedData);
				objectInputStream = new ObjectInputStream(byteArrayInputStream);
				RoutingTableAlgo readObject = (RoutingTableAlgo) objectInputStream.readObject();
				ArrayList<String> neighbors = new ArrayList<String>(routingTable.getNeighbours());
				neighbors.remove(routingTable.getRouterName());
				if(neighbors.contains(readObject.getRouterName())) {
					routingTable.updateRoutingTable(routingTable, readObject);
				}
			}
		}
		catch(IOException ioException) {
			System.out.println(ioException.getStackTrace());
		}
	}
}
