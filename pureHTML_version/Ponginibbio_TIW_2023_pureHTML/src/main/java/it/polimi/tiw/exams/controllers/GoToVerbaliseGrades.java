package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

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
import it.polimi.tiw.exams.beans.Report;
import it.polimi.tiw.exams.beans.StudentReport;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.CourseDAO;
import it.polimi.tiw.exams.dao.ReportDAO;
import it.polimi.tiw.exams.dao.StudentReportDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class GoToVerbaliseGrade
 */
@WebServlet("/GoToVerbaliseGrades")
public class GoToVerbaliseGrades extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToVerbaliseGrades() {
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
		// Verifies if the given courseName and examDate are valid, if not redirects to an error page
		String courseName = request.getParameter("courseName");
		String examdate = request.getParameter("examDate");
		if(courseName == null) {
			forwardToErrorPage(request, response, "Null course name, when accessing exam details");
			return;
		}
		if(examdate == null) {
			forwardToErrorPage(request, response, "Null exam date , when accessing exam details");
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
		// Verbalise all verbalisable grades for the courseName exam and examDate date and gets the report id
		ReportDAO reportDAO = new ReportDAO(connection);
		int report_id;
		try {
			report_id = reportDAO.verbalizeGrades(courseName, examDate);
		} catch(SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		// Gets the report by report id
		Report rep;
		try {
			rep = reportDAO.getReportById(report_id);
		} catch(SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (rep == null) {
			forwardToErrorPage(request, response, "Null report, when accessing report details");
			return;
		}
		StudentReportDAO studentReportDAO = new StudentReportDAO(connection);
		List<Object[]> report;
		try {
			report = studentReportDAO.getReportById(report_id);
		} catch(SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		date.setTimeZone(TimeZone.getTimeZone("UTC+2"));
		request.setAttribute("courseName", courseName);
		request.setAttribute("examDate", examDate);
		request.setAttribute("timestamp", date.format(((StudentReport)report.get(0)[0]).getReport().getTimestamp()));
		request.setAttribute("report", report);
		forward(request, response, PathHelper.pathToReportPage);
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
