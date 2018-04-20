package common;

import java.io.IOException;
import java.net.*;

public class UDPHandler {
    private String targetName;
    private int targetPort;
    private int myPort;

    public String getMyPort() {
        return String.valueOf(this.myPort);
    }

    DatagramSocket sender;
    DatagramSocket receiver;

    public UDPHandler(int myPort) throws UnknownHostException, SocketException {
        this.myPort = myPort;

        this.receiver = new DatagramSocket(this.myPort);
        this.sender = new DatagramSocket();
    }

    public UDPHandler(String targetName, int targetPort, int myPort) throws SocketException {
        this.targetName = targetName;
        this.targetPort = targetPort;
        this.myPort = myPort;

        this.receiver = new DatagramSocket(this.myPort);
        this.sender = new DatagramSocket();
    }

    public void Close(){
        this.receiver.close();
        this.sender.close();
    }

    public void Send(String word) throws IOException {
        this.Send(word, this.targetName, this.targetPort);
    }

    public void Send(String word, String targetName, int targetPort) throws IOException {
        InetAddress targetIP = InetAddress.getByName(targetName);

        byte[] buffer = word.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, targetIP, targetPort);
        this.sender.send(packet);

        System.out.println("Sending: " + word + "//" + targetIP.toString() + ":" + targetPort);
    }

    public String Receive()  {
        byte[] buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            System.out.println("Waiting in : " +  String.valueOf(this.myPort));
            this.receiver.receive(packet);
        } catch (Exception e) {
            return "CLOSE";
        }

        byte[] result = packet.getData();
        int nullIndex = 0;
        for(nullIndex = 0; nullIndex < result.length; nullIndex ++) {
            if(result[nullIndex] == 0){
                break;
            }
        }

        String str = new String(result, 0, nullIndex );

        str += ":<" + packet.getAddress().getHostName() + ">";

        System.out.println("Received: " +  str);
        return  str;
    }
}
