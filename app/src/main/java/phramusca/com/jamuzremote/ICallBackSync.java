/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

/**
 *
 * @author phramusca
 */
public interface ICallBackSync {
	void receivedJson(String json);
	void receivingFile(FileInfoReception fileInfoReception);
	void receivedFile(FileInfoReception fileInfoReception);
	void connected();
	void disconnected(boolean reconnect, String msg, long millisInFuture);
}
