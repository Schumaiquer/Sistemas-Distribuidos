import client.ClientHandler;
import com.sun.org.apache.bcel.internal.generic.RET;
import server.ServerHandler;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void StartServer() throws IOException {
        FileReader fileReader = new FileReader("server.conf");
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String myPort = bufferedReader.readLine();
        bufferedReader.close();

        ServerHandler server = new ServerHandler(Integer.valueOf(myPort));
        server.Start();
    }

    public static void StartClient(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String serverName = bufferedReader.readLine();
        String serverPort = bufferedReader.readLine();
        String myPort = bufferedReader.readLine();

        bufferedReader.close();

        ClientHandler client = new ClientHandler(serverName, Integer.valueOf(serverPort), Integer.valueOf(myPort));
        client.Start();
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[]{"-server"};

            //System.out.println("opções invalidas (-server ou -client)");
            //return;
        }

        if(args[0].equals("-server")) {
            StartServer();
        } else if(args[0].equals("-client")) {
            String fileName = "";

            if(args.length == 1) fileName =  "client.conf";
            else fileName = args[1];

            StartClient(fileName);
        } else {
            System.out.println("opções invalidas (-server ou -client)");
        }
    }
}
