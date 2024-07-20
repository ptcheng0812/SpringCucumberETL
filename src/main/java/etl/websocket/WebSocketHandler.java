package etl.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WebSocketHandler {

    private String endpoint;
    private WebSocketClient webSocketClient;

    private CompletableFuture<String> messageFuture = new CompletableFuture<>();
    private List<String> messages = new ArrayList<>();

    public WebSocketHandler(String endpoint) {
        this.endpoint = endpoint;
    }

    public void connectedToWebSocketServer() throws URISyntaxException, InterruptedException {
        URI uri = new URI(endpoint);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {
//                messageFuture.complete(s);
                messages.add(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }
        };
        webSocketClient.connectBlocking(20, TimeUnit.SECONDS);
    }

    public void sendMessageToServer(String message) throws InterruptedException {
        webSocketClient.send(message);
        Thread.sleep(10000);
    }

    public List<String> receieveFromService() throws ExecutionException, InterruptedException {
        return messages;
    }
}
