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
	public void received(String msg);
	public void receivedBitmap(Bitmap bitmap);
	public void receivedFile(FileInfoReception fileInfoReception);
	public void disconnected();
}
