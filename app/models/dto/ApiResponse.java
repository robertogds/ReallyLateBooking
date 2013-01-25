package models.dto;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Http;

public class ApiResponse {
	public Map<String, Object> json;

	public ApiResponse() {
		super();
		this.json = new HashMap<String, Object>();
		this.setStatus(Http.StatusCode.OK);
	}
	
	public ApiResponse(String key, String value){
		this();
		this.put(key, value);
	}
	
	public ApiResponse(int status, String key, String value) {
		this(key, value);
		setStatus(status);
	}

	public void setStatus(int status){
		json.put("status", status);
	}
	
	public void setTotal(String total){
		json.put("total", total);
	}
	
	public void put(String key, Object value){
		json.put(key, value);
	}
	
}
