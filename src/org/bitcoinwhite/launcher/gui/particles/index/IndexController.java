package org.bitcoinwhite.launcher.gui.particles.index;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.bitcoinwhite.launcher.Main;
import org.bitcoinwhite.launcher.core.Finder;
import org.bitcoinwhite.launcher.core.Updater;
import org.bitcoinwhite.launcher.core.tools.Node;
import org.bitcoinwhite.launcher.core.tools.NodeViewer;
import org.bitcoinwhite.launcher.core.tools.UpdateViewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;


public class IndexController implements UpdateViewer, Runnable, NodeViewer {
	public Text progressText;
	public ProgressIndicator progressBar;
	public TextArea out;
	public Button startNodeBtn;
	
	private static final int LIST_UPDATE_COUNT = 15;
	private Finder finder;
	
	private int maxFiles;
	private int nowFiles;
	private File nowFile;
	
	private static Node node;
	
	//private String[] buff = new String[20];
	
	@FXML
    public void initialize() {
		System.out.println("Initialize...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					if(nowFile == null) {
						try {
							Thread.sleep(500);
							continue;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					progressText.setText(nowFile.getPath() + " ("+ (nowFile.length()/1024) +"KB)");
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
		
		
		finder = new Finder("./");
		File[] files = finder.scan();
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { }
		
		setStatusUpdate(true);
		if(files.length > 0) {

			Object[] options = {"Yes", "No"};
			String text = "Hello! The following files were updated: \n\n";
			for(int i = 0; i<files.length; i++) {
				if(i > LIST_UPDATE_COUNT) { 
					text = text.concat("...\n");
					break;
				}
				else {
					text = text.concat(files[i].getPath() + "\n");
				}
			}
			maxFiles = files.length;
			text = text.concat("\nTotally: " + files.length);
			int result = JOptionPane.showOptionDialog(null, text,
				    "Update the software",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[1]);
			
			if(result == 0) {
				
				setStatusUpdate(false);
				
				new Thread(this).start();
				return;
			}
			
		}
		
		
		return;
    }


	public void setStatusUpdate(boolean status) {
		if(status) {
			startNodeBtn.setVisible(true);
			progressText.setVisible(false);
			progressBar.setProgress(1f);
		} else {
			startNodeBtn.setVisible(false);
			progressText.setVisible(true);
			progressBar.setProgress(0f);
		}
	}
	
	@Override
	public void onDownloading(File file) {
		
		if(file == null) return;
		nowFile = file;
	}


	@Override
	public void onDownloaded(File file) {

		if(file == null) return;
		nowFile = null;
		
		double val = (double)((double)++nowFiles/(double)maxFiles);
		
		progressBar.setProgress(val);
		
		
		if(val == 1) {
			setStatusUpdate(true);
		}
	}


	@Override
	public void run() {
		Updater updater = new Updater(finder);
		updater.update(this);
	}
	
	
	
	public void startNode() {
		if(node != null) {
			node.stop();
			node = null;
		}
		IndexController ic = this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				node = new Node("bin\\btwnode.exe", "index.js");
				try {
					node.start(ic);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "* Node not started, please check src/logs/debug.log");
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	
	public static Node getNode() {
		return node;
	}

	
	private boolean nodeStarted = false;
	
	@Override
	public void onMessage(String text) {
		if(nodeStarted) return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				long startTime = (System.currentTimeMillis() / 1000L);
				long diff = 0;
				while(diff < 60) {
					diff = (System.currentTimeMillis() / 1000L) - startTime;
					if(text.indexOf("BTW started") != -1) {
						nodeStarted = true;
						JOptionPane.showMessageDialog(null, "Node stated!");
						
						return;
					}
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
				if(nodeStarted) return;
				if(node != null) {
					node.stop();
					node = null;
				}
				JOptionPane.showMessageDialog(null, "Node not started, please check src/logs/debug.log");
				System.exit(1);
			}
			
		}).start();
		
	}
	
}
