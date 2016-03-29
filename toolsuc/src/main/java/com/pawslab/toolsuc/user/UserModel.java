package com.pawslab.toolsuc.user;

public class UserModel{
	private int UserID;
	private String uri;
	private String login;
	private String pass;
	private String fName;
	private String lName;
	private String fullName;
	private String name;
	private int isGroup;
	private int sync;
	private String email;
	private String organization;
	private String affiliation_code;
	private String city;
	private String country;
	private String how;
	private int isInstructor;
	private int isAnyGroup;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsAnyGroup() {
		return isAnyGroup;
	}

	public void setIsAnyGroup(int isAnyGroup) {
		this.isAnyGroup = isAnyGroup;
	}

	public String getAffiliation_code() {
		return affiliation_code;
	}

	public void setAffiliation_code(String affiliation_code) {
		this.affiliation_code = affiliation_code;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public int getIsGroup() {
		return isGroup;
	}
	
	public void setIsGroup(int isGroup) {
		this.isGroup = isGroup;
	}
	
	public int getSync() {
		return sync;
	}
	
	public void setSync(int sync) {
		this.sync = sync;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getHow() {
		return how;
	}
	
	public void setHow(String how) {
		this.how = how;
	}
	
	public int getIsInstructor() {
		return isInstructor;
	}
	
	public void setIsInstructor(int isInstructor) {
		this.isInstructor = isInstructor;
	}

}
