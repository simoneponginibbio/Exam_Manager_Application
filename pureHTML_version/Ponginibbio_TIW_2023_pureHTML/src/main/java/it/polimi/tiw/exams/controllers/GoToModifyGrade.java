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

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class GoToModifyGrade
 */
@WebServlet("/GoToModifyGrade")
public class GoToModifyGrade extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
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
		if (!userType.equals("Professor")) {
			forwardToErrorPage(request, response, "You are not authorized to access this page");
			return;
		}
		// Verifies if the given studentId, courseName and examDate are valid, if not redirects to an error page
		String studentid = request.getParameter("studentId");
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		if(studentid == null) {
			forwardToErrorPage(request, response, "Null student id, when accessing registration details");
			return;
		}
		int studentId;
		try {
			studentId = Integer.parseInt(studentid);
		} catch (NumberFormatException e) {
			forwardToErrorPage(request, response, "Chosen student id is not a number, when accessing registration details");
			return;
		}
		if(courseName == null) {
			forwardToErrorPage(request, response, "Null course name, when accessing registration details");
			return;
		}
		if(examdate == null) {
			forwardToErrorPage(request, response, "Null exam date , when accessing registration details");
			return;
		}
		Date examDate = null;
		try {
			examDate = Date.valueOf(examdate);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
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
		// Gets the student registration for the courseName exam and examDate date
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);
		Registration registration;
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(studentId, courseName, examDate);
		} catch(SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		// Verifies if the registration is not null, if not redirects to an error page
		if (registration == null) {
			forwardToErrorPage(request, response, "Null exam registration, when accessing registration details");
			return;
		}
		// Verifies if the registration judgment is a valid value, if not redirects to an error page
		if (registration.getJudgment().equals("published") || registration.getJudgment().equals("declined") || registration.getJudgment().equals("verbalised")) {
			forwardToErrorPage(request, response, "The chosen exam grade can't be modify");
			return;
		}
		request.setAttribute("courseName", courseName);
		request.setAttribute("examDate", examDate);
		request.setAttribute("registration", registration);
		forward(request, response, PathHelper.pathToModifyGradePage);
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
