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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exams.beans.User;
import it.polimi.tiw.exams.dao.ProfessorDAO;
import it.polimi.tiw.exams.dao.UserDAO;
import it.polimi.tiw.exams.utils.ConnectionHandler;
import it.polimi.tiw.exams.utils.PathHelper;
import it.polimi.tiw.exams.utils.TemplateHandler;

/**
 * Servlet implementation class RegisterProfessor
 */
@WebServlet("/RegisterProfessor")
public class RegisterProfessor extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterProfessor() {
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
		// Tries to register the professor, if the function returns null ends
		if (registerProfessor(request, response) == null) {
			return;
		}
		// Once the professor is registered is redirected to the LoginPage
		response.sendRedirect(getServletContext().getContextPath() + PathHelper.goToLoginServletPath);
	}

	/**
	 * Verifies the input and if it is correct creates the professor in the DB. If the operation is successful returns 0, else returns null
	 * 
	 * @param request
	 * @param response
	 * @return Student
	 * @throws ServletException
	 * @throws IOException
	 */
	private Integer registerProfessor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Gets the parameters of the request and verifies if they are in the correct format (length and syntax)
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeat_pwd = request.getParameter("repeat_pwd");
		// Verifies if all parameters are not null
		if (name == null || surname == null || email == null || password == null || repeat_pwd == null) {
			forwardToErrorPage(request, response, "Register module missing some data");
			return null;
		}
		// Checks if the inserted string (NAME) is of the correct length (1-45)
		if (name.length() <= 0 || name.length() > 45) {
			request.setAttribute("warning", "Chosen name invalid (a valid name has more than one character and less than 45)!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		// Checks if the inserted string (SURNAME) is of the correct length (1-45)
		if (surname.length() <= 0 || surname.length() > 45) {
			request.setAttribute("warning", "Chosen surname invalid (a valid surname has more than one character and less than 45)!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		// Checks if the inserted string (EMAIL) matches with an e-mail syntax (RFC5322 e-mail) by using a RegEx
		String emailRegEx = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
		// If the string does not match the the user is redirected to the register page with an error message
		if (!email.matches(emailRegEx)) {
			request.setAttribute("warning", "Chosen email invalid!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		// Checks if the inserted strings (PASSWORD and REPEAT_PWD) are of the correct length (1-45) and equal
		if (password.length() <= 0 || password.length() > 45) {
			request.setAttribute("warning", "Chosen password invalid (a valid password has more than one character and less than 45)!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		if (!password.equals(repeat_pwd)) {
			request.setAttribute("warning", "Password and repeat password field not equal!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		// Checks that the submitted user for the registration has not the same email of another user in the DB
		// If another user with the same email is present redirects to the to the RegisterPage with a warning and error message
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.findUserByEmail(email);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return null;	
		}
		if (user != null) {
			request.setAttribute("warning", "Chosen email already in use!");
			forward(request, response, PathHelper.pathToRegisterProfessorPage);
			return null;
		}
		// Creates the submitted professor in the DB. If error are generated everything is forwarded to an errorPage
		ProfessorDAO professorDAO = new ProfessorDAO(connection);
		try {
			professorDAO.registerProfessor(name, surname, email, password);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return null;	
		}
		// Gets the created user in the DB to verify it has been correctly created in the DB, if an Exception is raised forwards to the ErrorPage
		try {
			user = userDAO.findUserByEmail(email);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return null;
		}
		if (user == null) {
			forwardToErrorPage(request, response, "Error: professor not correctly created");
			return null;
		}
		return 0;
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
