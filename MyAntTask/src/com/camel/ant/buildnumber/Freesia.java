package com.camel.ant.buildnumber;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Freesia {
	private static final String TAG = "Freesia";
	private static final boolean DEBUG = true;
	
	public String getBuildNumber() throws IOException {
		String url = "http://freesia.sinaapp.com/api/camel/inc/";
		URL licenceUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) licenceUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		int res = conn.getResponseCode();
		if (DEBUG) System.out.println("ResponseCode = " + res);
		if (res == 200) {
			byte[] buffer= new byte[1024];
			int len = 0;
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			while (-1 != (len = is.read(buffer))) {
				String str = new String(buffer, 0, len);
				sb.append(str);
			}
			if (DEBUG) System.out.println("sb = " + sb.toString());
			return sb.toString();
		}
		return "";
	}
	
	public void updateBuildNumber(int buildnumber) {
		try {
			String url = "http://freesia.sinaapp.com/api/camel/update/id=1&bdnumber="+buildnumber;
			URL licenceUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) licenceUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			int res = conn.getResponseCode();
			if (DEBUG) System.out.println("ResponseCode = " + res);
			if (res == 200) {
				byte[] buffer= new byte[1024];
				int len = 0;
				InputStream is = conn.getInputStream();
				StringBuilder sb = new StringBuilder();
				while (-1 != (len = is.read(buffer))) {
					String str = new String(buffer, 0, len);
					sb.append(str);
				}
				if (DEBUG) System.out.println("sb = " + sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
