package coupon.notification;

public class NotificationException extends Exception {

	private static final long serialVersionUID = 3538782962453020074L;

	public NotificationException() {
	}

	public NotificationException(String notification) {
		super(notification);
	}

	public NotificationException(Throwable cause) {
		super(cause);
	}

	public NotificationException(String notification, Throwable cause) {
		super(notification, cause);
	}
}
