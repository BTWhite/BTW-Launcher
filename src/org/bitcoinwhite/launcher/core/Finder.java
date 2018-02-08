package org.bitcoinwhite.launcher.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bitcoinwhite.launcher.core.tools.Api;
import org.bitcoinwhite.launcher.core.tools.Helper;

import com.google.gson.JsonElement;

public class Finder {
	private String dir;
	private Api api;
	private HashMap<String, String> serverList = new HashMap<String, String>();
	private File[] array;
	
	public Finder(String dir) {
		this.dir = dir;
	}
	
	public String getDir() { return this.dir; }
	
	public File[] scan()  {
		Set<Entry<String, JsonElement>> serverFiles = this.getApi().getMd5();
		ArrayList<File> list = new ArrayList<File>();
		array = null;
		for(Map.Entry<String,JsonElement> entry : serverFiles) { 
			serverList.put(entry.getKey(), entry.getValue().getAsString());
			
			File file = new File(entry.getKey());
			try {
				if(!file.exists() || (!Helper.getMd5File(file.getPath()).equals(entry.getValue().getAsString()))
					) {
					
					if(!file.getName().equals("config.json")) list.add(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		array = new File[list.size()];
		for(int i = 0; i < array.length; i++) 
			array[i] = list.get(i);
		
		return array;
		
	}	
	
	
	public Api getApi() {

		if(api == null) api = new Api();
		return api;
	}
	
	public File[] getArray() {
		return this.array;
	}
}
