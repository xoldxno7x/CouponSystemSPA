package coupon.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import facade.AdminFacade;
import facade.CompanyFacade;
import facade.CustomerFacade;

@WebFilter("/api/*")
public class CouponFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
			throws IOException, ServletException {

		String url = ((HttpServletRequest) request).getRequestURI();
		HttpSession session = ((HttpServletRequest) request).getSession(false);

		if (url.toLowerCase().contains("login")) {
			filter.doFilter(request, response);
			return;
		} else if (session == null) {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.getWriter().println("You have been logged out, please log in to complete operation.");
			resp.setContentType(MediaType.APPLICATION_JSON);
			resp.setStatus(500);
			return;
		} else if (url.startsWith("/CouponWeb/api/admin")) {
			AdminFacade af = ((AdminFacade) session.getAttribute("facade"));
			if (af != null) {
				filter.doFilter(request, response);
				return;
			}
		} else if (url.startsWith("/CouponWeb/api/company")) {
			CompanyFacade cf = (CompanyFacade) session.getAttribute("facade");
			if (cf != null) {
				filter.doFilter(request, response);
				return;
			}
		} else if (url.startsWith("/CouponWeb/api/customer")) {
			CustomerFacade custF = ((CustomerFacade) session.getAttribute("facade"));
			if (custF != null) {
				filter.doFilter(request, response);
				return;
			}
		} else {
			HttpServletResponse res = (HttpServletResponse) response;
			res.getWriter().println("You have been logged out, please log in to complete operation.");
			res.setContentType(MediaType.APPLICATION_JSON);
			res.setStatus(500);
			return;
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {

	}

}
