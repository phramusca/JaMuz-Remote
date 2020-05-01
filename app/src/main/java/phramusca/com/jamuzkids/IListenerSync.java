/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzkids;

/**
 *
 * @author phramusca
 */
public interface IListenerSync {
	void onReceivedJson(String json);
	void onReceivingFile(Track fileInfoReception);
	void onReceivedFile(Track fileInfoReception);
	void onConnected();
	void onDisconnected(boolean reconnect, String msg, long millisInFuture, boolean enable);
}
