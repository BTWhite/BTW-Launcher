package org.bitcoinwhite.launcher.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bitcoinwhite.launcher.core.tools.Api;
import org.bitcoinwhite.launcher.core.tools.UpdateViewer;

public class Updater {
	private Finder finder;
	private Api api;
	
	
	public Updater(Finder finder) {
		this(finder, finder.getApi());
	}
	public Updater(Finder finder, Api api) {
		this.finder = finder;
		this.api = api;
	}
	
	public boolean update(UpdateViewer uv) {
		
		File[] files = finder.getArray();
		
		for(File file : files) {
			try{
				if(!file.getParentFile().exists() && file.getParentFile().mkdirs()) {}
			} catch(NullPointerException e) {}
			
			if(uv != null) {
				uv.onDownloading(file); 
			}
			
			api.download(file.getPath());
			
			if(uv != null) {
				uv.onDownloaded(file); 
				if(file.getName().endsWith(".zip")) {
					unZip(file.getPath());
				}
			}
			
		}
		return true;
	}
	
	private String destinationDirectory(final String srcZip) {
        return srcZip.substring(0, srcZip.lastIndexOf("."));
    }
	
	private void unZip(final String zipFileName) {
        byte[] buffer = new byte[1028];
 
        final String dstDirectory = destinationDirectory(zipFileName);
        final File dstDir = new File(dstDirectory);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
 
        try {
            final ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFileName));
            ZipEntry ze = zis.getNextEntry();
            String nextFileName;
            while (ze != null) {
                nextFileName = ze.getName();
                File nextFile = new File(dstDirectory + File.separator
                        + nextFileName);
                System.out.println("Unzipping: " + nextFile.getAbsolutePath());
                
                if (ze.isDirectory()) {
                    nextFile.mkdir();
                } else {
                    new File(nextFile.getParent()).mkdirs();	
                    try (FileOutputStream fos
                            = new FileOutputStream(nextFile)) {
                        int length;
                        while((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Updater.class.getName())
                    .log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
