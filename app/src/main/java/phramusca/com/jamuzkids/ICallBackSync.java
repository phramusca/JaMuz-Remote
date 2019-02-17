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
public interface ICallBackSync {
	void receivedJson(String json);
	void receivingFile(Track fileInfoReception);
	void receivedFile(Track fileInfoReception);
	void connected();
	void disconnected(boolean reconnect, String msg, long millisInFuture);
}
