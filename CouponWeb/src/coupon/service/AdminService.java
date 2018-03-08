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

import coupon.JavaBeans.Company;
import coupon.JavaBeans.Customer;
import coupon.notification.Notification;
import coupon.notification.NotificationException;
import facade.AdminFacade;
import facade.ClientType;
import facade.CouponSystem;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminService {

	@GET
	@Path("/login/{username}/{password}")
	public Notification login(@PathParam("username") String username, @PathParam("password") String password,
			@Context HttpServletRequest request) throws NotificationException {
		AdminFacade af = new AdminFacade();
		if (!(username.equals("admin") && password.equals("1234"))) {
			throw new NotificationException("Invalid login inforamtion");
		} else {
			af = ((AdminFacade) CouponSystem.getInstance().login(username, password, ClientType.Admin));
			request.getSession(true).setAttribute("facade", af);
			Notification ms = new Notification("succesful login");
			return ms;
		}
	}

	@POST
	@Path("/createCompany")
	public String createCompany(Company company, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (compNameChecker(company.getCompanyName())) {
				company.setId(compIdGenerator());
				af.createCompany(company);
				return "Company with ID:" + company.getId() + " was created successfuly";
			} else {
				return "Company name is taken";
			}
		} catch (Exception e) {
			throw new NotificationException("Failed creating new company. Invlaid information or company name taken.");
		}
	}

	@DELETE
	@Path("/deleteCompany/{compId}")
	public String deleteCompany(@PathParam("compId") Long compId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			Company comp = new Company(compId);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (!compIdChecker(compId)) {
				af.removeCompany(comp);
				return "Company " + compId + " deleted.";
			} else {
				return "Company with ID" + compId + " was not found";
			}
		} catch (Exception e) {
			throw new NotificationException("Unable to delete company " + compId);
		}
	}

	@PUT
	@Path("/updateCompany")
	public String updateCompany(Company company, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (!compNameChecker(company.getCompanyName())) {
				if (compIdChecker(company.getId())) {
					af.updateCompany(company);
					return "Company " + company.getId() + " updated.";
				} else {
					return "The ID:" + company.getId() + " is taken";
				}
			} else {
				return "No company by that name found";
			}
		} catch (Exception e) {
			throw new NotificationException("Unable to update company " + company.getId());
		}

	}

	@GET
	@Path("/getAllCompanies")
	public Set<Company> getAllCompanies(@Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			return af.getAllCompanies();
		} catch (Exception e) {
			throw new NotificationException(
					"Oops.. something went wrong, please try again later or contact costumer service.");
		}
	}

	@GET
	@Path("/getCompany/{compId}")
	public Company getCompany(@PathParam("compId") Long compId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			return af.getCompany(compId);
		} catch (Exception e) {
			throw new NotificationException(
					"Unable to get company " + compId + ", make sure you are using the correct ID");
		}
	}

	@POST
	@Path("/createCustomer")
	public Long createCustomer(Customer customer, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			customer.setId(custIdGenerator());
			af.createCustomer(customer);
			return customer.getId();
		} catch (Exception e) {
			throw new NotificationException("Failed creting new customer. Customer ID might be taken.");
		}
	}

	@DELETE
	@Path("/deleteCustomer/{custId}")
	public String deleteCustomer(@PathParam("custId") Long custId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			Customer cust = new Customer(custId);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (!custIdChecker(custId)) {
				af.removeCustomer(cust);
				return "Customer " + custId + " deleted";
			} else {
				return "No customer with ID:" + custId + " found";
			}
		} catch (Exception e) {
			throw new NotificationException("Unable to delete customer " + custId);
		}
	}

	@PUT
	@Path("/updateCustomer")
	public String updateCustomer(Customer customer, @Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (!custNameChecker(customer.getCustName())) {
				if (custIdChecker(customer.getId())) {
					af.updateCustomer(customer);
					return "Customer with ID:" + customer.getId() + " updated.";
				} else {
					return "Customer ID:" + customer.getId() + " is taken";
				}
			} else {
				return "No customer by that name found";
			}
		} catch (Exception e) {
			throw new NotificationException("Unable to update customer " + customer.getId());
		}

	}

	@GET
	@Path("/getCustomer/{custId}")
	public Customer getCustomer(@PathParam("custId") Long custId, @Context HttpServletRequest request)
			throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			return af.getCustomer(custId);
		} catch (Exception e) {
			throw new NotificationException("Unable to find customer " + custId);
		}
	}

	@GET
	@Path("/getAllCustomers")
	public Set<Customer> getAllCustomers(@Context HttpServletRequest request) throws NotificationException {
		try {
			HttpSession session = request.getSession(false);
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			return af.getAllCustomers();
		} catch (Exception e) {
			throw new NotificationException("Unable to get customer list");
		}
	}

	@GET
	@Path("/logout")
	public String logout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		session.invalidate();
		return "Adios muchacho!";
	}

	private Long compIdGenerator() {
		Long max = 0L;
		AdminFacade af = new AdminFacade();
		for (Company comp : af.getAllCompanies()) {
			if (comp.getId() > max) {
				max = comp.getId();
			}
		}
		return max + 1;
	}

	private boolean compIdChecker(Long id) {
		AdminFacade af = new AdminFacade();
		boolean flag = true;
		for (Company comp : af.getAllCompanies()) {
			if (comp.getId() == id) {
				flag = false;
			}
		}
		return flag;
	}

	private boolean compNameChecker(String name) {
		AdminFacade af = new AdminFacade();
		boolean flag = true;
		for (Company comp : af.getAllCompanies()) {
			if (comp.getCompanyName().toLowerCase().contains(name.toLowerCase())) {
				flag = false;
			}
		}
		return flag;
	}

	private Long custIdGenerator() {
		Long max = 0L;
		AdminFacade af = new AdminFacade();
		for (Customer cust : af.getAllCustomers()) {
			if (cust.getId() > max) {
				max = cust.getId();
			}
		}
		return max + 1;
	}

	private boolean custIdChecker(Long id) {
		AdminFacade af = new AdminFacade();
		boolean flag = true;
		for (Customer cust : af.getAllCustomers()) {
			if (cust.getId() == id) {
				flag = false;
			}
		}
		return flag;
	}

	private boolean custNameChecker(String name) {
		AdminFacade af = new AdminFacade();
		boolean flag = true;
		for (Customer cust : af.getAllCustomers()) {
			if (cust.getCustName().toLowerCase().contains(name.toLowerCase())) {
				flag = false;
			}
		}
		return flag;
	}
}
