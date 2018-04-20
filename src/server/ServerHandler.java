package server;

import common.Command;
import common.UDPHandler;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;

import java.net.UnknownHostException;
import java.util.*;

public class ServerHandler {
    private UDPHandler udp;

    private List<Command> input;
    private List<Command> process;
    private List<Command> log;

    private Dictionary<BigInteger, String> database;

    public ServerHandler(int serverPort) throws SocketException, UnknownHostException {
        this.udp = new UDPHandler("localhost", 0, serverPort);

        this.input = Collections.synchronizedList(new ArrayList<Command>());
        this.process = Collections.synchronizedList(new ArrayList<Command>());
        this.log = Collections.synchronizedList(new ArrayList<Command>());
    }

    public void Start() throws IOException {
        String buffer;

        ServerInputHandler inputHandler = new ServerInputHandler(this.input, this.process, this.log);
        ServerLogHandler logHandler = new ServerLogHandler(this.log);
        ServerDataHandler dataHandler = new ServerDataHandler(this.udp, this.process);

        inputHandler.start();
        logHandler.start();
        dataHandler.start();

        for(buffer = this.udp.Receive(); true; buffer = this.udp.Receive()){
            try {
                if (buffer.equals("")) continue;

                String[] splited = buffer.split(":<");

                Command c = Command.Deserialize(splited[0]);
                c.setSource(splited[1].replace('>', ' ').trim() + ":" + c.getSource().trim());

                this.input.add(c);
            } catch (Exception e) {
                break;
            }
        }

        this.udp.Close();
    }
}
