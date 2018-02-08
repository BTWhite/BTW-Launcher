package org.bitcoinwhite.launcher.core.tools;

import java.io.File;

public interface UpdateViewer {
	
	public void onDownloading(File file);
	public void onDownloaded(File file);
}

