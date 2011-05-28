package helper;


/**
 * @author pablopr
 * Shows status info about a request success or failure
 */
public class StatusMessage {
	   public int status;
	   public String message;
	   public String detail;

	   public StatusMessage(int status, String message, String detail) {
	      this.status = status;
	      this.message = message;
	      this.detail = detail;
	   }
	   
}
