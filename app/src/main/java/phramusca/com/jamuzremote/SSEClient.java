package phramusca.com.jamuzremote;

import android.util.Log;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;

public class SSEClient {

    private static final String TAG = SSEClient.class.getName();
    private EventSource eventSourceSse;
    private boolean isConnected = false;

    public SSEClient(SSEHandler sseHandler, URI uri, Headers headers) {
        EventHandler eventHandler = new DefaultEventHandler(sseHandler);
        // readTimeout: 30s (heartbeat every 5s keeps stream active; 30s catches real drop)
        // reconnectTime/maxReconnectTime: auto-reconnect with backoff on error (no stop on first failure)
        eventSourceSse = new EventSource.Builder(eventHandler, uri)
                .headers(headers)
                .readTimeout(30, TimeUnit.SECONDS)
                .reconnectTime(2, TimeUnit.SECONDS)
                .maxReconnectTime(30, TimeUnit.SECONDS)
                .build();
    }

    public void start() {
        eventSourceSse.start();
        isConnected = true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private class DefaultEventHandler implements EventHandler {
        private final SSEHandler sseHandler;

        DefaultEventHandler(SSEHandler sseHandler) {
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

        @Override
        public void onClosed() {
            isConnected = false;
            sseHandler.onSSEConnectionClosed();
        }
    }

    public void disconnect() {
        try {
            if (eventSourceSse != null) {
                // Closing here does not always trigger server onClose(); ServiceRemote calls GET /disconnect before this.
                eventSourceSse.close();
                isConnected = false;
                eventSourceSse = null;
            }
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }
}
