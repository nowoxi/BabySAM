package com.androidproject.babysam;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;


public class HttpFileUpload {
	
	private int serverResponseCode;
	private String serverResponseMessage, TAG ="BabySAM";
	
	HttpFileUpload(String path,String urlServer){
		try {
			upload(path, urlServer);
		} catch (Exception e) {
			Log.e(TAG, "Error", e);
		}
	}
	
	
	public void upload (String pathToOurFile, String urlServer) throws Exception {
		  HttpURLConnection connection = null;
		  DataOutputStream outputStream = null;
		  String lineEnd = "\r\n";
		  String twoHyphens = "--";
		  String boundary =  "*****";
	
		  int bytesRead, bytesAvailable, bufferSize;
		  byte[] buffer;
		  int maxBufferSize = 1*1024*1024;
	
		  try{
			  FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
		
			  URL url = new URL(urlServer);
			  connection = (HttpURLConnection) url.openConnection();
		
			  // Allow Inputs & Outputs
			  connection.setDoInput(true);
			  connection.setDoOutput(true);
			  connection.setUseCaches(false);
		
			  // Enable POST method
			  connection.setRequestMethod("POST");
		
			  connection.setRequestProperty("Connection", "Keep-Alive");
			  connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
		
			  outputStream = new DataOutputStream( connection.getOutputStream() );
			  outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			  outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			  outputStream.writeBytes(lineEnd);
		
			  bytesAvailable = fileInputStream.available();
			  bufferSize = Math.min(bytesAvailable, maxBufferSize);
			  buffer = new byte[bufferSize];
		
			  // Read file
			  bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		
			  while (bytesRead > 0){
				  outputStream.write(buffer, 0, bufferSize);
				  bytesAvailable = fileInputStream.available();
				  bufferSize = Math.min(bytesAvailable, maxBufferSize);
				  bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			  }
		
			  outputStream.writeBytes(lineEnd);
			  outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		
			  // Responses from the server (code and message)
			  serverResponseCode = connection.getResponseCode();
			  serverResponseMessage = connection.getResponseMessage();
			  
			  Log.i(TAG, serverResponseCode +" "+ serverResponseMessage);
			  
			  fileInputStream.close();
			  outputStream.flush();
			  outputStream.close();
		  } catch (Exception ex) {
			  Log.e(TAG, "Error getting data from URL", ex);
		  }	  
	}
	
	public int getServerResponseCode(){
		return this.serverResponseCode;
	}
	
	public String getServerResponseMessage(){
		return this.serverResponseMessage;
	}
}
