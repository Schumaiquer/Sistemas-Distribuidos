package org.sd.server;

import org.sd.common.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerLogHandler extends Thread {
    private LinkedBlockingQueue<Command> log;

    public ServerLogHandler(LinkedBlockingQueue<Command> log) {
        this.log = log;
    }

    public void run() {
        Command c = null;
        while (true) {
            try {
                c = this.log.take();
                PrintWriter writer = null;
                writer = new PrintWriter(new FileOutputStream(new File("command.log"), true));

                writer.println(c.toString());
                writer.close();
            } catch (InterruptedException e) {
            } catch (FileNotFoundException e) {
            };

            System.out.println("Logged: " + c.toString());
        }
    }
}