package api;

public class Purchase {
	
	private final Item item;
	private final double amount;
	
	public Purchase(Item item, double amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public String getName() {
		return item.getName();
	}
	
	public long getId() {
		return item.getId();
	}
	
	public double getUnitPrice() {
		return item.getPrice();
	}
	
	public double getAmount() {
		return amount;
	}
	
	public double getTotalPrice() {
		return item.getPrice() * amount;
	}

}
