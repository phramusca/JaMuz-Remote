/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzkids;

import android.graphics.Bitmap;

/**
 *
 * @author phramusca
 */
public class ClientRemote extends Client {

	private final IListenerRemote callback;
	private boolean userStop=false;

	public ClientRemote(ClientInfo clientInfo, IListenerRemote callback){
		super(clientInfo);
		this.callback = callback;
        super.setCallback(new ListenerReception());
	}

    @Override
    public boolean connect() {
        userStop=false;
        return super.connect();
    }

    @Override
    public void close() {
        userStop=true;
        super.close();
    }

    class ListenerReception implements IListenerReception {

		@Override
		public void onReceivedJson(String json) {
			callback.onReceivedJson(json);
		}

        @Override
        public void onReceivedBitmap(Bitmap bitmap) {
            callback.onReceivedBitmap(bitmap);
        }

        @Override
        public void onReceivingFile(Track fileInfoReception) {        }

        @Override
        public void onReceivedFile(Track fileInfoReception) {        }

        @Override
		public void onDisconnected(String msg) {
            callback.onDisconnected(userStop?"":msg);
		}
	}
}