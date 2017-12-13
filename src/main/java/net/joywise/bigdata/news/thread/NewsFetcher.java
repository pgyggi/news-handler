package net.joywise.bigdata.news.thread;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.bean.News;
import net.joywise.bigdata.news.client.HttpClient;
import net.joywise.bigdata.news.client.RedisClient;
import net.joywise.bigdata.news.handler.JsonHandler;
import net.joywise.bigdata.news.handler.RedisMap;
import net.sf.json.JSONException;

public class NewsFetcher implements Runnable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 532366801991477320L;
	private List<String> urlSeeds = new ArrayList<String>();
	private HttpClient client = new HttpClient();
	private static Logger logger = Logger.getLogger(NewsFetcher.class);
	private final String NETEASE = "netease";
	private final String SINA = "sina";
	private final String SOHU = "sohu";
	private final String WEIBO = "weibo";
	private final String CONFIG_SPLIT = "\t";
	private int second = 0;

	public void run() {
		while (true) {
			// fetch news from website
			List<News> news = new ArrayList<News>();
			try {
				for (String u : urlSeeds) {
					String newsType[] = u.split(CONFIG_SPLIT);
					if (newsType[0].equals(NETEASE)) {
						String url = formatSeedUrl(newsType[1], newsType[0]);
						String neteaseContent = client.getContent(url, "gbk");
						List<News> newsNetease = JsonHandler.neteaseHandler(neteaseContent);
						news.addAll(newsNetease);
						logger.info(NETEASE + " count: " + newsNetease.size());
					} else if (newsType[0].equals(SINA)) {
						String url = formatSeedUrl(newsType[1], newsType[0]);
						String sinaContent = client.getContent(url, "gbk");
						List<News> newsSina = JsonHandler.sinaHandler(sinaContent);
						news.addAll(newsSina);
						logger.info(SINA + " count: " + newsSina.size());
					} else if (newsType[0].equals(SOHU)) {
						String url = formatSeedUrl(newsType[1], newsType[0]);
						String sohuContent = client.getContent(url, "utf-8");
						List<News> newsSohu = JsonHandler.sohuHandler(sohuContent);
						news.addAll(newsSohu);
						logger.info(SOHU + " count: " + newsSohu.size());
					} else if (newsType[0].equals(WEIBO)) {
						String url = formatSeedUrl(newsType[1], newsType[0]);
						String weiboContent = client.getContent(url, "utf-8");
						List<News> newsWeibo = JsonHandler.weiboHandler(weiboContent);
						news.addAll(newsWeibo);
						logger.info(WEIBO + " count: " + newsWeibo.size());
					}
				}
				logger.info("new fetch news size:" + news.size());
				for (News n : news) {
					if (!RedisMap.isFetched(n.getUrl())) {
						RedisClient.rpush("url_fetch", n);
						RedisMap.addUrl(n.getUrl());
						logger.info(n.getUrl() + ":" + "is not exists!");
					}
				}
				logger.info("request news thread end,after " + second + " seconds repeat!");
				Thread.sleep(1000 * second);
			} catch (InterruptedException e) {
				logger.error("NewsFetcher Thread Exception:" + e.getMessage());
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				logger.error("NewsFetcher Thread Exception:" + e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				logger.error("NewsFetcher Thread Exception:" + e.getMessage());
			} catch (Exception e) {
				logger.error("NewsFetcher Thread Exception:" + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public void addSeed(String url) {
		urlSeeds.add(url);
	}

	public List<String> getSeeds() {
		return urlSeeds;
	}
	
	public void setInterval(int second) {
		this.second = second;
	}

	private String formatSeedUrl(String url, String type) {
		if (type.equals(SINA)) {
			return url;
		}
		if (type.equals(NETEASE)) {
			return url;
		}
		if (type.equals(SOHU)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return url.replace("{0}", sdf.format(new Date()));
		}
		if (type.equals(WEIBO)) {
			return url;
		}
		return url;
	}
}
