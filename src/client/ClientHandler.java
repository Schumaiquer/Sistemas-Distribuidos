package client;

import common.Command;
import common.UDPHandler;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientHandler {
    private UDPHandler udp;

    public ClientHandler(String serverName, int serverPort, int myPort) throws UnknownHostException, SocketException {
        this.udp = new UDPHandler(serverName, serverPort, myPort);
    }

    private void Send(Command command) throws IOException {
        String msg = command.toString();
        this.udp.Send(msg);
    }

    private String Read(String label, Scanner scanner) {
        System.out.println(label + ": ");
        String value = scanner.nextLine();

        return  value;
    }

    private Command CreateCommand(Scanner scanner) {
        String buffer = null;
        Command result = null;
        while(result == null) {
            System.out.println("Novo comando a ser enviado: ");
            System.out.println("C - Create");
            System.out.println("R - Read ");
            System.out.println("U - Update");
            System.out.println("D - Delete");
            System.out.println("Q - QUIT");

            char op = scanner.nextLine().charAt(0);
            if (op == 'C') {
                buffer = this.Read("Key", scanner);
                String value = this.Read("Value", scanner);

                result = Command.Create(buffer, value, this.udp.getMyPort());

            } else if (op == 'R') {
                buffer = this.Read("Key", scanner);
                result = Command.Read(buffer, this.udp.getMyPort());

            } else if (op == 'U') {
                buffer = this.Read("Key", scanner);
                String value = this.Read("Value", scanner);
                result = Command.Update(buffer, value, this.udp.getMyPort());

            } else if (op == 'D') {
                buffer = this.Read("Key", scanner);
                result = Command.Delete(buffer, this.udp.getMyPort());
            } else if (op == 'Q') {
                return null;
            }
        }

        return result;
    }

    public void Start() throws IOException {
        Scanner scanner = new Scanner(System.in);

        ClientReader reader = new ClientReader(this.udp);
        reader.start();

        for (Command command = this.CreateCommand(scanner); command != null; command = this.CreateCommand(scanner)) {
            this.Send(command);
        }

        this.udp.Close();
        reader.interrupt();
    }
}
