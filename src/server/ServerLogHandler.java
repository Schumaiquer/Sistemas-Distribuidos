package server;

import common.Command;

import java.io.*;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class ServerLogHandler extends Thread  {
    private List<Command> log;

    public ServerLogHandler(List<Command> log) {
        this.log = log;
    }

    public void run() {
        while(true){
            if(this.log.isEmpty()) continue;

            Command c = this.log.get(0);

            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileOutputStream(new File("command.log"), true ));
            } catch (FileNotFoundException e) {

            }
            writer.println(c.toString());
            writer.close();

            System.out.println("Logged: " + c.toString());

            this.log.remove(0);
        }
    }
}
