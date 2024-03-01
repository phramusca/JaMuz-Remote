package phramusca.com.jamuzremote;

import com.launchdarkly.eventsource.MessageEvent;

//FIXME: Replace with SSEHandler
public interface ServiceRemoteCallback {
    void onServiceDataReceived(String event, MessageEvent messageEvent);
}
