package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.EnrollmentDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class GoToExamOutcome
 */
@WebServlet("/GoToExamOutcome")
public class GoToExamOutcome extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToExamOutcome() {
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
		// Gets the user type from the session
		HttpSession session = request.getSession(false);
		String userType = (String)session.getAttribute("userType");
		// Verifies if the user type is correct, if not redirects to an error page
		if (!userType.equals("Student")) {
			forwardToErrorPage(request, response, "You are not authorized to access this page");
			return;
		}
		// Verifies if the given courseName and examDate are valid, if not redirects to an error page
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		if(courseName == null) {
			forwardToErrorPage(request, response, "Null course name, when accessing exam details");
			return;
		}
		if(examdate == null) {
			forwardToErrorPage(request, response, "Null exam date, when accessing exam details");
			return;
		}
		Date examDate;
		try {
			examDate = Date.valueOf(examdate);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;	
		}
		// Gets the currentUser from the session
		User currentUser = (User)session.getAttribute("currentUser");
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
		// Verifies if the current student is registered in the exam with the selected name and date, if not redirects to an error page
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);
		boolean isRegistered;
		try {
			isRegistered = registrationDAO.isStudentRegisteredInAnExam(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (!isRegistered) {
			forwardToErrorPage(request, response, "Student isn't registered in the selected exam");
			return;
		}
		// Gets the current student registration for the courseName exam and examDate date
		Registration registration = new Registration();
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;	
		}
		// Verifies if the current student registration is not null, if not redirects to an error page
		if (registration == null) {
			forwardToErrorPage(request, response, "Null registration, when accessing registration details");
			return;
		}
		// Verifies if the registration grade is declinable by the student
		if (registration.getGrade() == null || registration.getGrade().equals("absent") || registration.getGrade().equals("failed") ||
				registration.getGrade().equals("tried again") || registration.getGrade().equals("30L") || registration.getJudgment().equals("uninsered") || 
				registration.getJudgment().equals("insered") || registration.getJudgment().equals("declined") || 
				registration.getJudgment().equals("verbalised")) {
			request.setAttribute("declinable", false);
		} else {
			request.setAttribute("declinable", true);
		}
		request.setAttribute("courseName", courseName);
		request.setAttribute("examDate", examdate);
		request.setAttribute("registration", registration);
		forward(request, response, PathHelper.pathToExamOutcomePage);
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
