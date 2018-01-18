/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import android.graphics.Bitmap;

/**
 *
 * @author phramusca
 */
public class ClientRemote extends Client {
	private static final String TAG = ClientRemote.class.getSimpleName();

	private final ICallBackRemote callback;
	private boolean userStop=false;

	public ClientRemote(ClientInfo clientInfo, ICallBackRemote callback){
		super(clientInfo);
		this.callback = callback;
        super.setCallback(new CallBackReception());
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

    class CallBackReception implements ICallBackReception {

		@Override
		public void receivedJson(String msg) {
			callback.receivedJson(msg);
		}

        @Override
        public void receivedBitmap(Bitmap bitmap) {
            callback.receivedBitmap(bitmap);
        }

        @Override
        public void receivingFile(FileInfoReception fileInfoReception) {        }

        @Override
        public void receivedFile(FileInfoReception fileInfoReception) {        }

        @Override
        public void receivedDatabase() {        }

        @Override
		public void disconnected(String msg) {
            if(!userStop) {
                callback.disconnected(msg);
            }
		}
	}
}