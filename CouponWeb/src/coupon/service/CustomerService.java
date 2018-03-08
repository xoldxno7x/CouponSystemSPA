package coupon.service;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import coupon.DAO.CustomerDBDAO;
import coupon.JavaBeans.Coupon;
import coupon.JavaBeans.CouponType;
import coupon.exception.CouponSystemException;
import coupon.notification.Notification;
import coupon.notification.NotificationException;
import facade.ClientType;
import facade.CouponSystem;
import facade.CustomerFacade;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerService {

	@GET
	@Path("/login/{username}/{password}")
	public Notification login(@PathParam("username") String username, @PathParam("password") String password,
			@Context HttpServletRequest request) throws CouponSystemException,NotificationException {
		CustomerFacade cf = new CustomerFacade();
		CustomerDBDAO cd = new CustomerDBDAO();
		if (!(cd.Login(username, password))) {
			throw new NotificationException("Invalid login details");
		} else {
			cf = ((CustomerFacade) CouponSystem.getInstance().login(username, password, ClientType.Customer));
			request.getSession(true).setAttribute("facade", cf);
			Notification ms = new Notification("succesful login");
			return ms;
		}
	}

	@GET
	@Path("/viewAllCoupons")
	public Set<Coupon> getAllCoupons(@Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CustomerFacade cf = ((CustomerFacade) session.getAttribute("facade"));
			return cf.viewAllCoupons();
		} catch (Exception e) {
          throw new NotificationException("Unable to view coupon list.");
		}
	}

	@POST
	@Path("/purchaseCoupon")
	public String purchaseCoupon(Coupon coupon, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CustomerFacade cf = ((CustomerFacade) session.getAttribute("facade"));
			int flag = 0;
			String message = null;
			
			for(Coupon coup: cf.viewAllCoupons()) {
				if(coup.getId() == coupon.getId()) {
					flag = 1;
					break;
				}
			}
			
			for(Coupon coup:cf.browsePurchaseHistory()) {
				if(coup.getId() == coupon.getId()) {
					flag = 2;
					break;
				}
			}
			switch(flag) {
			case 1:
				for(Coupon coup: cf.viewAllCoupons()) {
					if(coup.getId() == coupon.getId()) {
						cf.purchaseNewCoupon(coup);
						break;
					}
				}
				message = "Coupon ID:" + coupon.getId() + " purchased";
				break;
			case 2:
				message = "Coupon ID:"+ coupon.getId() +" is already purchased";
				break;
			case 0:
				message = "No coupon by ID:" + coupon.getId();
				break;
			}
			
			return message;
			
		}catch (Exception e) {
			throw new NotificationException("Unable to purchase coupon " +coupon.getId() +" please contact the issuer for additiona information");
		}
	}

	@GET
	@Path("/purchasedCoupons")
	public Set<Coupon> getAllPurchasedCoupons(@Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CustomerFacade cf = ((CustomerFacade) session.getAttribute("facade"));
			return cf.browsePurchaseHistory();
		} catch (Exception e) {
			throw new NotificationException("Unable to view purchased coupons ");
		}
	}

	@GET
	@Path("/purchasedCouponsType/{type}")
	public Set<Coupon> getAllPurchasedCouponsType(@PathParam("type") String type, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CustomerFacade cf = ((CustomerFacade) session.getAttribute("facade"));
			return cf.browsePurchaseHistoryByType(CouponType.valueOf(type));
		} catch (Exception e) {
			throw new NotificationException("Unable to view purchased coupons by type " +type);
 		}
	}

	@GET
	@Path("/purchasedCouponsPrice/{price}")
	public Set<Coupon> getAllPurchasedCouponsPrice(@PathParam("price") Double price,
			@Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CustomerFacade cf = ((CustomerFacade) session.getAttribute("facade"));
			return cf.browsePurchaseHistoryByPrice(price);
		} catch (Exception e) {
			throw new NotificationException("Unable to view purchased coupons by price " +price);
		}
	}

	@GET
	@Path("/logout")
	public String logout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		session.invalidate();
		return "Adios muchacho!";
	}

}
