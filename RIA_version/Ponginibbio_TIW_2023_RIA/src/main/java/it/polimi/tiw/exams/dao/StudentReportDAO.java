package it.polimi.tiw.exams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.exams.beans.Report;
import it.polimi.tiw.exams.beans.Student;
import it.polimi.tiw.exams.beans.StudentReport;

public class StudentReportDAO {
	
	private Connection connection;
	
	public StudentReportDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Object[]> getReportById(int id) throws SQLException {
		List<Object[]> report = new ArrayList<>();
		String performedAction = " finding a student report by id ";
		String query = "SELECT u.id, u.name, u.surname, u.email, s.degree_course, r.grade, timestamp "
				+ "FROM user AS u, student AS s, exam AS e, registration AS r, report , student_report AS sr "
				+ "WHERE u.id = s.id AND s.id = sr.student_id AND sr.report_id = report.id AND report.id = ? AND s.id = r.student_id AND r.course_name = e.course_name AND r.exam_date = e.date AND e.course_name = report.course_name AND e.date = report.exam_date";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				StudentReport sr = new StudentReport();
				Student student = new Student();
				student.setId(resultSet.getInt("id"));
				student.setName(resultSet.getString("name"));
				student.setSurname(resultSet.getString("surname"));
				student.setEmail(resultSet.getString("email"));
				student.setDegree_course(resultSet.getString("degree_course"));
				sr.setStudent(student);
				Report rep = new Report();
				rep.setId(id);
				rep.setTimestamp(resultSet.getTimestamp("timestamp"));
				sr.setReport(rep);
				String grade = resultSet.getString("grade");
				Object[] object = new Object[2];
				object[0] = sr;
				object[1] = grade;
				report.add(object);
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
	
}
