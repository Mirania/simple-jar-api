package api;

import java.util.Map;
import java.util.Random;
import org.json.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Api {

	private TemporaryDatabase db;
	
	public Api() {
		db = new TemporaryDatabase();
	}
	
	//api methods ######################################################################################## 
	
	@RequestMapping(value = "/list/{uid}/{listid}")
    public ResponseEntity<String> list(@PathVariable("uid") String uid,
    									  @PathVariable("listid") String listid) throws JSONException {
    	
    	User user;
    	ShoppingList list;
    	JSONObject o;
    	
    	//check args
    	
    	if (!db.isValidKey(uid)) return templateErrorResponse("invalid user id in URL");
    	else if (!db.userExists(uid)) return templateErrorResponse("user does not exist");
    	
    	user = db.getUser(uid);
    	
    	if (!db.isValidKey(listid)) return templateErrorResponse("invalid list id in URL");
    	else if (!user.listExists(listid)) return templateErrorResponse("list does not exist for this user");
    	
    	list = user.getList(listid);
    	
    	//answer
    	
    	o = new JSONObject();
    	
    	o.put("uid", Integer.parseInt(uid));
    	o.put("list_id", Integer.parseInt(listid));
		o.put("date", list.getPrintableDate());
		o.put("total", list.getTotal());
		
		JSONArray items = new JSONArray();
		for (Purchase p: list.getPurchases()) {
			JSONObject entry = new JSONObject();
			entry.put("id", p.getId());
			entry.put("name", p.getName());
			entry.put("amount", p.getAmount());
			entry.put("unit_price", p.getUnitPrice());
			entry.put("total_price", p.getTotalPrice());
			items.put(entry);
		}
		
		o.put("items", items);
    	
    	return okResponse(o);
    }
	
	@RequestMapping(value = "/user/{uid}")
    public ResponseEntity<String> user(@PathVariable("uid") String param) throws JSONException {
    	
    	User user;
    	JSONObject o;
    	
    	//check args
    	
    	if (!db.isValidKey(param)) return templateErrorResponse("invalid user id in URL");
    	else if (!db.userExists(param)) return templateErrorResponse("user does not exist");
    	
    	user = db.getUser(param);
    	
    	//answer
    	
    	o = new JSONObject();
    	
    	o.put("uid", Integer.parseInt(param));
    	o.put("name", user.getName());
		o.put("address", user.getAddress());
		o.put("fnumber", user.getFnumber());
		o.put("key", user.getBase64PublicKey());
    	
    	return okResponse(o);
    }
    
    @RequestMapping(value = "/history/{uid}")
    public ResponseEntity<String> history(@PathVariable("uid") String param) throws JSONException {
    	
    	User user;
    	JSONObject o;
    	JSONArray array;
    	
    	//check args
    	
    	if (!db.isValidKey(param)) return templateErrorResponse("invalid user id in URL");
    	else if (!db.userExists(param)) return templateErrorResponse("user does not exist");
    	
    	user = db.getUser(param);
    	
    	//answer
    	
    	o = new JSONObject();
    	array = new JSONArray();
    	
    	o.put("uid", Integer.parseInt(param));
    	o.put("lists", array);
    	for (ShoppingList list: user.getLists()) {
    		JSONObject entry = new JSONObject();
    		entry.put("uuid", list.getUuid());
    		entry.put("date", list.getPrintableDate());
    		entry.put("total", list.getTotal());
    		
    		JSONArray items = new JSONArray();
    		for (Purchase p: list.getPurchases()) {
    			JSONObject subentry = new JSONObject();
    			subentry.put("id", p.getId());
    			subentry.put("name", p.getName());
    			subentry.put("amount", p.getAmount());
    			subentry.put("unit_price", p.getUnitPrice());
    			subentry.put("total_price", p.getTotalPrice());
    			items.put(subentry);
    		}
    		
    		entry.put("items", items);
    		array.put(entry);
    	}
    	
    	return okResponse(o);
    }
    
    @RequestMapping(value = "/barcodes")
    public ResponseEntity<String> barcodes() throws JSONException {

    	JSONObject o = new JSONObject();
    	JSONObject keys = new JSONObject();
    	
    	for (Long key: db.getBarcodeList().keySet()) {
    		JSONObject entry = new JSONObject();
    		entry.put("name", db.getBarcodeList().get(key).getName());
    		entry.put("unit_price", db.getBarcodeList().get(key).getPrice());
    		keys.put(key.toString(), entry);		
    	}
    	
    	o.put("date", db.getCurrentDate());
    	o.put("barcodes", keys);
    	
    	return okResponse(o);
    }
    
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public ResponseEntity<String> pay(@RequestBody Map<String,String> body) throws JSONException {
        
    	ShoppingList list = new ShoppingList();
    	User user;
    	JSONArray array;
    	
    	//check args
    	
    	if (body.get("uid")==null) return missingFieldResponse("uid");
    	else if (!db.isValidKey(body.get("uid"))) return invalidFieldResponse("uid");
    	else if (!db.userExists(body.get("uid"))) return templateErrorResponse("user must register first");
    	
    	user = db.getUser(body.get("uid"));
    	
    	if (body.get("signature")==null) return missingFieldResponse("signature");
    	
    	if (body.get("list")==null) return missingFieldResponse("list");
    	try {
    		array = new JSONArray(body.get("list"));
    	} catch (JSONException e) { return invalidFieldResponse("list"); }
    	
    	//validate signature
    	
    	if (!user.verifySignature(body.get("list"), body.get("signature")))
    		return templateErrorResponse("signature validation failed");
    	
    	//verify card val
    	
    	if (!user.hasValidCard()) 
    		return templateErrorResponse("credit card has expired");
    	
    	//perform payment
    	
    	if (new Random().nextDouble()>=0.95) 
    		return templateErrorResponse("an unexpected error occurred");
    	
    	//save and return
    	
    	for (int i=0;i<array.length();i++) {
    		JSONObject o = array.getJSONObject(i);
    		if (!o.has("id")) return missingItemFieldResponse("id");
    		else if (!o.has("amount")) return missingItemFieldResponse("amount");
    		
    		long id;
    		double amt;
    		
    		try {
    			id = Long.parseLong(o.get("id").toString());
    		} catch (NumberFormatException e) { return invalidItemFieldResponse("id"); }
    		
    		try {
    			amt = Double.parseDouble(o.get("amount").toString());
    		} catch (NumberFormatException e) { return invalidItemFieldResponse("amount"); }
    		
    		if (!db.itemExists(id)) return templateErrorResponse("item with id '"+id+"' is unknown");

    		list.addPurchase(new Purchase(db.getItem(id), amt));
    	}
    	
    	int id = db.generateListUuid();
    	list.setUuid(id);
    	user.addList(list);
    	
    	return okResponse(new JSONObject().put("uuid", id));
    	
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody Map<String,String> body) throws JSONException {
    	
    	User user = new User();
    	
    	//check args
    	
    	if (body.get("name")!=null) user.setName(body.get("name"));
    	else return missingFieldResponse("name");
    	
    	if (body.get("address")!=null) user.setAddress(body.get("address"));
    	else return missingFieldResponse("address");
        
    	if (body.get("fnumber")==null) return missingFieldResponse("fnumber");
    	else if (!user.setFnumber(body.get("fnumber"))) return invalidFieldResponse("fnumber");
    	
    	if (body.get("cardtype")!=null) user.setCardtype(body.get("cardtype"));
    	else return missingFieldResponse("cardtype");
    	
    	if (body.get("cardnum")!=null) user.setCardnum(body.get("cardnum"));
    	else return missingFieldResponse("cardnum");
    	
    	if (body.get("cardval")==null) return missingFieldResponse("cardval");
    	else if (!user.setCardval(body.get("cardval"))) return invalidFieldResponse("cardval");
    	
    	if (body.get("key")==null) return missingFieldResponse("key");
    	else if (!user.setKey(body.get("key"))) return invalidFieldResponse("key");
    	
    	//save and return
    	
    	int uid = db.addUser(user);
    	
    	return okResponse(new JSONObject().put("uid", uid));
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUser(@RequestBody Map<String,String> body) throws JSONException {
    	
    	User user;
    	String uid;
    	
    	//check args
    	
    	if (body.get("uid")==null) return missingFieldResponse("uid");
    	
    	uid = body.get("uid");
    	if (!db.isValidKey(uid)) return invalidFieldResponse("uid");
    	if (!db.userExists(uid)) return templateErrorResponse("user must register first. Use POST instead");
    	user = db.getUser(uid);

		//update
    	
    	if (body.get("name")!=null) user.setName(body.get("name"));
    	
    	if (body.get("address")!=null) user.setAddress(body.get("address"));

    	if (body.get("fnumber")!=null && !user.setFnumber(body.get("fnumber"))) 
    		return invalidFieldResponse("fnumber");
    	
    	if (body.get("cardtype")!=null) user.setCardtype(body.get("cardtype"));
    	
    	if (body.get("cardnum")!=null) user.setCardnum(body.get("cardnum"));

    	if (body.get("cardval")!=null && !user.setCardval(body.get("cardval"))) 
    		return invalidFieldResponse("cardval");

    	if (body.get("key")!=null && !user.setKey(body.get("key"))) 
    		return invalidFieldResponse("key");
    	
    	//return
    	
    	return okResponse(new JSONObject().put("uid", uid));
    }
    
    
    //helper methods #####################################################################################    
    
    private ResponseEntity<String> okResponse(JSONObject n) throws JSONException {
    	return ResponseEntity.status(HttpStatus.OK).body(n.toString());
    }
    
    private ResponseEntity<String> templateErrorResponse(String msg) throws JSONException {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    			.body(new JSONObject().put("error", msg).toString());
    }

    private ResponseEntity<String> missingFieldResponse(String n) throws JSONException {
    	return templateErrorResponse("missing '"+n+"' field");
    }
    
    private ResponseEntity<String> missingItemFieldResponse(String n) throws JSONException {
    	return templateErrorResponse("missing '"+n+"' field of an item in 'list'");
    }
    
    private ResponseEntity<String> invalidFieldResponse(String n) throws JSONException {
    	return templateErrorResponse("invalid content in '"+n+"' field");
    }
    
    private ResponseEntity<String> invalidItemFieldResponse(String n) throws JSONException {
    	return templateErrorResponse("invalid content in '"+n+"' field of an item in 'list'");
    }
    
}
