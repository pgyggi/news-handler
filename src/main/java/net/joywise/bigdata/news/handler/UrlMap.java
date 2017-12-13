package net.joywise.bigdata.news.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * use {@link RedisMap}
 * @author Administrator
 *
 */
@Deprecated
public class UrlMap {
	private static Map<String, Integer> urls;
	private static UrlMap urlMap = null;
	private static Logger logger = Logger.getLogger(UrlMap.class);

	private UrlMap() {
	}

	public boolean isFetched(String url) {
		return urls.containsKey(url);
	}

	public int size() {
		return urls.size();
	}

	private static void initMap() {
		if (urls == null) {
			urls = new HashMap<String, Integer>();
		}
	}

	public static UrlMap getInstance() {
		if (urlMap == null) {
			urlMap = new UrlMap();
			initMap();
		}
		return urlMap;
	}

	public void addUrl(String url) {
		if (!urls.containsKey(url)) {
			urls.put(url, 1);
			logger.info(url+":is not exists");
		} else {
			urls.put(url, urls.get(url) + 1);
//			System.out.println(url+":is exists");
		}
	}
}
