package it.polimi.tiw.exams.packets;

import java.sql.Date;
import java.util.List;

public class PacketExam {
	
	private String courseName;
	private List<Date> examDates;
	private List<Object[]> examDatesAndCount;
	
	public PacketExam(String courseName, List<Date> examDates, List<Object[]> examDatesAndCount) {
		this.courseName = courseName;
		this.examDates = examDates;
		this.examDatesAndCount = examDatesAndCount;
	}

	public String getCourseName() {
		return courseName;
	}
	
	public List<Date> getExamDates() {
		return examDates;
	}


	public List<Object[]> getExamDatesAndCount() {
		return examDatesAndCount;
	}
	
}
