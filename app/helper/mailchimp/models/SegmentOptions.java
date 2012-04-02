package helper.mailchimp.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentOptions {
	public static final String MATCH_ALL = "all";
	public static final String MATCH_ANY = "any";
	public static final String FIELD_WEBSITE = "WEBSITE";
	public static final String FIELD_CITY = "CITY";
	public static final String FIELD_DEPT_DATE = "DEPT_DATE";
	public static final String OP_EQ = "eq";
	
	public String match;
	public List<Map> conditions;

	public SegmentOptions() {
		super();
		this.match =  MATCH_ALL;
		conditions = new ArrayList<Map>();
	}

	public void addCondition(String field, String op, String value){
		Map condition = new HashMap<String, String>();
		condition.put("field", field);
		condition.put("op", op);
		condition.put("value", value);
		conditions.add(condition);
	}
	
	public void addDateCondition(String field, String op, Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		String dateString = sdf.format(date);
		addCondition(field, op, dateString);
	}
	
	public void setMatchType(String type){
		this.match = type;
	}
}
