package phramusca.com.jamuzremote;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import java.net.URI;

public class SSEClient {

    private SSEHandler sseHandlers;
    private EventSource eventSourceSse;

    public void initSse(SSEHandler sseHandler, URI uri) {
        this.sseHandlers = sseHandler;
        EventHandler eventHandler = new DefaultEventHandler(sseHandler);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            eventSourceSse = new EventSource.Builder(eventHandler, uri)

//                    .connectTimeout()
//                    .backoffResetThreshold(Duration.ofSeconds(3))
                    .build();


            eventSourceSse.start();
//        }

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

//        @Override
//        public void onClosed() {
//            sseHandler.onSSEConnectionClosed();
//        }

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
