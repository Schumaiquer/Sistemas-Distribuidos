package org.sd.server;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.sd.common.Command;
import org.sd.common.UDPHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerExecuteHandler extends Thread {
    private UDPHandler udp;

    private LinkedBlockingQueue<Command> process;
    private HashMap<BigInteger, String> database;
    private HashMap<BigInteger, List<String>> observers;

    public ServerExecuteHandler(UDPHandler udp, LinkedBlockingQueue<Command> process) {
        this.udp = udp;
        this.process = process;

        this.database = new HashMap<BigInteger, String>();
        this.observers = new HashMap<BigInteger, List<String>>();
    }

    private void LoadFromLog() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream("command.log"));
            while (scanner.hasNextLine()) {
                String buffer = scanner.nextLine();
                System.out.println("Loading from log: " + buffer);

                Command command = Command.Deserialize(buffer);
                this.RunCommand(command);
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (scanner != null) scanner.close();
        }
    }

    private String RunCommand(Command command) {
        String message = "";
        if (command.getOperation().equals("C")) {
            if (!this.database.containsKey(command.getKey())) {
                this.database.put(command.getKey(), command.getValue());
                message = "<Ok>";
            } else {
                message = "<Err, key already exists>";

            }
        } else if (command.getOperation().equals("U")) {
            if (!this.database.containsKey(command.getKey())) {
                message = "<Err, key not exists>";
            } else {
                this.database.put(command.getKey(), command.getValue());
                message = "<Ok>";
            }
        } else if (command.getOperation().equals("R")) {
            String value = this.database.get(command.getKey());
            message = "<Ok, R = " + value + ">";

        } else if (command.getOperation().equals("D")) {
            if (!this.database.containsKey(command.getKey())) {
                message = "<Err, key not exists>";
            } else {
                this.database.remove(command.getKey());
                message = "<Ok>";
            }
        } else if (command.getOperation().equals("O")) {
            List l = this.observers.get(command.getKey());
            if(l == null) {
                l = new ArrayList<String>();
                this.observers.put(command.getKey(), l);
            }
            l.add(command.getSource());

            String value = "BEGINNING TO  OBSERVING THE KEY | " + command.getKey();
            message = "<Ok, R = " + value + ">";
        } else if (command.getOperation().equals("S")) {
            List l = this.observers.get(command.getKey());
            if(l == null) {
                l = new ArrayList<String>();
                this.observers.put(command.getKey(), l);
            }
            l.remove(command.getSource());

            String value = "STOPING TO OBSERVE THE KEY | " + command.getKey();
            message = "<Ok, R = " + value + ">";
        }

        return message;
    }

    public void NotifyClients(Command c, String message, String source) throws IOException {
        List<String> clients = this.observers.get(c.getKey());
        if(clients == null || clients.isEmpty()) return;

        for(String client : clients) {
            //O CLIENTE QUE ENVIVOU O COMMANDO NAO DEVE SER NOTIFICADO SOBRE O MESMO.
            if(client.equals(source)) continue;

            String Ip = client.split(":")[0];
            String Port = client.split(":")[1];

            message = "Notification from command: " + message;
            this.udp.Send(message, Ip, Integer.valueOf(Port));
        }
    }

    public void run() {
        this.LoadFromLog();
        Command command;
        while (true) {
            try {
                command = this.process.take();
                System.out.println("Processing:" + command.toString());

                String message = this.RunCommand(command);

                String Ip = command.getSource().split(":")[0];
                String Port = command.getSource().split(":")[1];

                message = command.toString() + message;

                this.udp.Send(message, Ip, Integer.valueOf(Port));
                if(!command.getOperation().equals("O") && !command.getOperation().equals("S")) {
                    //APENAS GERA NOTIFICAÇÕES PARA AS OPERACOES CRUD.
                    this.NotifyClients(command, message, command.getSource());
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
