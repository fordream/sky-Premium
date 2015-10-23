package org.com.atmarkcafe.object;

public class ItemMail {

	private int id;
	
	private String subject;
	
	private String dateTime;
	
	private String content;
	
	private int newsStatus; // 0 is old, i is new

	private int statusOpen;
	
	private int type; // type = 0 is item null
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNewsStatus() {
		return newsStatus;
	}

	public void setNewsStatus(int newsStatus) {
		this.newsStatus = newsStatus;
	}

	public int getStatusOpen() {
		return statusOpen;
	}

	public void setStatusOpen(int statusOpen) {
		this.statusOpen = statusOpen;
	}
}
