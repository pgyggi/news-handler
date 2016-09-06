package net.joywise.bigdata.news.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.client.RedisClient;

public class RedisMap {
	private static String mapKey = "url_exists_key";
	private static Logger logger = Logger.getLogger(RedisMap.class);

	public static boolean isFetched(String url) {
		return RedisClient.hexists(mapKey, url);
	}

	public static void addUrl(String field) {
		if (field != null && !field.equals("")) {
			RedisClient.hset(mapKey, field, "1");
		}
	}

	public static void addUrlMap(Map<String, String> hash) {
		Map<String, String> syncRedisMap = new HashMap<String, String>();
		if (hash != null && hash.size() > 0) {
			for (String field : hash.keySet()) {
				if (!isFetched(field)) {
					syncRedisMap.put(field, "1");
					logger.info(field + ":is not exists");
				} else {
					int value = Integer.parseInt(RedisClient.hget(mapKey, field));
					syncRedisMap.put(field, (value + 1) + "");
					// System.out.println(url+":is exists");
				}
			}
			RedisClient.hmset(mapKey, syncRedisMap);
			logger.info("sync url to redis: " + syncRedisMap.size());
		}

	}
	public static void main(String[] args) {
		RedisClient.initialPool("192.168.20.14", "6379");
		System.out.println(RedisClient.hexists("url_exists_key", "http://m.weibo.cn/1672241794/4015654728342113"));
	}
}
