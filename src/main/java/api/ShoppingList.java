package api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingList {
	
	private List<Purchase> purchases;
	private Date date;
	private int uuid;
	
	public ShoppingList() {
		purchases = new ArrayList<>();
		date = new Date();
	}
	
	public String getPrintableDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}
	
	public void addPurchase(Purchase p) {
		purchases.add(p);
	}
	
	public List<Purchase> getPurchases() {
		return purchases;
	}

	public Date getDate() {
		return date;
	}

	public int getUuid() {
		return uuid;
	}

	public void setUuid(int uuid) {
		this.uuid = uuid;
	}

	public double getTotal() {
		double t = 0;
		for (Purchase p: purchases)
			t += p.getTotalPrice();
		return t;
	}

}
