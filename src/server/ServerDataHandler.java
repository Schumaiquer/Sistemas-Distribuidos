package server;

import common.Command;
import common.UDPHandler;
import org.omg.CORBA.COMM_FAILURE;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ServerDataHandler extends Thread  {
    private UDPHandler udp;

    private List<Command> process;
    private HashMap<BigInteger, String> database;

    public ServerDataHandler(UDPHandler udp, List<Command> process) {
        this.udp = udp;
        this.process = process;

        this.database = new HashMap<BigInteger, String>();
    }

    private void LoadFromLog() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream("command.log"));
            while (scanner.hasNextLine()){
                String buffer = scanner.nextLine();
                System.out.println("Loading from log: " + buffer);

                Command command = Command.Deserialize(buffer);
                this.RunCommand(command);
            }
        } catch (FileNotFoundException e) {

        } finally{
            if(scanner != null) scanner.close();
        }
    }

    private String RunCommand(Command command) {
        String message = "";
        if(command.getOperation().equals("C")){
            if(!this.database.containsKey(command.getKey())) {
                this.database.put(command.getKey(), command.getValue());
                message = "<Ok>";
            } else {
                message = "<Err, key already exists>";

            }
        } else if(command.getOperation().equals("U")) {
            if(!this.database.containsKey(command.getKey())){
                message = "<Err, key not exists>";
            } else {
                this.database.put(command.getKey(), command.getValue());
                message = "<Ok>";
            }
        } else if(command.getOperation().equals("R")) {
            String value = this.database.get(command.getKey());
            message = "<Ok, R = " + value + ">";

        } else if(command.getOperation().equals("D")) {
            if(!this.database.containsKey(command.getKey())){
                message = "<Err, key not exists>";
            } else {
                this.database.remove(command.getKey());
                message = "<Ok>";
            }
        }

        return  message;
    }

    public void run() {
        this.LoadFromLog();

        while(true){
            if(this.process.isEmpty()) continue;

            Command command = this.process.get(0);

            System.out.println("Processing:" + command.toString());

            String message = this.RunCommand(command);

            String Ip = command.getSource().split(":")[0];
            String Port = command.getSource().split(":")[1];

            try {
                //command.setSource("");
                message = command.toString() + message;

                this.udp.Send(message, Ip, Integer.valueOf(Port));
            } catch (IOException e) {

            }

            this.process.remove(0);
        }
    }
}
