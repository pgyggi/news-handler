package net.joywise.bigdata.news;

import java.io.File;

import org.apache.log4j.Logger;

import net.joywise.bigdata.news.client.RedisClient;
import net.joywise.bigdata.news.file.OutputTask;
import net.joywise.bigdata.news.thread.CrawlContent;
import net.joywise.bigdata.news.thread.NewsFetcher;

public class NewsHandler {
	private static Logger logger = Logger.getLogger(NewsHandler.class);

	public static void main(String[] args) throws InterruptedException {
		logger.info("----------------------start to crawl---------------------------------");
		String url = "netease	http://news.163.com/special/0001220O/news_json.js?0.6420350618997459";
		String url1 = "sina	http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=89&spec=&type=&ch=03&k=&offset_page=0&offset_num=0&num=20000&asc=&page=1&r=0.06302655208855867";
		String url2 = "sohu	http://news.sohu.com/_scroll_newslist/{0}/news.inc";
		// clear redis queue key url_fetch
		RedisClient.del("url_fetch");
		// remove result.tsv file
		File f = new File("logs/result.tsv");
		if (f.exists()) {
			f.delete();
		}
		NewsFetcher fetcher = new NewsFetcher();
		CrawlContent content = new CrawlContent();
		OutputTask writeFile = new OutputTask("logs/result.tsv");
		fetcher.addSeed(url);
		fetcher.addSeed(url1);
		fetcher.addSeed(url2);
		Thread fetch = new Thread(fetcher);
		fetch.start();
		Thread.sleep(2000);
		for (int i = 0; i < 5; i++) {
			new Thread(content).start();
			Thread.sleep(1000);
		}

		Thread.sleep(2000);
		new Thread(writeFile).start();

	}
}
