package it.unical.ingsw;

import it.unical.ingsw.connectivity.ConnectionMonitorImpl;
import it.unical.ingsw.http.ApiClient;
import it.unical.ingsw.http.OKHttpApiClientImpl;
import okhttp3.OkHttpClient;

public class Main {

	public static void main(String[] args) {
		
		ApiClient hClient = new OKHttpApiClientImpl("https://randommer.io/", new OkHttpClient());
		MyMath m = new MyMath(new ConnectionMonitorImpl(), hClient);
		try {
			System.out.println(m.remoteFibonacci(300));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
