package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.exams.beans.Course;
import it.polimi.tiw.exams.beans.Professor;

public class EnrollmentDAO {
	
	private Connection connection;
	
	public EnrollmentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Course> getCoursesByStudentId(int id) throws SQLException {
		List<Course> courses = new ArrayList<>();
		String performedAction = " finding courses by student id ";
		String query = "SELECT c.name, u.name AS professor_name, u.surname, u.email "
				+ "FROM course AS c, enrollment AS e, professor AS p, user AS u "
				+ "WHERE e.course_name = c.name AND c.professor_id = p.id AND p.id = u.id AND e.student_id = ? "
				+ "ORDER BY c.name DESC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Course course = new Course();
				course.setName(resultSet.getString("name"));
				Professor professor = new Professor();
				professor.setName(resultSet.getString("professor_name"));
				professor.setSurname(resultSet.getString("surname"));
				professor.setEmail(resultSet.getString("email"));
				course.setProfessor(professor);
				courses.add(course);
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
		return courses;
	}
	
	public List<Object[]> getCourseAndCountByProfessorId(int id) throws SQLException {
		List<Object[]> obj = new ArrayList<>();
		String performedAction = " finding courses and counting number of students by professor id ";
		String query = "SELECT name, COUNT(student_id) AS count "
				+ "FROM enrollment, course "
				+ "WHERE professor_id = ? AND course_name = name "
				+ "GROUP BY name "
				+ "ORDER BY name DESC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Course course = new Course();
				course.setName(resultSet.getString("name"));
				int count = resultSet.getInt("count");
				Object[] o = new Object[2];
				o[0] = course;
				o[1] = count;
				obj.add(o);
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
		return obj;
	}
	
	public Boolean isStudentEnrolledInACourse(int id, String name) throws SQLException {
		String performedAction = " finding if there is a student enrolled in a course with a chosen name ";
		String query = "SELECT student_id "
				+ "FROM enrollment "
				+ "WHERE student_id = ? AND course_name = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return true;
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
		return false;
	}
	
}
