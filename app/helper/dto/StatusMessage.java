package helper.dto;

import com.google.gson.annotations.Expose;

import models.User;


/**
 * @author pablopr
 * Shows status info about a request success or failure
 */
public class StatusMessage {
	@Expose
	public int status;
	@Expose
	public String message;
	@Expose
	public String detail;

   public StatusMessage(int status, String message, String detail) {
      this.status = status;
      this.message = message;
      this.detail = detail;
   }
	  
	   
}
