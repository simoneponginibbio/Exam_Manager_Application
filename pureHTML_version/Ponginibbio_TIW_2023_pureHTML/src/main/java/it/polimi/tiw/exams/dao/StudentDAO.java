package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.exams.beans.Student;

public class StudentDAO {
	
	private Connection connection;
	
	public StudentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Student findStudentByEmail(String email) throws SQLException {
		Student student = null;
		String performedAction = " finding a student by email ";
		String query = "SELECT u.id, u.name, u.surname, u.email, s.degree_course FROM user AS u, student AS s WHERE u.id = s.id AND u.email = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				student = new Student();
				student.setId(resultSet.getInt("id"));
				student.setName(resultSet.getString("name"));
				student.setSurname(resultSet.getString("surname"));
				student.setEmail(resultSet.getString("email"));
				student.setDegree_course(resultSet.getString("degree_course"));
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
		return student;
	}
	
	public void registerStudent(String name, String surname, String email, String password, String degree_course) throws SQLException {
		String performedAction = " registering a new student in the database ";
		String query;
		PreparedStatement preparedStatementUser = null;
		PreparedStatement preparedStatementStudent = null;
		try {
			connection.setAutoCommit(false);
			query = "INSERT INTO user (name, surname, email, password) VALUES(?, ?, ?, ?)";
			preparedStatementUser = connection.prepareStatement(query);
			preparedStatementUser.setString(1, name);
			preparedStatementUser.setString(2, surname);
			preparedStatementUser.setString(3, email);
			preparedStatementUser.setString(4, password);
			preparedStatementUser.executeUpdate();
			query = "INSERT INTO student (id, degree_course) VALUES((SELECT id FROM user WHERE email = ?), ?)";
			preparedStatementStudent = connection.prepareStatement(query);
			preparedStatementStudent.setString(1, email);
			preparedStatementStudent.setString(2, degree_course);
			preparedStatementStudent.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			connection.setAutoCommit(true);
			try {
				preparedStatementUser.close();
				preparedStatementStudent.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
	}
	
}
