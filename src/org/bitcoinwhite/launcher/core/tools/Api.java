package org.bitcoinwhite.launcher.core.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Api {
	private static final String root = "http://5.39.122.197/launcher?action=";
	private static final String rootAlt = "http://5.39.122.197/launcher/data/";
	
	
	public JsonObject request(String action) {
		
		try {
			URL obj = new URL(root + action);
		
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
	
			try {
				connection.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
	
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
			    response.append(inputLine);
			}
			in.close();
	
			Gson gson = new Gson();
			
			return gson.fromJson(response.toString(), JsonObject.class);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public Set<Entry<String, JsonElement>> getMd5() {
		JsonObject response = request("getMd5");
		if(response.get("success").getAsBoolean()) {
			JsonObject files = response.get("files").getAsJsonObject();
			Set<Entry<String, JsonElement>> entrySet = files.entrySet();
			
			return entrySet;
		}
		return null;
	}
	
	
	public boolean download(String filepath) {
		URL website;
		
		try {
			
			website = new URL(root + "GetFile&name=" + filepath);
			try {
				
				downloadByUrl(website, filepath, true);
				
			} catch (IOException e) {
				website = new URL(rootAlt + filepath.replaceAll("\\\\", "/"));
				try {
					downloadByUrl(website, filepath, false);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
			}
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	public void downloadByUrl(URL website, String filepath, boolean fix) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);
		FileOutputStream fos = new FileOutputStream(filepath);
		int i = 0;
		while (rbc.read(buffer) != -1 || buffer.position() > 0) { 
			buffer.flip();
			byte b = buffer.get();
			buffer.compact();
			i++;
			
			if(fix && (i == 1 || i == 2)) continue;
			
			fos.write(b);	
		}
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}
	
	
}
