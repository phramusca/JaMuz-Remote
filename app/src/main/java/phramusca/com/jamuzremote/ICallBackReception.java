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
public interface ICallBackReception {
	void received(String msg);
	void receivedBitmap(Bitmap bitmap);
	void receivedFile(FileInfoReception fileInfoReception);
	void receivingFile(FileInfoReception fileInfoReception);
	void receivedDatabase();
	void disconnected();
}
