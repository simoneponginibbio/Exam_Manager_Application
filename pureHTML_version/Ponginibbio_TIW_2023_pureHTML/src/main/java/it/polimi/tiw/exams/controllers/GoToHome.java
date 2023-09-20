package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
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

import it.polimi.tiw.exams.beans.Course;
import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Student;
import it.polimi.tiw.exams.dao.EnrollmentDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHome() {
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
		// Gets the current user type from the session
		HttpSession session = request.getSession(false);
		String userType = (String)session.getAttribute("userType");
		if (userType.equals("Student")) {
			Student currentUser = (Student)session.getAttribute("currentUser");
			// Gets the courses in which the current student is enrolled
			EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
			List<Course> courses;
			try {
				courses = enrollmentDAO.getCoursesByStudentId(currentUser.getId());
			} catch(SQLException e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;
			}
			request.setAttribute("courses", courses);
			forward(request, response, PathHelper.pathToStudentHomePage);
		}
		if (userType.equals("Professor")) {
			Professor currentUser = (Professor)session.getAttribute("currentUser");
			// Gets the courses help by the current professor and the count of students enrolled in them 
			EnrollmentDAO entrollmentDAO = new EnrollmentDAO(connection);
			List<Object[]> obj;
			try {
				obj = entrollmentDAO.getCourseAndCountByProfessorId(currentUser.getId());
			} catch(SQLException e) {
				forwardToErrorPage(request, response, e.getMessage());
				return;
			}
			request.setAttribute("coursesNamesAndCount", obj);
			forward(request, response, PathHelper.pathToProfessorHomePage);
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
