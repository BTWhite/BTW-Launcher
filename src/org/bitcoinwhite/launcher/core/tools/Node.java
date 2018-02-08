package org.bitcoinwhite.launcher.core.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Node implements Runnable {
	private String node;
	private String application;
	private BufferedReader brError;
	private BufferedReader brOutput;
	private Process process;
	private NodeViewer nv;
	
	public Node(String node, String application) {
		this.node = node;
		this.application = application;
	}
	
	
	public void stop() {
		process.destroy();
	}
	
	public void start(NodeViewer nv) throws IOException {
		this.nv = nv;
		if((System.getProperty("os.name").toLowerCase().indexOf("win") < 0)) {
			System.out.println("Use node index.js using your own nodejs");
			return;
		}
		try {
			String[] command = {"CMD", "/C", this.node + " " + this.application};
	        ProcessBuilder query = new ProcessBuilder( command );
	        process = query.start();
	        brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			brOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
	      			
			new Thread(this).start();
			process.waitFor();
			
			try {
	            int exitValue = process.waitFor();
	            System.out.println("\n\nExit Value is " + exitValue);
	            System.exit(exitValue);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void run() {
		
		try {
			String s = null;
			while (true) {
				
				s = brOutput.readLine();
				if(s != null) resultString(s, false);
				if(brError.ready()) {
					s = brError.readLine();
					if(s != null) resultString(s, true);
				}
			    
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void resultString(String str, boolean error) {
		System.out.println(str);
		nv.onMessage(str);
	}
}
