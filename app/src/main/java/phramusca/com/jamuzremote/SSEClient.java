package phramusca.com.jamuzremote;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import java.net.URI;

import okhttp3.Headers;

public class SSEClient {

    private final EventSource eventSourceSse;
    private boolean isConnected = false; // Flag to track connection status

    public SSEClient(SSEHandler sseHandler, URI uri, Headers headers) {
        EventHandler eventHandler = new DefaultEventHandler(sseHandler);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        eventSourceSse = new EventSource.Builder(eventHandler, uri)
//                    .connectTimeout()
//                    .backoffResetThreshold(Duration.ofSeconds(3))
                .headers(headers)
                .build();
    }

    public void start() {
        eventSourceSse.start();
        isConnected = true; // Update connection status when starting
    }

    public boolean isConnected() {
        return isConnected;
    }

    private static class DefaultEventHandler implements EventHandler {
        private final SSEHandler sseHandler;

        public DefaultEventHandler(SSEHandler sseHandler) {
            this.sseHandler = sseHandler;
        }

        @Override
        public void onOpen() {
            sseHandler.onSSEConnectionOpened();
        }

        @Override
        public void onMessage(String event, MessageEvent messageEvent) {
            sseHandler.onSSEEventReceived(event, messageEvent);
        }

        @Override
        public void onError(Throwable t) {
            sseHandler.onSSEError(t);
        }

        @Override
        public void onComment(String comment) {
            System.out.println("SSE_CONNECTION: " + comment);
        }
    }

    public void disconnect() {
        try {
            if (eventSourceSse != null) {
                eventSourceSse.close();
                isConnected = false; // Update connection status when disconnecting
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
