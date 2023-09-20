package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.exams.beans.Professor;

public class CourseDAO {
	
	private Connection connection;
	
	public CourseDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Professor getProfessorByCourseName(String name) throws SQLException {
		Professor professor = null;
		String performedAction = " finding the course professor by course name ";
		String query = "SELECT u.id, u.name, u.surname, u.email "
				+ "FROM user AS u, professor AS p, course AS c "
				+ "WHERE u.id = p.id AND p.id = c.professor_id AND c.name = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
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
	
}
