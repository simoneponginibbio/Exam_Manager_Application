package it.polimi.tiw.exams.packets;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class PacketReport {
	
	private String courseName;
	private Date examDate;
	private SimpleDateFormat timestamp;
	private List<Object[]> report;
	
	public PacketReport(String courseName, Date examDate, SimpleDateFormat timestamp, List<Object[]> report) {
		this.courseName = courseName;
		this.examDate = examDate;
		this.timestamp = timestamp;
		this.report = report;
	}

	public String getCourseName() {
		return courseName;
	}

	public Date getExamDate() {
		return examDate;
	}

	public SimpleDateFormat getTimestamp() {
		return timestamp;
	}

	public List<Object[]> getReport() {
		return report;
	}
	
}
