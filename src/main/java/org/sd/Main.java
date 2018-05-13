package org.sd;

import org.sd.client.ClientHandler;
import org.sd.server.ServerHandler;

public class Main {
    public static void main(String[] args) {
        String[] input = args;
        if (input == null || input.length == 0) {
            System.out.println("NUMERO DE PARAMETROS INVALIDO");
            args = new String[] {"-SERVER", "-UDP_PORT:8080", "-GRPC:8081"};
            //args = new String[] {"-CLIENT", "-grpc", "-RECEIVER:9091", "-SERVER:localhost:8081"};
        }

        if (args[0].toUpperCase().equals("-SERVER")) {
            ServerHandler server;
            try {
                if (args.length != 3) throw new Exception();

                String UDPPort = args[1].split(":")[1];
                String GRPCPort = args[2].split(":")[1];

                server = new ServerHandler(Integer.valueOf(UDPPort), Integer.valueOf(GRPCPort));
                server.Start();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("O MODO SERVER DEVE SER  SEGUIDO POR -UDP_PORT:PORTNUMBER -gRPC_PORT:PORTNUMBER");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else if (args[0].toUpperCase().equals("-CLIENT")) {
            try {
                 ClientHandler client = new ClientHandler();

                String ReceiverPort = args[2].split(":")[1];

                String ServerName = args[3].split(":")[1];
                String ServerPort = args[3].split(":")[2];

                if (args[1].toUpperCase().equals("-UDP")) {
                    client.StartUDPClient(ServerName, Integer.valueOf(ServerPort), Integer.valueOf(ReceiverPort));
                } else if (args[1].toUpperCase().equals("-GRPC")) {
                    client.StartGrpcClient(ServerName, Integer.valueOf(ServerPort), Integer.valueOf(ReceiverPort));
                } else {
                    throw new Exception();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("O MODO CLIENT DEVE SER  SEGUIDO POR (-UDP || -GRPC) -RECEIVER_PORT:PORTNUMBER -SERVER:SERVERNAME:SERVERPORT");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("O PRIMEIRO PARAMETRO DEVE SER -SERVER || -CLIENT");
        }
    }
}
