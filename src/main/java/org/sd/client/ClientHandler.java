package org.sd.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.sd.common.Command;
import org.sd.common.UDPHandler;
import org.sd.gRPC.CommandRequest;
import org.sd.gRPC.CommandResponse;
import org.sd.gRPC.ExecuteCommandServiceGrpc;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.Scanner;

public class ClientHandler {
    private void Send(Command command, UDPHandler udp) throws IOException {
        String msg = command.toString();
        udp.Send(msg);
    }

    private String Read(String label, Scanner scanner) {
        System.out.println(label + ": ");
        String value = scanner.nextLine();

        return  value;
    }

    private BigInteger ReadKey(Scanner scanner) {
        String key = this.Read("Key", scanner);
        BigInteger readKey = new BigInteger(key);

        //2 ^ 20 = 1048576
        BigInteger limit = new BigInteger("1048576");

        while (limit.compareTo(readKey) > 1) {
            System.out.println("Tamanho maximo da chave é 1048576");
            key = this.Read("Key", scanner);
            readKey = new BigInteger(key);
        }

        return readKey;
    }

    private String ReadValue(Scanner scanner) {
        String value = this.Read("Value", scanner);
        while(value.length() > 1400) {
            System.out.println("Tamanho maximo do valor é 1400 bytes");
        }
        return value;
    }

    private Command CreateCommand(Scanner scanner, String source) {
        Command result = null;
        while(result == null) {
            System.out.println("Novo comando a ser enviado: ");
            System.out.println("C - Create");
            System.out.println("R - Read ");
            System.out.println("U - Update");
            System.out.println("D - Delete");
            System.out.println("O - Observer");
            System.out.println("S - Stop Observer");
            System.out.println("Q - QUIT");

            char op = scanner.nextLine().charAt(0);
            if (op == 'C') {
                BigInteger key = this.ReadKey(scanner);
                String value = this.ReadValue(scanner);

                result = Command.Create(key, value, source);

            } else if (op == 'R') {
                BigInteger key = this.ReadKey(scanner);
                result = Command.Read(key, source);

            } else if (op == 'U') {
                BigInteger key = this.ReadKey(scanner);
                String value = this.ReadValue(scanner);
                result = Command.Update(key, value, source);

            } else if (op == 'D') {
                BigInteger key = this.ReadKey(scanner);
                result = Command.Delete(key, source);
            } else if (op == 'O') {
                BigInteger key = this.ReadKey(scanner);
                result = Command.Observer(key, source);
            } else if (op == 'S') {
                BigInteger key = this.ReadKey(scanner);
                result = Command.StopObserver(key, source);
            } else if (op == 'Q') {
                return null;
            }
        }

        return result;
    }

    public void StartUDPClient(String serverName, int serverPort, int myPort) throws IOException {
        UDPHandler udp = new UDPHandler(serverName, serverPort, myPort);

        Scanner scanner = new Scanner(System.in);

        ClientReader reader = new ClientReader(udp);
        reader.start();

        for (Command command = this.CreateCommand(scanner, udp.getMyPort());
            command != null;
            command =  this.CreateCommand(scanner, udp.getMyPort())) {
            this.Send(command, udp);
        }

        udp.Close();
        reader.interrupt();
    }

    public void StartGrpcClient(String serverName, int serverPort, int myPort) throws IOException {
        //NESSE METODO O UDP É USADO APENAS PARA RECEBER AS NOTIFICAÇÕES ASINCRONAS DAS CHAVES
        UDPHandler udp = new UDPHandler("localhost", 0, myPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverName, serverPort)
                                .usePlaintext(true)
                                .build();
        ExecuteCommandServiceGrpc.ExecuteCommandServiceBlockingStub stub = ExecuteCommandServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        ClientReader reader = new ClientReader(udp);
        reader.start();

        for (Command command =  this.CreateCommand(scanner, udp.getMyPort());
            command != null;
            command = this.CreateCommand(scanner, udp.getMyPort())) {

            CommandResponse response = stub.executeCommand(CommandRequest.newBuilder()
                    .setOperation(command.getOperation())
                    .setKey(String.valueOf(command.getKey()))
                    .setValue(command.getValue())
                    .setSource(String.valueOf(myPort))
                    .build());

            System.out.println("Result: " +  response.getBuffer());
        }

        udp.Close();
        channel.shutdown();
        reader.interrupt();
    }
}
