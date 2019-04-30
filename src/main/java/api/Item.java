package api;

public class Item {

	private final long id; //barcode
	private final double price;
	private final String name;
	
	public Item(long barcode, String name, double price) {
		id = barcode;
		this.name = name;
		this.price = price;
	}
	
	public long getId() {
		return id;
	}
	
	public double getPrice() {
		return price;
	}
	
	public String getName() {
		return name;
	}

}
