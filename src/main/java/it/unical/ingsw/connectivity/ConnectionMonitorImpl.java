package it.unical.ingsw.connectivity;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is to simulate an Internet Connection Manager, to show how Mockito
 * library is able to mock instances and enforce class methods behavior
 */
public class ConnectionMonitorImpl implements ConnectionMonitor{

	private String trustUrl = "http://www.google.com";

	public boolean isConnected() {
		try {
			URL url = new URL(trustUrl);
			//InetAddress[] addresses = InetAddress.getAllByName("www.google.com");
			URLConnection connection = url.openConnection();
			connection.connect();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
