package net.joywise.bigdata.news.handler;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.joywise.bigdata.news.bean.News;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonHandler  extends BaseHandler{

	public static List<News> neteaseHandler(String jsonStr) {
		String htmlRep = jsonStr.replace("var data=", "").replace("};", "}");
		JSONObject jsonObject = JSONObject.fromObject(htmlRep);
		JSONArray newsArray = jsonObject.getJSONArray("news");
		List<News> newsList = new ArrayList<News>();
		for (int i = 0; i < newsArray.size(); i++) {
			JSONArray newArray = newsArray.getJSONArray(i);
			for (int j = 0; j < newArray.size(); j++) {
				JSONObject obj = newArray.getJSONObject(j);
				News news = new News(obj.get("l").toString(), obj.get("t").toString(), "", "", obj.get("p").toString(),"netease");
				newsList.add(news);
			}
		}
		return newsList;
	}

	public static List<News> sinaHandler(String jsonStr) {
		String htmlRep = jsonStr.replace("var jsonData = ", "").replace("};", "}");
		JSONObject jsonObject = JSONObject.fromObject(htmlRep);
		JSONArray newsList = jsonObject.getJSONArray("list");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<News> newsLists = new ArrayList<News>();
		for (int i = 0; i < newsList.size(); i++) {
			News news = new News(newsList.getJSONObject(i).get("url").toString(),
								 newsList.getJSONObject(i).get("title").toString(), "", "",
								 sf.format(new Date(Long.valueOf(newsList.getJSONObject(i).get("time").toString() + "000"))),"sina");
			newsLists.add(news);
		}
		return newsLists;
	}

	public static List<News> sohuHandler(String jsonStr) throws UnsupportedEncodingException {
		String htmlRep = jsonStr.replace("var newsJason = ", "");
		JSONObject jsonObject = JSONObject.fromObject(htmlRep);
		JSONArray newsList = jsonObject.getJSONArray("item");
		List<News> newsLists = new ArrayList<News>();
		for (int i = 0; i < newsList.size(); i++) {
			JSONArray newsListArray = newsList.getJSONArray(i);
			for (int j = 0; j < newsListArray.size(); j++) {
				News news = new News(
						newsListArray.getString(2), 
						newsListArray.getString(1), "", "",
						newsListArray.getString(3), "sohu");
				newsLists.add(news);
			}
		}

		return newsLists;
	}
public static void main(String[] args) throws UnsupportedEncodingException {
	String json="http://news.sohu.com/_scroll_newslist/20160902/news.inc";
	JsonHandler h = new JsonHandler();
	System.out.println(sohuHandler(h.getContent(json)).size());
}
}
