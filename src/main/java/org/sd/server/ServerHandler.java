package org.sd.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import org.sd.common.Command;
import org.sd.common.UDPHandler;
import org.sd.gRPC.CallIntercepter;
import org.sd.gRPC.ExecuteCommandServiceImpl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerHandler {
    private UDPHandler udp;

    private LinkedBlockingQueue<Command> input;
    private LinkedBlockingQueue<Command> process;
    private LinkedBlockingQueue<Command> log;

    private int grpcServerPort;
    public ServerHandler(int udpServerPort, int grpcServerPort) throws UnknownHostException, SocketException {
        this.grpcServerPort = grpcServerPort;
        this.udp = new UDPHandler("localhost", 0, udpServerPort);

        // O tipo LinkedBlockingQueue é por natureza thread safe,
        // assim não é necessario utilizar o construtor syncronized
        this.input = new LinkedBlockingQueue<Command>();
        this.process = new LinkedBlockingQueue<Command>();
        this.log = new LinkedBlockingQueue<Command>();
    }

    public void Start() throws IOException {
        String buffer;

        ServerInputHandler inputHandler = new ServerInputHandler(this.input, this.process, this.log);
        ServerLogHandler logHandler = new ServerLogHandler(this.log);
        ServerExecuteHandler executeHandler = new ServerExecuteHandler(this.udp, this.process);

        Server grpcServer =
        ServerBuilder
            .forPort(this.grpcServerPort)
            .addService(ServerInterceptors.intercept(new ExecuteCommandServiceImpl(this.input), new CallIntercepter()))
            .build();

        grpcServer.start();

        inputHandler.start();
        logHandler.start();
        executeHandler.start();

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
