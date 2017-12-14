package net.joywise.bigdata.news.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.bean.News;
import net.joywise.bigdata.news.client.EsClient;
import net.joywise.bigdata.news.client.RedisClient;
import net.joywise.bigdata.news.file.WriterQueue;
import net.joywise.bigdata.news.handler.HtmlHander;
import net.joywise.bigdata.news.util.DatetimeUtil;

public class CrawlContent implements Runnable {
	private static Logger logger = Logger.getLogger(CrawlContent.class);
	private HtmlHander htmlHandler = new HtmlHander();
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	public void run() {
		while (true) {
			News news = RedisClient.lpop("url_fetch".getBytes());
			try {
				Thread.sleep(1000);
				if (news != null) {
					if (news.getType().equals("sina")) {
						News n = htmlHandler.sinaHandler(news);
						n.setCrawlTime(df.format(new Date()));
						n.setTime(DatetimeUtil.datetime(n.getTime()));
						WriterQueue.getQueue().put(n.toString());
						EsClient.add("diting", "news", null, n.toJson());
					} else if (news.getType().equals("netease")) {
						News n = htmlHandler.neteaseHandler(news);
						n.setCrawlTime(df.format(new Date()));
						n.setTime(DatetimeUtil.datetime(n.getTime()));
						WriterQueue.getQueue().put(n.toString());
						EsClient.add("diting", "news", null, n.toJson());
					} else if (news.getType().equals("sohu")) {
						News n = htmlHandler.sohuHandler(news);
						n.setCrawlTime(df.format(new Date()));
						n.setTime(DatetimeUtil.datetime(n.getTime()));
						WriterQueue.getQueue().put(n.toString());
						EsClient.add("diting", "news", null, n.toJson());
					} else if (news.getType().equals("weibo")) {
						news.setCrawlTime(df.format(new Date()));
						news.setTime(DatetimeUtil.datetime(news.getTime()));
						WriterQueue.getQueue().put(news.toString());
						EsClient.add("diting", "news", null, news.toJson());
					}
				}
			} catch (Exception e) {
				logger.error("CrawlContent Thread Exception:" + e.getMessage());
			}
		}
	}

}
