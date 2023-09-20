package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.EnrollmentDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class GoToExamDates
 */
@WebServlet("/GoToExamDates")
public class GoToExamDates extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToExamDates() {
    	super();
    	// TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.templateEngine = TemplateHandler.getEngine(servletContext, ".html");
		this.connection = ConnectionHandler.getConnection(servletContext);
    }
    
    @Override
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Verifies if the given courseName is valid, if not redirects to an error page
		String courseName = request.getParameter("courseName");
		if(courseName == null) {
			forwardToErrorPage(request, response, "Null course name, when accessing course details");
			return;
		}
		// Gets the currentUser and user type from the session
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		String userType = (String)session.getAttribute("userType");
		if (userType.equals("Student")) {
			// Verifies if the current student is enrolled in the courseName course, if not redirects to an error page
			EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
			boolean isEnrolled;
			try {
				isEnrolled = enrollmentDAO.isStudentEnrolledInACourse(currentUser.getId(), courseName);
			} catch (Exception e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;
			}
			if (!isEnrolled) {
				forwardToErrorPage(request, response, "Student isn't enrolled in the selected course");
				return;
			}
			// Gets the dates of the courseName exam in which the current student is registered
			RegistrationDAO registrationDAO = new RegistrationDAO(connection);
			List<Date> dates;
			try {
				dates = registrationDAO.getExamDatesByStudentIdAndCourseName(currentUser.getId(), courseName);
			} catch (Exception e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;	
			}
			request.setAttribute("courseName", courseName);
			request.setAttribute("dates", dates);
			forward(request, response, PathHelper.pathToStudentExamDatesPage);
		}
		if (userType.equals("Professor")) {
			// Verifies if the current professor id is the same as the professor of the courseName course, if not redirects to an error page
			CourseDAO courseDAO = new CourseDAO(connection);
			Professor professor;
			try {
				professor = courseDAO.getProfessorByCourseName(courseName);
			} catch (Exception e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;
			}
			// Verifies if the courseName professor is not null , if not redirects to an error page
			if (professor == null) {
				forwardToErrorPage(request, response, "There isn't a course with the selected name");
				return;
			}
			if (professor.getId() != currentUser.getId()) {
				forwardToErrorPage(request, response, "The current professor is not the professor of the selected course");
				return;
			}
			// Gets the dates of the courseName exam held by the current professor and the count of students registered in them
			RegistrationDAO registrationDAO = new RegistrationDAO(connection);
			List<Object[]> obj;
			try {
				obj = registrationDAO.getDatesAndCountByCourseName(courseName);
			} catch(SQLException e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;
			}
			request.setAttribute("courseName", courseName);
			request.setAttribute("examDatesAndCount", obj);
			forward(request, response, PathHelper.pathToProfessorExamDatesPage);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * Forwards to the ErrorPage
	 * 
	 * @param request
	 * @param response
	 * @param error
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException{
		request.setAttribute("error", error);
		forward(request, response, PathHelper.pathToErrorPage);
		return;
	}
	
	/**
	 * Forwards to the specified path
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
	
}
