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

	@SuppressWarnings("deprecation")
	public static List<News> weiboHandler(String jsonStr) throws UnsupportedEncodingException {
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		JSONArray newsList = jsonObject.getJSONArray("cards");
		List<News> newsLists = new ArrayList<News>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 0; i < newsList.size(); i++) {
			JSONObject JSONObject = newsList.getJSONObject(i);
			News news = new News(JSONObject.getString("scheme"),
					JSONObject.getJSONObject("mblog").getJSONObject("user").getString("screen_name"),
					JSONObject.getJSONObject("mblog").getString("text"), "",
					sdf.format(Date.parse((JSONObject.getJSONObject("mblog").getString("created_at")))), "weibo");
			newsLists.add(news);
		}
		return newsLists;
	}
public static void main(String[] args) throws UnsupportedEncodingException {
	String json="http://m.weibo.cn/container/getIndex?containerid=102803";
	JsonHandler h = new JsonHandler();
	List<News> news = weiboHandler(h.getContent(json));
	for(int i=0;i<news.size();i++){
		System.out.println(news.get(i).toString());
	}
}
}
