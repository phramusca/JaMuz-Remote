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
	void receivedJson(String msg);
	void receivingFile(FileInfoReception fileInfoReception);
	void receivedFile(FileInfoReception fileInfoReception);
	void receivedDatabase();
	void connected();
	void disconnected(String msg, boolean disable);
}
