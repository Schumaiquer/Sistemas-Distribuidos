package org.sd.gRPC;

import io.grpc.*;
import io.grpc.stub.StreamObserver;

import org.sd.common.Command;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecuteCommandServiceImpl extends ExecuteCommandServiceGrpc.ExecuteCommandServiceImplBase {
    private LinkedBlockingQueue<Command> input;

    public ExecuteCommandServiceImpl(LinkedBlockingQueue<Command> input) {
        this.input = input;
    }

    @Override
     public void executeCommand(CommandRequest request, StreamObserver<CommandResponse> responseStreamObserver) {
        try {
            SocketAddress clientSocketAddress = CallIntercepter.CLIENT_ADDRESS.get();
            String clientIp = ((InetSocketAddress)clientSocketAddress)
                            .getAddress()
                            .toString()
                            .split("/")[1];

            Command command = new Command(request);
            System.out.println("Receveived by gRPC protocol: " + clientSocketAddress.toString());


            command.setSource(clientIp + ":" + command.getSource().trim());

            //O TRATAMENTO DE UMA REQUISIÇÃO gRPC É O MESMO QUE UMA REQUISIÇÃO UDP
            //APENAS ADICIONAR A LISTA DE INPUTS
            this.input.put(command);

            CommandResponse commandResponse = CommandResponse.newBuilder()
                                              .setBuffer("Command" + command + "added to queue of processing")
                                              .build();
            responseStreamObserver.onNext(commandResponse);
            responseStreamObserver.onCompleted();

        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
