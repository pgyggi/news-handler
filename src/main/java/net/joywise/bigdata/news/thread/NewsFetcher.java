package net.joywise.bigdata.news.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.bean.News;
import net.joywise.bigdata.news.client.HttpClient;
import net.joywise.bigdata.news.client.RedisClient;
import net.joywise.bigdata.news.handler.JsonHandler;
import net.joywise.bigdata.news.handler.UrlMap;

public class NewsFetcher implements Runnable {

	private List<String> urlSeeds = new ArrayList<String>();
	private HttpClient client = new HttpClient();
	private UrlMap map = UrlMap.getInstance();
	private static Logger logger = Logger.getLogger(NewsFetcher.class);
	private final String NETEASE = "netease";
	private final String SINA = "sina";
	private final String CONFIG_SPLIT = "\t";

	public void run() {
		while (true) {
			// fetch news from website
			List<News> news = new ArrayList<News>();
			try {
				for (String u : urlSeeds) {
					String newsType[] = u.split(CONFIG_SPLIT);
					if (newsType[0].equals(NETEASE)) {
						String neteaseContent = client.getContent(newsType[1]);
						List<News> newsNetease = JsonHandler.neteaseHandler(neteaseContent);
						news.addAll(newsNetease);
					} else if (newsType[0].equals(SINA)) {
						String sinaContent = client.getContent(newsType[1]);
						List<News> newsSina = JsonHandler.sinaHandler(sinaContent);
						news.addAll(newsSina);
					}
					logger.info("news size:" + news.size());
					for (News n : news) {
						if (!map.isFetched(n.getUrl())) {
							RedisClient.rpush("url_fetch", n);
						}
						map.addUrl(n.getUrl());
					}
				}
				logger.info("request news thread end,after 2 min repeat!");
				logger.info("Map size:" + map.size());
				Thread.sleep(1000 * 120);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
}
