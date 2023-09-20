package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.exams.beans.Course;
import it.polimi.tiw.exams.beans.Exam;
import it.polimi.tiw.exams.beans.Report;

public class ReportDAO {
	
	private Connection connection;
	
	public ReportDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Report getReportById(int id) throws SQLException {
		Report report = null;
		String performedAction = " finding a report by id ";
		String query = "SELECT course_name, exam_date, timestamp "
				+ "FROM report "
				+ "WHERE id = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Course course = new Course();
				course.setName(resultSet.getString("course_name"));
				Exam exam = new Exam();
				exam.setDate(resultSet.getDate("exam_date"));
				exam.setCourse(course);
				report = new Report();
				report.setId(id);
				report.setTimestamp(resultSet.getTimestamp("timestamp"));
				report.setExam(exam);
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
		return report;
	}
	
	public int verbalizeGrades(String name, Date date) throws SQLException {
		int report_id = -1;
		String performedAction = " verbalizing exam grades in the database ";
		String query;
		PreparedStatement preparedStatementReport = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatementStudentReport = null;
		PreparedStatement preparedStatementDeleteRegistrations = null;
		PreparedStatement preparedStatementUpdateRegistrations = null;
		try {
			connection.setAutoCommit(false);
			query = "INSERT INTO report (course_name, exam_date) VALUES(?, ?)";
			preparedStatementReport = connection.prepareStatement(query, new String[]{"id"});
			preparedStatementReport.setString(1, name);
			preparedStatementReport.setDate(2, date);
			preparedStatementReport.executeUpdate();
			resultSet = preparedStatementReport.getGeneratedKeys();
			while (resultSet.next()) {
				report_id = resultSet.getInt(1);
			}
			query ="INSERT INTO student_report (student_id, report_id) "
					+ "(SELECT id, ? "
					+ "FROM student, registration "
					+ "WHERE id = student_id AND (judgment = ? OR judgment = ?))";
			preparedStatementStudentReport = connection.prepareStatement(query);
			preparedStatementStudentReport.setInt(1, report_id);
			preparedStatementStudentReport.setString(2, "published");
			preparedStatementStudentReport.setString(3, "declined");
			preparedStatementStudentReport.executeUpdate();
			query = "DELETE FROM registration WHERE student_id IN ("
					+ "SELECT student_id FROM ("
					+ "SELECT student_id FROM registration WHERE course_name = ? AND exam_date != ? AND (judgment = ? OR judgment = ?) AND student_id IN ("
					+ "SELECT student_id FROM registration "
					+ "WHERE course_name = ? AND exam_date = ? AND (grade != ? OR grade != ? OR grade != ?) AND (judgment = ? OR judgment = ?)"
					+ ")) AS r)";
			preparedStatementDeleteRegistrations = connection.prepareStatement(query);
			preparedStatementDeleteRegistrations.setString(1, name);
			preparedStatementDeleteRegistrations.setDate(2, date);
			preparedStatementDeleteRegistrations.setString(3, "uninsered");
			preparedStatementDeleteRegistrations.setString(4, "insered");
			preparedStatementDeleteRegistrations.setString(5, name);
			preparedStatementDeleteRegistrations.setDate(6, date);
			preparedStatementDeleteRegistrations.setString(7, "absent");
			preparedStatementDeleteRegistrations.setString(8, "failed");
			preparedStatementDeleteRegistrations.setString(9, "tried again");
			preparedStatementDeleteRegistrations.setString(10, "published");
			preparedStatementDeleteRegistrations.setString(11, "declined");
			preparedStatementDeleteRegistrations.executeUpdate();
			query = "UPDATE registration SET judgment = ? WHERE student_id IN ("
					+ "SELECT student_id FROM ("
					+ "SELECT student_id FROM registration WHERE course_name = ? AND exam_date = ? AND (judgment = ? OR judgment = ?)"
					+ ") AS r)";
			preparedStatementUpdateRegistrations = connection.prepareStatement(query);
			preparedStatementUpdateRegistrations.setString(1, "verbalised");
			preparedStatementUpdateRegistrations.setString(2, name);
			preparedStatementUpdateRegistrations.setDate(3, date);
			preparedStatementUpdateRegistrations.setString(4, "published");
			preparedStatementUpdateRegistrations.setString(5, "declined");
			preparedStatementUpdateRegistrations.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException("Error accessing the DB when" + performedAction + "[ " + e.getMessage() + " ]");
		} finally {
			connection.setAutoCommit(true);
			try {
				resultSet.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the result set when" + performedAction + "[ " + e.getMessage() + " ]");
			}
			try {
				preparedStatementReport.close();
				preparedStatementStudentReport.close();
				preparedStatementDeleteRegistrations.close();
				preparedStatementUpdateRegistrations.close();
			} catch (Exception e) {
				throw new SQLException("Error closing the statement when" + performedAction + "[ " + e.getMessage() + " ]");
			}
		}
		return report_id;
	}
	
}
