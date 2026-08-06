package websocket.messages;

import java.util.Locale;

public class ErrorMessage extends ServerMessage {

    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage.toLowerCase(Locale.ROOT).contains("error")
                ? errorMessage : "Error: " + errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
