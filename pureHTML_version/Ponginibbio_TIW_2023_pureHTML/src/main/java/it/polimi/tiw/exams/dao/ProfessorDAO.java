package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.exams.beans.Professor;

public class ProfessorDAO {
	
	private Connection connection;
	
	public ProfessorDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Professor findProfessorByEmail(String email) throws SQLException {
		Professor professor = null;
		String performedAction = " finding a professor by email";
		String query = "SELECT u.id, u.name, u.surname, u.email FROM user AS u, professor AS p WHERE u.id = p.id AND u.email = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				professor = new Professor();
				professor.setId(resultSet.getInt("id"));
				professor.setName(resultSet.getString("name"));
				professor.setSurname(resultSet.getString("surname"));
				professor.setEmail(resultSet.getString("email"));
			}
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction + "[ " + e.getMessage() + " ]");
			}
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
		return professor;
	}
	
	public void registerProfessor(String name, String surname, String email, String password) throws SQLException {
		String performedAction = " registering a new professor in the database ";
		String query;
		PreparedStatement preparedStatementUser = null;
		PreparedStatement preparedStatementProfessor = null;
		try {
			connection.setAutoCommit(false);
			query = "INSERT INTO user (name, surname, email, password) VALUES(?, ?, ?, ?)";
			preparedStatementUser = connection.prepareStatement(query);
			preparedStatementUser.setString(1, name);
			preparedStatementUser.setString(2, surname);
			preparedStatementUser.setString(3, email);
			preparedStatementUser.setString(4, password);
			preparedStatementUser.executeUpdate();
			query = "INSERT INTO professor (id) VALUES((SELECT id FROM user WHERE email = ?))";
			preparedStatementProfessor = connection.prepareStatement(query);
			preparedStatementProfessor.setString(1, email);
			preparedStatementProfessor.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			connection.setAutoCommit(true);
			try {
				preparedStatementUser.close();
				preparedStatementProfessor.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
	}
	
}
