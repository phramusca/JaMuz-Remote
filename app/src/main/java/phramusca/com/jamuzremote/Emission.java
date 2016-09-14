package phramusca.com.jamuzremote;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Emission extends ProcessAbstract {

	private final PrintWriter printWriter;
	private final BlockingQueue<String> outQueue;
	
	public Emission(PrintWriter printWriter) {
		super("Thread.Client.Emission");
		this.printWriter = printWriter;
		outQueue = new LinkedBlockingQueue<>();
	}

	public boolean send(String msg) {
		try {
			outQueue.put(msg);
			return true;
		} catch (InterruptedException ex) {
//			Logger.getLogger(Emission.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}
	
	@Override
	public void run() {
		try {
			String msg;
			while ((msg = outQueue.take())!=null) {
				checkAbort();
				printWriter.println(msg);
//                printWriter.println(msg+"\n");
				printWriter.flush();
			}
		} catch (InterruptedException ex) {
//			Logger.getLogger(Emission.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}