package net.joywise.bigdata.news;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.client.RedisClient;
import net.joywise.bigdata.news.file.OutputTask;
import net.joywise.bigdata.news.thread.CrawlContent;
import net.joywise.bigdata.news.thread.NewsFetcher;

public class NewsHandler {
	private static Logger logger = Logger.getLogger(NewsHandler.class);

	public static void main(String[] args) throws InterruptedException {
		//初始化参数
		String redisServer = args[0];
		String redisPort = args[1];
		Integer crawlThread = Integer.parseInt(args[2]);
		Integer second = Integer.parseInt(args[3]);
		logger.info("args:" + args[0] + "," + args[1] + "," + args[2]);
		//初始化Redis连接池
		RedisClient.initialPool(redisServer, redisPort);
		logger.info("----------------------start to crawl---------------------------------");
		String url = "netease	http://news.163.com/special/0001220O/news_json.js?0.6420350618997459";
		String url1 = "sina	http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=89&spec=&type=&ch=03&k=&offset_page=0&offset_num=0&num=20000&asc=&page=1&r=0.06302655208855867";
		String url2 = "sohu	http://news.sohu.com/_scroll_newslist/{0}/news.inc";
//		String url3 = "weibo	http://m.weibo.cn/container/getIndex?containerid=102803";
		NewsFetcher fetcher = new NewsFetcher();
		CrawlContent content = new CrawlContent();
		OutputTask writeFile = new OutputTask("logs/result.tsv");
		fetcher.addSeed(url);
		fetcher.addSeed(url1);
		fetcher.addSeed(url2);
//		fetcher.addSeed(url3);
		fetcher.setInterval(second);
		Thread fetch = new Thread(fetcher);
		fetch.start();
		Thread.sleep(2000);
		for (int i = 0; i < crawlThread; i++) {
			new Thread(content).start();
			Thread.sleep(1000);
		}

		Thread.sleep(2000);
		new Thread(writeFile).start();

	}
}
