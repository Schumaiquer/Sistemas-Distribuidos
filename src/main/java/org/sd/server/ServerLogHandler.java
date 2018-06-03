package org.sd.server;

import org.sd.common.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerLogHandler extends AbstractServerThread {
    private LinkedBlockingQueue<Command> log;
    private BigInteger last = new BigInteger("-1");

    public ServerLogHandler(LinkedBlockingQueue<Command> log) {
        this.log = log;
    }

    public void run() {
        Command c = null;
        while (true) {
            try {
                c = this.log.take();

                super.Lock();

                // SE O SERIAL ATUAL FOR MAIOR CONSIDERE QUE O COMMANDO JA FOI LOGADO.
                if(c.serial.compareTo(this.last) <= 0) continue;

                PrintWriter writer = null;
                writer = new PrintWriter(new FileOutputStream(new File("command.log"), true));

                writer.println(c.toString());
                writer.close();

                this.last = c.serial;
                super.Unlock();
            } catch (InterruptedException e) {
            } catch (FileNotFoundException e) {
            };

            System.out.println("Logged: " + c.toString());
        }
    }

    public BigInteger getLast() {
        return this.last;
    }

    public void setLast(BigInteger last) {
        this.last = last;
    }

}