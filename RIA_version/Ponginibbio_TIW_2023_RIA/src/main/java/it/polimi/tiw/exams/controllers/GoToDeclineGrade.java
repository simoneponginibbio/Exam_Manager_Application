package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.EnrollmentDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToDeclineGrade
 */
@WebServlet("/GoToDeclineGrade")
@MultipartConfig
public class GoToDeclineGrade extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToDeclineGrade() {
    	super();
    	// TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
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
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("You are not authorized to access this page");
			return;
		}
		// Verifies if the given courseName and examDate are valid, if not redirects to an error page
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		if(courseName == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Null course name, when accessing exam details");
			return;
		}
		if(examdate == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Null exam date, when accessing exam details");
			return;
		}
		Date examDate;
		try {
			examDate = Date.valueOf(examdate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("\"Chosen exam date is not a date, when accessing registration details\"");
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
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (!isEnrolled) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Student isn't enrolled in the selected course");
			return;
		}
		// Verifies if the current student is registered in the exam with the selected name and date, if not redirects to an error page
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);
		boolean isRegistered;
		try {
			isRegistered = registrationDAO.isStudentRegisteredInAnExam(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (!isRegistered) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Student isn't registered in the selected exam");
			return;
		}
		// Gets the current student registration for the courseName exam and examDate date
		Registration registration = new Registration();
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		// Verifies if the current student registration is not null, if not redirects to an error page
		if (registration == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Null registration, when accessing registration details");
			return;
		}
		// Declines the grade of the current student in the courseName exam of examDate date
		try {
			registrationDAO.declineGrade(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		// Gets the current student registration for the courseName exam and examDate date
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(currentUser.getId(), courseName, examDate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		// Verifies if the current student registration is not null, if not redirects to an error page
		if (registration == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Null registration, when accessing registration details");
			return;
		}
		// Verifies if the current student registration judgment and grade were updated correctly, if not redirects to an error page
		if (!(registration.getJudgment().equals("declined") || registration.getGrade().equals("failed"))) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Reject grade operation not working");
			return;
		}
		String json = new Gson().toJson(registration);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
}
