package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
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
import it.polimi.tiw.exams.beans.Student;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.ProfessorDAO;
import it.polimi.tiw.exams.dao.StudentDAO;
import it.polimi.tiw.exams.dao.UserDAO;
import it.polimi.tiw.exams.packets.PacketUser;
import it.polimi.tiw.exams.utils.ConnectionHandler;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
@MultipartConfig
public class Login extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		// Verify if the given argument are null and if so set to BAD_REQUEST Page
		if (email == null || password == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Missing parameter");
			return;
		}
		// Query DB to authenticate user, if user is not present set to an INTERNAL_SERVER_ERROR
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.findUserByEmailAndPassword(email, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		// If the user exists, add info to the session and go to home page, otherwise set to UNAUTHORIZED
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Email or password are incorrect");
			return;
		}
		
		// Query DB to check user type, if user is not present set to an UNAUTHORIZED
		StudentDAO studentDAO = new StudentDAO(connection);
		Student student = null;
		try {
			student = studentDAO.findStudentByEmail(email);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (student != null) {
			HttpSession session = request.getSession();
			session.setAttribute("currentUser", student);
			session.setAttribute("userType", "Student");
			String json = new Gson().toJson(new PacketUser(student.getId(), student.getName(), student.getSurname(), "Student"));
			response.setStatus(HttpServletResponse.SC_OK);	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
			return;
		}
		ProfessorDAO professorDAO = new ProfessorDAO(connection);
		Professor professor = null;
		try {
			professor = professorDAO.findProfessorByEmail(email);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		if (professor != null) {
			HttpSession session = request.getSession();
			session.setAttribute("currentUser", professor);
			session.setAttribute("userType", "Professor");
			String json = new Gson().toJson(new PacketUser(professor.getId(), professor.getName(), professor.getSurname(), "Professor"));
			response.setStatus(HttpServletResponse.SC_OK);	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
			return;
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().println("The user is not a student nor a professor!");
	}
	
}
