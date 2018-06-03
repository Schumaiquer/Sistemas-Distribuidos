package org.sd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sd.common.Command;
import org.sd.common.UDPHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerExecuteHandler extends AbstractServerThread {
    private UDPHandler udp;

    private LinkedBlockingQueue<Command> process;
    private ConcurrentHashMap<BigInteger, String> database;
    private ConcurrentHashMap<BigInteger, List<String>> observers;

    private BigInteger last = new BigInteger("-1");

    public ServerExecuteHandler(UDPHandler udp, LinkedBlockingQueue<Command> process) {
        this.udp = udp;

        this.process = process;
        this.database = new ConcurrentHashMap<BigInteger, String>();
        this.observers = new ConcurrentHashMap<BigInteger, List<String>>();
    }

    private void LoadFromFile() {
        Scanner scanner = null;

        String buffer = "";
        ObjectMapper mapper = new ObjectMapper();

        //READ DATABASE
        try {
            scanner = new Scanner(new FileInputStream("database.dat"));
            while (scanner.hasNextLine()) {
                buffer += scanner.nextLine();
            }
            this.database = mapper.readValue(buffer, new TypeReference<ConcurrentHashMap<BigInteger, String>>() {
            });
        } catch (Exception e) {}
        finally { if (scanner != null) scanner.close(); }

        try {
            buffer = "";
            //READ OBSERVERS
            scanner = new Scanner(new FileInputStream("observers.dat"));
            while (scanner.hasNextLine()) {
                buffer += scanner.nextLine();
            }
            this.observers = mapper.readValue(buffer, new TypeReference<ConcurrentHashMap<BigInteger, List<String>>>() {
            });
        } catch (Exception e) {}
        finally { if (scanner != null) scanner.close(); }

        //Se a data de modificacão do arquivo de log for anterior a data de modificao
        //o servidor caiu entre o snapshot e a limpeza do log
        //entao ignore o log e só recupere o snapshot

        //READ LOG
        File logFile = new File("command.log");
        File snapshotFile = new File("database.dat");

        Date logDate = new Date(logFile.lastModified());
        Date snapshotDate = new Date(snapshotFile.lastModified());

        if(snapshotDate.compareTo(logDate) > 0) {
            System.out.println("IGNORANDO E LIMPANDO ARQUIVO DE LOG POR SER MAIS ANTIGO QUE O SNAPSHOT");
            logFile.delete();
            return;
        }

        try{
            scanner = new Scanner(new FileInputStream("command.log"));
            while (scanner.hasNextLine()) {
                buffer = scanner.nextLine();
                if(buffer == null || buffer.equals("")) continue;

                System.out.println("Loading from log: " + buffer);

                Command command = Command.Deserialize(buffer);
                this.RunCommand(command);
            }
        } catch (Exception e) { }
        finally { if (scanner != null) scanner.close(); }
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

    private void NotifyClients(Command c, String message, String source) throws IOException {
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
        this.LoadFromFile();
        Command command;

        while (true) {
            try {
                command = this.process.take();

                super.Lock();
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

                this.last = command.serial;
                super.Unlock();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String Database() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this.database);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String Observers() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this.observers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public BigInteger getLast() {
        return this.last;
    }
}
