package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exams.beans.Course;
import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Student;
import it.polimi.tiw.exams.dao.EnrollmentDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GoToHome")
@MultipartConfig
public class GoToHome extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
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
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(e.getMessage());
				return;
			}
			String json = new Gson().toJson(courses);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
		}
		if (userType.equals("Professor")) {
			Professor currentUser = (Professor)session.getAttribute("currentUser");
			// Gets the courses help by the current professor and the count of students enrolled in them 
			EnrollmentDAO entrollmentDAO = new EnrollmentDAO(connection);
			List<Object[]> obj;
			try {
				obj = entrollmentDAO.getCourseAndCountByProfessorId(currentUser.getId());
			} catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(e.getMessage());
				return;
			}
			String json = new Gson().toJson(obj);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
}
