package helper.dto;

import models.User;

public class UserStatusMessage extends StatusMessage {
	
	 public UserDTO content;

     public UserStatusMessage(int status, String message, String detail, User content) {
	      super(status, message, detail);
	      this.content = new UserDTO(content);
	}

}
