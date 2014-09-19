package com.amalto.core.objects.universe.ejb;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RevisionItem implements Serializable{
	private String name;
	private String creator;
	private String birthday;
	private List<String> quoterList = new ArrayList<String>();
	private String description ;
	
	public RevisionItem() {
		super();
		birthday = (new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss, SSS"))
				.format(new Date(System.currentTimeMillis()));
	}
	
	public void setName(String nm) {
		name = nm;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCreator(String org) {
		creator = org;
		description = "Created by Universe " + creator;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setBirthday(String timeStamp)
	{
		birthday = timeStamp;
	}
	
	public String getBirthday()
	{
		return birthday;
	}
	
	public void setQuoterList(List<String> list) {
		quoterList = list;
	}
	
	public List<String> getQuoterList() {
		return quoterList;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public String getDescription() {
		return description;
	}
}
