package org.com.atmarkcafe.object;

import org.json.JSONArray;

public class NewsObject {

	private int id;
	private String title;
	private String content;
	private String time;
	private JSONArray category;
	private int type;
	private int newsStatus;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public JSONArray getCategory() {
		return category;
	}

	public void setCategory(JSONArray category) {
		this.category = category;
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

	
}
