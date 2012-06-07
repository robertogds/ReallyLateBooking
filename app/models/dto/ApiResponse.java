package models.dto;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
	public static final String OK = "ok";
	public static final String ERROR = "error";
	private Map<String, Object> response;
	public Map<String, Object> json;

	public ApiResponse() {
		super();
		this.response = new HashMap<String, Object>();
		this.json = new HashMap<String, Object>();
		json.put("response", response);
	}
	
	public void setStatus(String status){
		response.put("status", "ok");
	}
	
	public void setTotal(String total){
		response.put("total", total);
	}
	
	public void put(String key, Object value){
		response.put(key, value);
	}
	
}
