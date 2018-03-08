package coupon.notification;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotificationExceptionHandler implements ExceptionMapper<Exception>{

	@Override
	public Response toResponse(Exception e) {
		return Response.serverError()
			   .entity(new ErrorNotification(e.getMessage()))
			   .build();
	}

}
