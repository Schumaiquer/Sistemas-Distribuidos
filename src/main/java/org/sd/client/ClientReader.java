package org.sd.client;

import org.sd.common.UDPHandler;

public class ClientReader extends Thread  {
    private UDPHandler udp;
    public ClientReader(UDPHandler udpHandler){
        this.udp = udpHandler;
    }

    public void run() {
        String s;
        for(s = this.udp.Receive(); true; s = this.udp.Receive()) {
            if (s.equals("CLOSE")) return;
            //System.out.println(s);
        }

    }
}
