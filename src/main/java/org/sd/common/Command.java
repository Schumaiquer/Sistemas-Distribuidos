package org.sd.common;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.sd.gRPC.CommandRequest;

import java.math.BigInteger;
import java.nio.charset.CoderMalfunctionError;

public class Command {
    public BigInteger serial;

    private String operation;
    private BigInteger key;
    private String value;
    private String source;

    public String getOperation() {
        return this.operation;
    }

    public BigInteger getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getSource() {
        return  this.source;
    }

    public void setSource(String source){
        this.source = source.trim();
    }

    public static Command Create(BigInteger key, String value, String port) {
        return new Command("C", key ,value, port);
    }

    public static Command Read(BigInteger key, String port) {
        return new Command("R", key,"", port);
    }

    public static Command Update(BigInteger key, String value, String port) {
        return new Command("U", key, value, port);
    }

    public static Command Delete(BigInteger key, String port) {
        return new Command("D", key, "", port);
    }

    public static Command StopObserver(BigInteger key, String port) {
        return new Command("S", key, "", port);
    }

    public static Command Observer(BigInteger key, String port) {
        return new Command("O", key, "", port);
    }

    public static Command Deserialize(String str) {
        str = str.replace('<', ' ').replace('>', ' ').trim();

        String command = str.substring(0, str.indexOf(','));
        str = str.substring(str.indexOf(',') + 1);

        String buffer = str.substring(0, str.indexOf(','));
        BigInteger key = new BigInteger(buffer.trim());
        str = str.substring(str.indexOf(',') + 1);

        String value = "";
        String port = "";


        if(str.indexOf(',') == -1) {
            value = str;
        } else {
            value = str.substring(0, str.indexOf(','));
            str = str.substring(str.indexOf(',') + 1);

            port = str;
        }

        return new Command(command, key, value, port);
    }

    public Command(CommandRequest request) {
        this.operation = request.getOperation();
        this.key = new BigInteger(request.getKey());
        this.value = request.getValue();
        this.source = request.getSource();
    }

    public Command(String operation, BigInteger key, String value, String port) {
        this.operation = operation;
        this.key = key;
        this.value = value;
        this.source = port;
    }

    @Override
    public String toString() {
        if(this.source.equals("")) return String.format("<%s,%s,%s>", this.operation, this.key, this.value.trim());
        return String.format("<%s,%s,%s,%s>", this.operation, this.key, this.value.trim(), this.source);
    }
}