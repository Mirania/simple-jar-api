package api;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class User {

	private String name, address, fnumber;
	private String cardtype, cardnum;
	private Date cardval; 
	private PublicKey publickey;
	private List<ShoppingList> lists;
	
	public User() {
		lists = new ArrayList<>();
	}
	
	public boolean hasValidCard() {
		return new Date().before(cardval);
	}
	
	public boolean verifySignature(String original, String sign) {
		try {
			Signature sig = Signature.getInstance("SHA1WithRSA");
			byte[] target = original.getBytes();
			byte[] base = Base64.getDecoder().decode(sign);
	        sig.initVerify(publickey);
	        sig.update(target);
	        return sig.verify(base);
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) { return false; }
	}
	
	public boolean listExists(String id) {
		try {
			int v = Integer.parseInt(id);
			for (ShoppingList list: lists) {
				if (list.getUuid()==v) return true;
			}
			return false;
		} catch (NumberFormatException e) { return false; }		
	}
	
	public ShoppingList getList(String id) {
		try {
			int v = Integer.parseInt(id);
			for (ShoppingList list: lists) {
				if (list.getUuid()==v) return list;
			}
			return null;
		} catch (NumberFormatException e) { return null; }		
	}
	
	public String getBase64PublicKey() {
		return new String(Base64.getEncoder().encode(publickey.getEncoded()));
	}
	
	public PublicKey getKey() {
		return publickey;
	}	
	public boolean setKey(String key) {
		try {
			byte[] base = Base64.getDecoder().decode(key);
			this.publickey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(base));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) { return false; }
		
		return true;
	}
	public List<ShoppingList> getLists() {
		return lists;
	}
	public void addList(ShoppingList s) {
		lists.add(s);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFnumber() {
		return fnumber;
	}
	public boolean setFnumber(String fnumber) {
		if (fnumber.length()!=9) return false;
		try {
    		Integer.parseInt(fnumber);
    	} catch (NumberFormatException e) { return false; }
		
		this.fnumber = fnumber;
		return true;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardnum() {
		return cardnum;
	}
	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}
	public Date getCardval() {
		return cardval;
	}
	public boolean setCardval(String cardval) {
		try {
			this.cardval = new SimpleDateFormat("yyyy-MM").parse(cardval);
    	} catch (ParseException e) { return false; };

		return true;
	}
}
