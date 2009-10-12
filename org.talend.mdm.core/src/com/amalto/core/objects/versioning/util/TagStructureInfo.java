package com.amalto.core.objects.versioning.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TagStructureInfo implements Serializable {
	
	private static final long serialVersionUID = 7116129388850864283L;
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	String tagName;
	Date  lastDate;
	String  lastAuthor;
	String  lastComment;
	List<String> clusters;
	
	

	public TagStructureInfo(String tagName,String lastAuthor, 
			Date lastDate,String lastComment) {
		super();
		this.lastAuthor = lastAuthor;
		this.lastComment = lastComment;
		this.lastDate = lastDate;
		this.tagName = tagName;
		this.clusters=new ArrayList<String>();
	}
	
	

	public String getTagName() {
		return tagName;
	}



	public Date getLastDate() {
		return lastDate;
	}


	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}


	public String getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(String lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

	public String getLastComment() {
		return lastComment;
	}

	public void setLastComment(String lastComment) {
		this.lastComment = lastComment;
	}


	public List<String> getClusters() {
		return clusters;
	}

	public void addCluster(String cluster) {
		
		if(this.clusters!=null){
			if(this.clusters.contains(cluster)){
				this.clusters.remove(cluster);
			}
			clusters.add(cluster);
		}
	}
	
	public void display() {
		System.out.println("=====================");
		System.out.println("Tag "+tagName);
		System.out.println(sdf.format(lastDate)+" ;\t"+lastAuthor+" ;\t"+lastComment);
		String clustersStr="|";
		for (int i = 0; i < clusters.size(); i++) {
			clustersStr+=(clusters.get(i)+"|");
		}
		System.out.println("clusters:"+clustersStr);
	}
	
}
