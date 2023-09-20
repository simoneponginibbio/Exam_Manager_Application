package it.polimi.tiw.exams.beans;

public class Registration {
	
	private Exam exam;
	private Student student;
	private String grade;
	private String judgment;
	
	public Exam getExam() {
		return exam;
	}
	
	public void setExam(Exam exam) {
		this.exam = exam;
	}
	
	public Student getStudent() {
		return student;
	}
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getJudgment() {
		return judgment;
	}
	
	public void setJudgment(String judgment) {
		this.judgment = judgment;
	}
	
}
