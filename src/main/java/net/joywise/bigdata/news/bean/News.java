package net.joywise.bigdata.news.bean;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;  
public class News implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -110204447482296248L;
	private String url;
	private String title;
	private String body;
	private String source;
	private String time;
	private String type;
	private static Logger logger = Logger.getLogger(News.class);

	public News() {
		this.url = "";
		this.title = "";
		this.body = "";
		this.source = "";
		this.time = "";
		this.type="";
	}

	public News(String url, String title, String body, String source, String time,String type) {
		this.url = url;
		this.title = title;
		this.body = body;
		this.source = source;
		this.time = time;
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTime() {
		return time;
	}
	
	public String getType() {
		return type;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String charReplace(String charSequence) {
		return charSequence.replaceAll("<[^>]+>", "").replaceAll("\\s{1,}", " ").replaceAll("\t", "")
				.replaceAll("\n", "").trim();
	}

	@Override
	public String toString() {
		return url + "\t" + charReplace(title) + "\t" + charReplace(body) + "\t" + charReplace(source) + "\t"
				+ charReplace(time) + "\t" + type;
	}
	public String toJson() {
		 String jsonString = JSON.toJSONString(this);
		 return jsonString;
	}

}
