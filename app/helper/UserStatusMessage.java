package helper;

import models.User;

public class UserStatusMessage extends StatusMessage {
	
	 public User content;

     public UserStatusMessage(int status, String message, String detail, User content) {
	      super(status, message, detail);
	      this.content = content;
	}

}
