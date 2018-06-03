package org.sd.server;

import org.sd.common.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerInputHandler extends Thread {
    private LinkedBlockingQueue<Command> input;
    private LinkedBlockingQueue<Command> process;
    private LinkedBlockingQueue<Command> log;

    private BigInteger serial;

    public ServerInputHandler(LinkedBlockingQueue<Command> input,
                              LinkedBlockingQueue<Command> process,
                              LinkedBlockingQueue<Command> log) {
        this.input = input;
        this.process = process;
        this.log = log;

        this.serial = new BigInteger("0");
    }

    public void run() {
        Command c = null;

        while (true) {
            try {
                c = this.input.take();
                System.out.println("Redirect: " + c.toString());

                this.serial = this.serial.add(new BigInteger("1"));
                c.serial = this.serial;

                this.log.put(c);
                this.process.put(c);
            } catch (InterruptedException e) {
            }

            System.out.println("Logged: " + c.toString());
        }
    }
}