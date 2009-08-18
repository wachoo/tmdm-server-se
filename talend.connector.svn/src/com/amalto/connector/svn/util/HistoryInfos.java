package com.amalto.connector.svn.util;

import java.io.Serializable;

public class HistoryInfos implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String fileName;
	String [] revisions;
	String [] dates;
	String [] authors;
	String [] comments;
	String [] tagNames;

	
	public HistoryInfos() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String[] getAuthors() {
		return authors;
	}


	public void setAuthors(String[] authors) {
		this.authors = authors;
	}


	public String[] getComments() {
		return comments;
	}


	public void setComments(String[] comments) {
		this.comments = comments;
	}


	public String[] getDates() {
		return dates;
	}


	public void setDates(String[] dates) {
		this.dates = dates;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String[] getRevisions() {
		return revisions;
	}


	public void setRevisions(String[] revisions) {
		this.revisions = revisions;
	}


	public String[] getTagNames() {
		return tagNames;
	}


	public void setTagNames(String[] tagNames) {
		this.tagNames = tagNames;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Path "+fileName);
		sb.append("Revision \tDate \tAuthor \tComment\n");
		for (int i=0; i<revisions.length; i++) {
			sb.append(revisions[i]+" |\t"+dates[i]+" |\t"+authors[i]+" |\t"+comments[i]);
		}
		
		
		return sb.toString();
	}
	
	public void display() {
		System.out.println("Path "+fileName);
		System.out.println("Revision \tDate \tAuthor \tComment \tTag");
		for (int i=0; i<revisions.length; i++) {
			System.out.println(revisions[i]+" |\t"+dates[i]+" |\t"+authors[i]+" |\t"+comments[i]+" |\t"+tagNames[i]);
		}
	}
}
