package api;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TemporaryDatabase {
	
	private Map<Integer,User> users;
	private Map<Long,Item> items; //barcodes are map keys
	private int listcount;
	
	public TemporaryDatabase() {
		users = new HashMap<>();
		items = new HashMap<>();
		listcount = 1000000;
		readItemsResource();
	}
	
	public int addUser(User user) {
		int uid = users.size()+1;
		users.put(uid, user);
		return uid;
	}
	
	public int generateListUuid() {
		return ++listcount;
	}
	
	public User getUser(int uid) {
		return users.get(uid);
	}
	
	public User getUser(String uid) {
		return users.get(Integer.parseInt(uid));
	}
	
	public Map<Long,Item> getBarcodeList() {
		return items;
	}
	
	public Item getItem(long barcode) {
		return items.get(barcode);
	}
	
	public boolean isValidKey(String uid) {
		try {
			Integer.parseInt(uid);
		} catch (NumberFormatException e) { return false; }
		
		return true;
	}
	
	public boolean userExists(String uid) {
		try {
			return users.get(Integer.parseInt(uid))!=null;
		} catch (NumberFormatException e) { return false; }		
	}
	
	public boolean itemExists(long barcode) {
		return items.containsKey(barcode);
	}
	
	public String getCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(new Date());
	}
	
	private void readItemsResource() {
		InputStream stream = TemporaryDatabase.class.getResourceAsStream("../items.txt");
		Scanner sc = new Scanner(stream);
		sc.nextLine(); //skip header
		
		while (sc.hasNextLine()) {
			String[] parts = sc.nextLine().split(",");
			long barcode = Long.parseLong(parts[0]);
			String name = parts[1].trim();
			double price = Double.parseDouble(parts[2]);
			items.put(barcode, new Item(barcode,name,price));
		}
		
		sc.close();
	}

}
