package it.polimi.tiw.exams.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.exams.beans.Professor;
import it.polimi.tiw.exams.beans.Student;
import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.ProfessorDAO;
import it.polimi.tiw.exams.dao.StudentDAO;
import it.polimi.tiw.exams.dao.UserDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

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
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		// Verify if the given argument are null and if so forward to errorPage
		if (email == null || password == null) {
			forwardToErrorPage(request, response, "Null email or password");
			return;
		}
		// Query DB to authenticate user, if user is not present forward to errorPage
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.findUserByEmailAndPassword(email, password);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		// If the user exists, add info to the session and go to home page, otherwise show login page with error message
		if (user == null) {
			request.setAttribute("warning", "Email or password incorrect!");
			forward(request, response, PathHelper.pathToLoginPage);
			return;
		}
		// Query DB to check user type, if user is not present forward to errorPage
		StudentDAO studentDAO = new StudentDAO(connection);
		Student student = null;
		try {
			student = studentDAO.findStudentByEmail(email);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (student != null) {
			request.getSession().setAttribute("currentUser", student);
			request.getSession().setAttribute("userType", "Student");
			response.sendRedirect(getServletContext().getContextPath() + PathHelper.goToHomeServletPath);
			return;
		}
		ProfessorDAO professorDAO = new ProfessorDAO(connection);
		Professor professor = null;
		try {
			professor = professorDAO.findProfessorByEmail(email);
		} catch (Exception e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		if (professor != null) {
			request.getSession().setAttribute("currentUser", professor);
			request.getSession().setAttribute("userType", "Professor");
			response.sendRedirect(getServletContext().getContextPath() + PathHelper.goToHomeServletPath);
			return;
		}
		request.setAttribute("warning", "The user is not a student nor a professor!");
		forward(request, response, PathHelper.goToLoginServletPath);
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
