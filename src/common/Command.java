package common;

import java.math.BigInteger;

public class Command {
    private  String operation;
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

    public static Command Create(String key, String value, String port) {
        BigInteger k = new BigInteger(key);
        return new Command("C", k,value, port);
    }

    public static Command Read(String key, String port) {
        BigInteger k = new BigInteger(key);
        return new Command("R", k,"", port);
    }

    public static Command Update(String key, String value, String port) {
        BigInteger k = new BigInteger(key);
        return new Command("U", k,value, port);
    }

    public static Command Delete(String key, String port) {
        BigInteger k = new BigInteger(key);
        return new Command("D", k, "", port);
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

    public Command(String operation, BigInteger key, String value, String port) {
        this.operation = operation;
        this.key = key;
        this.value = value;
        this.source = port;
    }

    @Override
    public String toString() {
        if(this.source.equals("")) return String.format("<%s, %s, %s>", this.operation, this.key, this.value.trim());
        return String.format("<%s, %s, %s, %s>", this.operation, this.key, this.value.trim(), this.source);
    }
}
