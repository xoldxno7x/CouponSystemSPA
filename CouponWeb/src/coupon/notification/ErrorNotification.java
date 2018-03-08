package coupon.notification;

public class ErrorNotification {
	
	String message;
	
	public ErrorNotification() {
	}

	public ErrorNotification(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ErrorNotification [message=" + message + "]";
	}

	
	

}
