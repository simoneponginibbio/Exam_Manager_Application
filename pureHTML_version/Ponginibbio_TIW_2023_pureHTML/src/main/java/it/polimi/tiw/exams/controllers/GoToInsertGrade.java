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

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.RegistrationDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class GoToInsertGrade
 */
@WebServlet("/GoToInsertGrade")
public class GoToInsertGrade extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

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
			forwardToErrorPage(request, response, "You are not authorized to access this page");
			return;
		}
		// Verifies if the given studentId, courseName and examDate are valid, if not redirects to an error page
		String studentid = request.getParameter("studentId");
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		String newGrade = request.getParameter("newGrade");
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
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (registration == null) {
			forwardToErrorPage(request, response, "Null exam registration, when accessing registration details");
			return;
		}
		if (registration.getJudgment().equals("published") || registration.getJudgment().equals("declined") || registration.getJudgment().equals("verbalised")) {
			forwardToErrorPage(request, response, "The chosen exam grade can't be modify");
			return;
		}
		// Verifies if the new grade is a valid value, otherwise show modify page with error message
		if (!(newGrade == null || newGrade.equals("absent") || newGrade.equals("rejected") || newGrade.equals("tried again") || newGrade.equals("18") || newGrade.equals("19") || 
				newGrade.equals("20") || newGrade.equals("21") || newGrade.equals("22") || newGrade.equals("23") || newGrade.equals("24") || 
				newGrade.equals("25") || newGrade.equals("26") || newGrade.equals("27") || newGrade.equals("28") || newGrade.equals("29") || 
				newGrade.equals("30") || newGrade.equals("30L"))) {
			request.setAttribute("courseName", courseName);
			request.setAttribute("examDate", examDate);
			request.setAttribute("registration", registration);
			request.setAttribute("warning", "invalid new insered grade, the grade must be absent, rejected, tried again or between 18 and 30L");
			forward(request, response, PathHelper.pathToModifyGradePage);
			return;
		}
		try {
			registrationDAO.insertGrade(newGrade, studentId, courseName, examDate);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		try {
			registration = registrationDAO.getRegistrationByStudentIdAndExam(studentId, courseName, examDate);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (registration == null) {
			forwardToErrorPage(request, response, "Null exam registration, when accessing registration details");
			return;
		}
		if (!(registration.getGrade().equals(newGrade) && registration.getJudgment().equals("insered"))) {
			forwardToErrorPage(request, response, "The new grade was not insered");
			return;
		}
		response.sendRedirect(getServletContext().getContextPath() + PathHelper.goToHomeServletPath);
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
