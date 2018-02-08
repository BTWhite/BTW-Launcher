package org.bitcoinwhite.launcher;

import java.io.File;

import org.bitcoinwhite.launcher.core.Finder;
import org.bitcoinwhite.launcher.core.tools.UpdateViewer;
import org.bitcoinwhite.launcher.core.Updater;

public class Console {

	
	public static void main(String[] args) {
		
		
		if(args.length < 1) {
			printHelp();
		} else if(args[0].equals("scan")) {
			printScan();
		} else if(args[0].equals("update")) {
			printUpdate();
		} else if(args[0].equals("version")) {
			printVersion();
		} else {
			printHelp();
		}
	}
	
	
	public static void printHelp() {
		
		System.out.println("Usage: programm [command]");
		System.out.println(" * scan");
		System.out.println(" * update");
		System.out.println(" * version");
	}
	
	public static void printScan() {
		Finder finder = new Finder("./");
		File[] files = finder.scan();
		
		if(files.length > 0) {
			System.out.println("You need to update the following files: ");
			
			for(File file : files) System.out.println(file.getPath());
			
			System.out.println("\nUse command \"update\" to update.");
		} else printLatest();
	}
	
	public static void printUpdate() {
		Finder finder = new Finder("./");
		if(finder.scan().length == 0) {
			printLatest();
			return;
		}
		
		Updater updater = new Updater(finder);
		System.out.println("Updating...");
		
		updater.update(new UpdateViewer() {

			@Override
			public void onDownloading(File file) {
				System.out.print(file.getPath() + " downloading... ");	
			}

			@Override
			public void onDownloaded(File file) {
				System.out.print("Done!\n");	
			}

			
			
		});
	}
	
	public static void printLatest() {
		System.out.println("You use the latest version!");
	}
	
	public static void printVersion() {
		System.out.println(Main.version);
	}
	
}
