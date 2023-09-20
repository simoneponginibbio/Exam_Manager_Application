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

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToInsertGrade
 */
@WebServlet("/GoToInsertGrade")
@MultipartConfig
public class GoToInsertGrade extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToInsertGrade() {
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
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		String newGrade = request.getParameter("newGrade");
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
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (registration == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Null exam registration, when accessing registration details");
			return;
		}
		if (registration.getJudgment().equals("published") || registration.getJudgment().equals("declined") || registration.getJudgment().equals("verbalised")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("The chosen exam grade can't be modify");
			return;
		}
		// Verifies if the new grade is a valid value, otherwise show modify page with error message
		if (!(newGrade == null || newGrade.equals("absent") || newGrade.equals("rejected") || newGrade.equals("tried again") || newGrade.equals("18") || newGrade.equals("19") || 
				newGrade.equals("20") || newGrade.equals("21") || newGrade.equals("22") || newGrade.equals("23") || newGrade.equals("24") || 
				newGrade.equals("25") || newGrade.equals("26") || newGrade.equals("27") || newGrade.equals("28") || newGrade.equals("29") || 
				newGrade.equals("30") || newGrade.equals("30L"))) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("invalid new insered grade, the grade must be absent, rejected, tried again or between 18 and 30L");
			return;
		}
		try {
			registrationDAO.insertGrade(newGrade, studentId, courseName, examDate);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(studentId, courseName, examDate);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (registration == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("Null exam registration, when accessing registration details");
			return;
		}
		if (!(registration.getGrade().equals(newGrade) && registration.getJudgment().equals("insered"))) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().println("The new grade was not insered");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
}
