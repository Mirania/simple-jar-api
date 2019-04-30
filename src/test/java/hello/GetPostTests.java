package hello;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.json.*;
import org.junit.Test;

public class GetPostTests {
	
	private String b = "http://localhost:8080/";
	private KeyPair kp;

	@Test
	public void gr() throws Exception {
		//sendGet(b+"greeting?name=User");
		//sendPost(b+"register", new JSONObject().put("df", 325));
		//sendPost(b+"register", new JSONObject().put("name", 325));
	}
	
	//@Test
	public void user() throws JSONException, IOException, NoSuchAlgorithmException {
		JSONObject j = new JSONObject();
		j.put("name", "n");
		j.put("address", "n");
		j.put("fnumber", "343434344");
		j.put("cardtype", "n");
		j.put("cardnum", "n");
		j.put("cardval", "2020-12");
		j.put("key", rsaKey());
		sendPost(b+"register",j);
	}
	
	//@Test
		public void userupdate() throws JSONException, IOException, NoSuchAlgorithmException {
			JSONObject j = new JSONObject();
			j.put("uid", "1");
			//j.put("name", "nxdx");
			//j.put("address", "n");
			//j.put("fnumber", "34d");
			/*j.put("cardtype", "n");
			j.put("cardnum", "n");
			j.put("cardval", "2020-12");*/
			j.put("key", rsaKey());
			sendPut(b+"register",j);
		}
	
	//@Test
	public void pay() throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		JSONObject j = new JSONObject();
		j.put("uid", 1);
		JSONArray a = new JSONArray();
		JSONObject j1 = new JSONObject().put("id", 61234567890L).put("amount", 2.22);
		JSONObject j2 = new JSONObject().put("id", 12853478357L).put("amount", 3.33);
		a.put(j1);
		a.put(j2);
		j.put("list", a.toString());
		j.put("signature", sign(a.toString()));
		sendPost(b+"pay",j);
	}
	
	public void pay2() throws IOException, JSONException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		JSONObject j = new JSONObject();
		j.put("uid", 1);
		JSONArray a = new JSONArray();
		JSONObject j1 = new JSONObject().put("id", 320).put("amount", 2.22).put("price", 17.75);
		JSONObject j2 = new JSONObject().put("id", 1).put("amount", 3.33243).put("price", 11);
		JSONObject j3 = new JSONObject().put("id", 2).put("amount", 2133.33).put("price", 1221);
		a.put(j1);
		a.put(j2);
		a.put(j3);
		j.put("list", a.toString());
		j.put("signature", sign(a.toString()));
		sendPost(b+"pay",j);
	}
	
	@Test
	public void history() throws Exception {
		user();
		//userupdate();
	}
	
	private String sign(String original) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		byte[] data = original.getBytes();
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(kp.getPrivate());
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        return new String(Base64.getEncoder().encode(signatureBytes));
	}
	
	private String rsaKey() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        kp = keyGen.genKeyPair();
        byte[] publicKey = kp.getPublic().getEncoded();  
        return new String(Base64.getEncoder().encode(publicKey));
	}
	
	private void sendPut(String urlstr, JSONObject body) throws IOException {
		String urlParameters = body.toString();
		URL url = new URL(urlstr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestProperty("Content-type", "application/json");
		conn.setRequestMethod("PUT");
		conn.setDoOutput(true);
		

		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

		writer.write(urlParameters);
		writer.flush();

		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		int responseCode = conn.getResponseCode();
		System.out.println("Sending 'PUT' request to URL : " + urlstr);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		}
		writer.close();
		reader.close();  
		
	}
	
	private void sendPost(String urlstr, JSONObject body) throws IOException {
		String urlParameters = body.toString();
		URL url = new URL(urlstr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestProperty("Content-type", "application/json");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		

		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

		writer.write(urlParameters);
		writer.flush();

		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		int responseCode = conn.getResponseCode();
		System.out.println("Sending 'POST' request to URL : " + urlstr);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		}
		writer.close();
		reader.close();  
		
	}
	
	private void sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("GET");

		String urlParameters = "";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("Sending 'GET' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString()+"\n");

	}
}
