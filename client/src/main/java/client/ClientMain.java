package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 3306;
        new Repl(port).run();
    }
}
