package coupon.service;


import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import coupon.DAO.CompanyDBDAO;
import coupon.DAO.CouponDBDAO;
import coupon.JavaBeans.Coupon;
import coupon.JavaBeans.CouponType;
import coupon.exception.CouponSystemException;
import coupon.notification.Notification;
import coupon.notification.NotificationException;
import facade.ClientType;
import facade.CompanyFacade;
import facade.CouponSystem;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyService {
	
	@GET
	@Path("/login/{username}/{password}")
	public Notification login(@PathParam("username") String username, @PathParam("password") String password,
			@Context HttpServletRequest request) throws NotificationException, CouponSystemException {
		CompanyFacade cf = new CompanyFacade();
		CompanyDBDAO cd = new CompanyDBDAO();
		if (!(cd.Login(username, password))) {
			throw new NotificationException("Invalid login information");
		} else {
			cf = ((CompanyFacade) CouponSystem.getInstance().login(username, password, ClientType.Company));
			request.getSession(true).setAttribute("facade", cf);
			Notification ms = new Notification("succesful login");
			return ms;
		}
	}
	
	@POST
	@Path("/createCoupon")
	public Long createCoupon(Coupon coupon, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			coupon.setId(coupIdGenerator());
			cf.createCoupon(coupon);
			return coupon.getId();
		} catch (Exception e) {
			throw new NotificationException("Creating coupon " +coupon.getId() +" failed, coupon name/ID might be taken.");
		}
	}
	
	@DELETE
	@Path("/deleteCoupon/{coupId}")
	public String deleteCoupon(@PathParam("coupId") Long coupId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			if(coupFinder(coupId)) {
			cf.removeCoupon(new Coupon(coupId));
			return "Coupon " + coupId + " deleted.";
			}else {
				return "Coupon with ID:" +coupId +" was not found";
			}
		} catch (Exception e) {
			throw new NotificationException("Deleting coupon " +coupId +" failed.");
		}
	}
	
	@PUT
	@Path("/updateCoupon")
	public String updateCoupon(Coupon coupon, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			if(coupFinder(coupon.getId())) {
			cf.updateCoupon(coupon);
			return "Coupon " + coupon.getId() + " updated.";
			}else {
				return "Coupon with ID:" +coupon.getId() +" was not found";
			}
		} catch (Exception e) {
			throw new NotificationException("Updating coupon " +coupon.getId() +" failed.");
		}

	}
	
	@GET
	@Path("/getCouponId/{coupId}")
	public Coupon getCouponId(@PathParam("coupId") Long coupId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			return cf.getCoupon(coupId);
		} catch (Exception e) {
			throw new NotificationException("Unable to find coupon " +coupId);
		}
	}
	
	@GET
	@Path("/viewCompCoupons")
	public Set<Coupon> getAllCoupons(@Context HttpServletRequest request) throws NotificationException{
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			return cf.getCompanyCoupon();
		} catch (Exception e) {
			throw new NotificationException("Unable to find coupon list");
		}
	}
	
	@GET
	@Path("/couponsType/{type}")
	public Set<Coupon> getAllPurchasedCouponsType(@PathParam("type") String type,@Context HttpServletRequest request) throws NotificationException{
		try {
			HttpSession session = request.getSession(false);
			CompanyFacade cf = ((CompanyFacade) session.getAttribute("facade"));
			return cf.getCouponByType(CouponType.valueOf(type));
		} catch (Exception e) {
			throw new NotificationException("Unable to find coupon of type " +type);
		}
	}
	
	@GET
	@Path("/logout")
	public String logout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		session.invalidate();
		return "Adios muchacho!";
	}
	
	private Long coupIdGenerator() throws CouponSystemException {
		Long max = 0L;
		CouponDBDAO cd = new CouponDBDAO();
		for (Coupon coup : cd.getAllCoupon()) {
			if (coup.getId() > max) {
				max = coup.getId();
			}
		}
		return max+1;
	}

	private boolean coupFinder(Long id) throws CouponSystemException {
		boolean exists = false;
		CouponDBDAO cd = new CouponDBDAO();
		for (Coupon coup : cd.getAllCoupon()) {
			if (coup.getId() == id) {
				exists = true;
			}
		}
		return exists;
	}
}
