package it.polimi.tiw.exams.packets;

public class PacketUser {
	
	private int id;
	private String name;
	private String surname;
	private String userType;
	
	public PacketUser(int id, String name, String surname, String userType) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.userType = userType;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getUserType() {
		return userType;
	}
	
}
