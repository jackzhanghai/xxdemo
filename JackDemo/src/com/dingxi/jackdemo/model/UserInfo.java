package com.dingxi.jackdemo.model;

public class UserInfo {

	
	public String ticket;
	public String fkSchoolId;
//	public String fkClassId;
	public String id;
	public UserType roleType;
	
	
	public enum UserType{
	     
	    ROLE_TEACHER("1"),ROLE_PARENT("0");
		String type;
		UserType(String string){
		    type =  string;
		};
		
		@Override
	    public String toString() {
	        // TODO Auto-generated method stub
	        return type;
	    }
	}
	
	

}
