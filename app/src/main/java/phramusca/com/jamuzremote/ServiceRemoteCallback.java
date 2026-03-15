package phramusca.com.jamuzremote;

import com.launchdarkly.eventsource.MessageEvent;

public interface ServiceRemoteCallback {
    void onServiceDataReceived(String event, MessageEvent messageEvent);
}
