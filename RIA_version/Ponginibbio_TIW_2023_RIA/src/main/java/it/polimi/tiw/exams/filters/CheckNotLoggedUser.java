package it.polimi.tiw.exams.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.exams.utils.PathHelper;

/**
 * Servlet Filter implementation class CheckNotLoggedUser
 */
public class CheckNotLoggedUser implements Filter {

	/**
	 * Default constructor.
	 */
	public CheckNotLoggedUser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		if (session != null) {
			Object user = session.getAttribute("currentUser");
			if (user != null) {
				String userType = (String)session.getAttribute("userType");
				if (userType.equals("Student")) {
					res.sendRedirect(request.getServletContext().getContextPath() + PathHelper.pathToStudentHomePage);
					return;
				}
				if (userType.equals("Professor")) {
					res.sendRedirect(request.getServletContext().getContextPath() + PathHelper.pathToProfessorHomePage);
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

}
