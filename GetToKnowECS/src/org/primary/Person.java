package org.primary;

public class Person {
	
	private String emailId;
	private String name;
	private String imageUrl;
	
	public Person(String e,String n,String i){
		emailId=e;
		name=n;
		imageUrl=i;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getName() {
		return name;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	
}
