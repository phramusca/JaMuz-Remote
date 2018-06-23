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
public interface ICallBackReception {
	void receivedJson(String json);
	void receivedBitmap(Bitmap bitmap);
	void receivingFile(FileInfoReception fileInfoReception);
	void receivedFile(FileInfoReception fileInfoReception);
	void disconnected(String msg);
}
