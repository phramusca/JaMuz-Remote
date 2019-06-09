package phramusca.com.jamuzkids;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientEmission extends ProcessAbstract {

	private final PrintWriter printWriter;
	private final BlockingQueue<String> outQueue;
	
	public ClientEmission(PrintWriter printWriter) {
		super("Thread.Client.ClientEmission");
		this.printWriter = printWriter;
		outQueue = new LinkedBlockingQueue<>();
	}

	public boolean send(String msg) {
		try {
			checkAbort();
			outQueue.put(msg);
			return true;
		} catch (InterruptedException ex) {
			return false;
		}
	}
	
	@Override
	public void run() {
		try {
			String msg;
			//Retrieves and removes the head of outQueue, waiting if necessary
			// until an element becomes available.
			while ((msg = outQueue.take())!=null) {
				checkAbort();
				printWriter.println(msg);
				printWriter.flush();
			}
		} catch (InterruptedException ignored) {
		}
	}
}