package com.rr.sdextract;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpHelper {

	private static final String USER_AGENT = "Mozilla/5.0";
	
	public static String getAsString(String url) {
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpGet request = new HttpGet(url);
	
			request.addHeader("User-Agent", USER_AGENT);
	
			HttpResponse response = client.execute(request);
	
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
	
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static Image getAsImage(String url) {
		Image image = null;
		try {
		    image = ImageIO.read(new URL(url));
		} catch (IOException e) {
		}
		return image;
	}
	
}
