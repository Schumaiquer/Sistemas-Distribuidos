package org.sd.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.server.ExportException;
import java.util.Date;
import java.util.Scanner;

public class ServerSnapshotHandler extends Thread {
    private ServerExecuteHandler executeHandler;
    private ServerLogHandler logHandler;

    private int minutes;

    public ServerSnapshotHandler(ServerLogHandler logHandler, ServerExecuteHandler executeHandler, int minutes) {
        this.logHandler = logHandler;
        this.executeHandler = executeHandler;

        this.minutes = minutes;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(this.minutes * 60 * 1000);

                // Garante que nada vai ser logado ou processado durante o SNAPSHOT
                this.logHandler.Lock();
                this.executeHandler.Lock();

                // LOG PROCESSOU MAIS QUE O EXECUTE
                if(this.logHandler.getLast().compareTo(this.executeHandler.getLast()) > 0) {
                    // LIBERAR O EXECUTE ATE QUE ELE ALCANSE OU PASSE O LOG
                    this.executeHandler.Unlock();
                    while(this.logHandler.getLast().compareTo(this.executeHandler.getLast()) > 0);
                    this.executeHandler.Lock();

                    //INFORMA O LOG PARA IGNORAR AS ENTRADAS ATÉ ESSE SERIAL
                    this.logHandler.setLast(this.executeHandler.getLast());
                };

                //Inicia a serializacao do dicionario
                String database = this.executeHandler.Database();
                String observers = this.executeHandler.Observers();

                PrintWriter writer  = new PrintWriter(new FileOutputStream(new File("database.dat"), false));
                writer.println(database);
                writer.close();

                writer  = new PrintWriter(new FileOutputStream(new File("observers.dat"), false));
                writer.println(observers);
                writer.close();

                System.out.println("ENTRE SNAPSHOT E LOG :: Aperte qualquer coisa para continuar ou mate o processo para simulacao. ");
                Scanner scanner = new Scanner(System.in);
                String a = scanner.next();

                System.out.println("SNAPSHOT ::: LIMPANDO O LOG");

                writer  = new PrintWriter(new FileOutputStream(new File("command.log"), false));
                writer.println("");
                writer.close();

            } catch (Exception e) {
            } finally {
                this.logHandler.Unlock();
                this.executeHandler.Unlock();
            };

            System.out.println(new Date() + ": SNAPSHOT FEITO");
        }
    }
}
