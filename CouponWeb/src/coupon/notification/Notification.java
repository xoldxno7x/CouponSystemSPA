package coupon.notification;

public class Notification {
	
	String message;
	
	public Notification() {
	}

	public Notification(String message) {
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
		return "Notification [message=" + message + "]";
	}

	
	

}
