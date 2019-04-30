# **Java API branch**

To run: `mvn spring-boot:run`

To compile into jar: `mvn clean package`

Base path: **http://localhost:8080/**

## /register (creates new user)

**Type:** POST

**Requires:** JSON string

**Fields:**

- name - `string`
	
- address - `string`
	
- fnumber - `string` (length must be 9, numbers only)

- cardtype - `string`

- cardnum - `string`

- cardval - `string` (yyyy-MM date, ex: 2012-10)

- key - `string` (Base64 encoded RSA public key)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**
	
- uid - `integer`

## /register (updates existing user)

**Type:** PUT

**Requires:** JSON string

**Fields:**

- uid - `integer`

- name - `string` (optional)
	
- address - `string` (optional)
	
- fnumber - `string` (optional) (length must be 9, numbers only)

- cardtype - `string` (optional)

- cardnum - `string` (optional)

- cardval - `string` (optional) (yyyy-MM date, ex: 2012-10)

- key - `string` (optional) (Base64 encoded RSA public key)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**
	
- uid - `integer`
	
## /pay

**Type:** POST

**Requires:** JSON string

**Fields:**

- uid - `integer`

- list - `string` (a JSON array **with " before and after**, ex: "[abc]")

  - id - `long` (barcode)

  - amount - `double`

- signature - `string` (SHA1WithRSA signature of **list**, without the " symbols)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**

- uuid - `integer`
	
## /history/{uid}

**Type:** GET

**Requires:** uid (must be integer)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**

- uid - `integer`

- list - `array`

  - date - `string` (yyyy-MM-dd HH:mm, ex: 2019-03-02 19:57)

  - total - `double`

  - uuid - `integer`

  - items - `array`
  
    - id - `long`
	
	- name - `string`
    
    - amount - `double`
    
    - unit_price - `double`
	
	- total_price - `double`
		
## /barcodes

**Type:** GET

**Returns:** JSON string

**Success fields:**

- date - `string` (yyyy-MM-dd HH:mm, ex: 2019-03-02 19:57)

- barcodes - `JSON object`

  - **each key is a barcode** (`string` value of a long) **and points to a** `JSON object`

	- name - `string`
	
	- unit_price - `double`

## /list/{uid}/{list_id}

**Type:** GET

**Requires:** uid (must be integer), list_id (must be integer)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**

- uid - `integer`

 - list_id - `integer`

- date - `string` (yyyy-MM-dd HH:mm, ex: 2019-03-02 19:57)

- total - `double`

- items - `array`
  
  - id - `long`
	
  - name - `string`
    
  - amount - `double`
    
  - unit_price - `double`
	
  - total_price - `double`
  
## /user/{uid}

**Type:** GET

**Requires:** uid (must be integer)

**Returns:** JSON string

**Error fields:**

- error - `string`
	
**Success fields:**

- uid - `integer`

- name - `string`
	
- address - `string` 
	
- fnumber - `string` 

- key - `string` (Base64 encoded RSA public key)
