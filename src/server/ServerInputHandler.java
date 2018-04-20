package server;

import common.Command;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class ServerInputHandler extends Thread  {
    private List<Command> input;
    private List<Command> process;
    private List<Command> log;

    public ServerInputHandler(List<Command> input, List<Command> process, List<Command> log) {
        this.input = input;
        this.process = process;
        this.log = log;
    }


    public void run() {
        while(true){
            if(this.input.isEmpty()) continue;

            Command command = this.input.get(0);

            System.out.println("Redirect: " + command.toString());

            this.process.add(command);
            this.log.add(command);

            this.input.remove(0);
        }
    }
}
