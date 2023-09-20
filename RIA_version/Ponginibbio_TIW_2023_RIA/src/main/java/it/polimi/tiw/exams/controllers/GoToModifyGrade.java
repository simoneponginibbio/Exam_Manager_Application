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

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToModifyGrade
 */
@WebServlet("/GoToModifyGrade")
@MultipartConfig
public class GoToModifyGrade extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToModifyGrade() {
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
		if (!userType.equals("Professor")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("You are not authorized to access this page");
			return;
		}
		// Verifies if the given studentId, courseName and examDate are valid, if not redirects to an error page
		String studentid = request.getParameter("studentId");
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		if(studentid == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Null student id, when accessing registration details");
			return;
		}
		int studentId;
		try {
			studentId = Integer.parseInt(studentid);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Chosen student id is not a number, when accessing registration details");
			return;
		}
		if(courseName == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Null course name, when accessing registration details");
			return;
		}
		if(examdate == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Null exam date , when accessing registration details");
			return;
		}
		Date examDate = null;
		try {
			examDate = Date.valueOf(examdate);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("\"Chosen exam date is not a date, when accessing registration details\"");
			return;
		}
		// Gets the currentUser from the session
		User currentUser = (User)session.getAttribute("currentUser");
		// Verifies if the current professor id is the same as the professor of the courseName course, if not redirects to an error page
		CourseDAO courseDAO = new CourseDAO(connection);
		Professor professor;
		try {
			professor = courseDAO.getProfessorByCourseName(courseName);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;	
		}
		// Verifies if the courseName professor is not null , if not redirects to an error page
		if (professor == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("There isn't a course with the selected name");
			return;
		}
		if (professor.getId() != currentUser.getId()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("The current professor is not the professor of the selected course");
			return;
		}
		// Gets the student registration for the courseName exam and examDate date
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);
		Registration registration;
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(studentId, courseName, examDate);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;	
		}
		// Verifies if the registration is not null, if not redirects to an error page
		if (registration == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Null exam registration, when accessing registration details");
			return;
		}
		// Verifies if the registration judgment is a valid value, if not redirects to an error page
		if (registration.getJudgment().equals("published") || registration.getJudgment().equals("declined") || registration.getJudgment().equals("verbalised")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("The chosen exam grade can't be modify");
			return;
		}
		String json = new Gson().toJson(registration);
		response.setStatus(HttpServletResponse.SC_OK);
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
