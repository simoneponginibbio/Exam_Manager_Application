package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.exams.beans.Course;
import it.polimi.tiw.exams.beans.Exam;
import it.polimi.tiw.exams.beans.Registration;
import it.polimi.tiw.exams.beans.Student;

public class RegistrationDAO {
	
	private Connection connection;
	
	public RegistrationDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Registration> getStudentRegistrationsByExam(String name, Date date) throws SQLException {
		List<Registration> registrations = new ArrayList<>();
		String performedAction = " finding student registrations by exam ";
		String query = "SELECT u.id, u.name, u.surname, u.email, s.degree_course, grade, judgment "
				+ "FROM user AS u, student AS s, registration "
				+ "WHERE u.id = s.id AND s.id = student_id AND course_name = ? AND exam_date = ? "
				+ "ORDER BY u.id ASC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setDate(2, date);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Registration registration = new Registration();
				Student student = new Student();
				student.setId(resultSet.getInt("id"));
				student.setName(resultSet.getString("name"));
				student.setSurname(resultSet.getString("surname"));
				student.setEmail(resultSet.getString("email"));
				student.setDegree_course(resultSet.getString("degree_course"));
				registration.setStudent(student);
				registration.setGrade(resultSet.getString("grade"));
				registration.setJudgment(resultSet.getString("judgment"));
				registrations.add(registration);
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
		return registrations;
	}
	
	public List<Registration> getRegistrationsWithUniseredGradeByCourseNameAndExamDate(String name, Date date) throws SQLException {
		List<Registration> registrations = new ArrayList<>();
		String performedAction = " finding student registrations by exam ";
		String query = "SELECT u.id, u.name, u.surname, u.email, s.degree_course "
				+ "FROM user AS u, student AS s, registration "
				+ "WHERE u.id = s.id AND s.id = student_id AND course_name = ? AND exam_date = ? AND judgment = ? "
				+ "ORDER BY u.id ASC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setDate(2, date);
			preparedStatement.setString(3, "uninsered");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Registration registration = new Registration();
				Student student = new Student();
				student.setId(resultSet.getInt("id"));
				student.setName(resultSet.getString("name"));
				student.setSurname(resultSet.getString("surname"));
				student.setEmail(resultSet.getString("email"));
				student.setDegree_course(resultSet.getString("degree_course"));
				registration.setStudent(student);
				registration.setGrade(resultSet.getString("grade"));
				registration.setJudgment(resultSet.getString("judgment"));
				registrations.add(registration);
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
		return registrations;
	}
	
	public Registration getRegistrationByStudentIdAndExam(int id, String name, Date date) throws SQLException {
		Registration registration = null;
		String performedAction = " finding the student registration by id and exam ";
		String query = "SELECT u.id, u.name, u.surname, u.email, s.degree_course, course_name, exam_date, grade, judgment "
				+ "FROM user AS u, student AS s, registration, course AS c "
				+ "WHERE u.id = s.id AND s.id = student_id AND course_name = c.name AND u.id = ? AND course_name = ? AND exam_date = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			preparedStatement.setDate(3, date);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				registration = new Registration();
				Student student = new Student();
				student.setId(resultSet.getInt("id"));
				student.setName(resultSet.getString("name"));
				student.setSurname(resultSet.getString("surname"));
				student.setEmail(resultSet.getString("email"));
				student.setDegree_course(resultSet.getString("degree_course"));
				registration.setStudent(student);
				Course course = new Course();
				course.setName(resultSet.getString("course_name"));
				Exam exam = new Exam();
				exam.setDate(resultSet.getDate("exam_date"));
				exam.setCourse(course);
				registration.setExam(exam);
				registration.setGrade(resultSet.getString("grade"));
				registration.setJudgment(resultSet.getString("judgment"));
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
		return registration;
	}
	
	public List<Date> getExamDatesByStudentIdAndCourseName(int id, String name) throws SQLException {
		List<Date> dates = new ArrayList<>();
		String performedAction = " finding exam dates by student id and course name ";
		String query = "SELECT exam_date "
				+ "FROM registration "
				+ "WHERE student_id = ? AND course_name = ? "
				+ "ORDER BY exam_date DESC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Date date = resultSet.getDate("exam_date");
				dates.add(date);
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
		return dates;
	}
	
	public void insertGrade(String grade, int id, String name, Date date) throws SQLException {
		String performedAction = " inserting a new grade in the database ";
		String query = "UPDATE registration SET grade = ?, judgment = ? WHERE student_id = ? AND course_name = ? AND exam_date = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, grade);
			preparedStatement.setString(2, "insered");
			preparedStatement.setInt(3, id);
			preparedStatement.setString(4, name);
			preparedStatement.setDate(5, date);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
	}
	
	public void publishGrades(String name, Date date) throws SQLException {
		String performedAction = " publishing new grades of an exam ";
		String query = "UPDATE registration SET judgment = ? WHERE course_name = ? AND exam_date = ? AND judgment = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "published");
			preparedStatement.setString(2, name);
			preparedStatement.setDate(3, date);
			preparedStatement.setString(4, "insered");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
	}
	
	public void declineGrade(int id, String name, Date date) throws SQLException {
		String performedAction = " rejecting a grade of an exam ";
		String query = "UPDATE registration SET grade = ?, judgment = ? WHERE student_id = ? AND course_name = ? AND exam_date = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "failed");
			preparedStatement.setString(2, "declined");
			preparedStatement.setInt(3, id);
			preparedStatement.setString(4, name);
			preparedStatement.setDate(5, date);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			try {
				preparedStatement.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
	}
	
	public List<Object[]> getDatesAndCountByCourseName(String name) throws SQLException {
		List<Object[]> obj = new ArrayList<>();
		String performedAction = " finding exam dates and counting number of students in those exams ";
		String query = "SELECT exam_date, COUNT(student_id) AS count "
				+ "FROM registration "
				+ "WHERE course_name = ? "
				+ "GROUP BY exam_date "
				+ "ORDER BY exam_date DESC";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Object[] o = new Object[2];
				Date date = resultSet.getDate("exam_date");
				o[0] = date;
				int count = resultSet.getInt("count");
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
	
	public int countPublishableGradesByExam(String name, Date date) throws SQLException {
		int count = 0;
		String performedAction = " counting the number of publishable grades ";
		String query = "SELECT COUNT(student_id) AS count "
				+ "FROM registration "
				+ "WHERE course_name = ? AND exam_date = ? AND judgment = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setDate(2, date);
			preparedStatement.setString(3, "insered");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				count = resultSet.getInt("count");
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
		return count;
	}
	
	public int countVerbalisableGradesByExam(String name, Date date) throws SQLException {
		int count = 0;
		String performedAction = " counting the number of verbalisable grades ";
		String query = "SELECT COUNT(student_id) AS count "
				+ "FROM registration "
				+ "WHERE course_name = ? AND exam_date = ? AND (judgment = ? OR judgment = ?)";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setDate(2, date);
			preparedStatement.setString(3, "published");
			preparedStatement.setString(4, "declined");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				count = resultSet.getInt("count");
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
		return count;
	}
	
	public Boolean isStudentRegisteredInAnExam(int id, String name, Date date) throws SQLException {
		String performedAction = " finding if there is a student registered in an exam with a chosen name and date ";
		String query = "SELECT student_id "
				+ "FROM registration "
				+ "WHERE student_id = ? AND course_name = ? AND exam_date = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			preparedStatement.setDate(3, date);
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
