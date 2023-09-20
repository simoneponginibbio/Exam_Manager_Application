package it.polimi.tiw.exams.beans;

import java.sql.Date;

public class Exam {
	
	private Course course;
	private Date date;

	public Course getCourse() {
		return course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
}
