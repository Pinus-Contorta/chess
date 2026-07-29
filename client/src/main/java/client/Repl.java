package client;

import java.util.Scanner;
public class Repl {

    private final ChessClient client;


    public Repl(String serverURL) {
        this.client = new ChessClient(serverURL);
    }

    public void run() {
        System.out.println("MCP awake. Would you like to play a game?");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result =  client.eval(line);
            System.out.println(result);
        }
    }

    private void printPrompt() {
        System.out.print("\n[" + client.getPromptLabel() + "] >>>");
    }
}
