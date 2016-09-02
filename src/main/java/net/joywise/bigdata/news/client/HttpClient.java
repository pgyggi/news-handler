package net.joywise.bigdata.news.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

public class HttpClient {
	private static Logger logger = Logger.getLogger(HttpClient.class);

	public String getContent(String url,String charset) {
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
//		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "wechat_count1.0");
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 joywise.net");
		HttpConnectionManager httpConnManager = httpClient.getHttpConnectionManager();
		BufferedReader bufferedReader;
		StringBuilder entityStringBuilder = new StringBuilder();
		if (httpConnManager != null) {
			HttpConnectionManagerParams mgrParams = new HttpConnectionManagerParams();
			mgrParams.setSoTimeout(10000);
			mgrParams.setTcpNoDelay(true);
			mgrParams.setConnectionTimeout(10000);
			mgrParams.setLinger(0);
			mgrParams.setStaleCheckingEnabled(false);
			httpConnManager.setParams(mgrParams);
		}
		GetMethod methodGet = new GetMethod(url);

		try {
			httpClient.executeMethod(methodGet);
			InputStream[] is = cloneInputStream(methodGet.getResponseBodyAsStream(), 2);
			httpClient.executeMethod(methodGet);
			bufferedReader = new BufferedReader(new InputStreamReader(is[1], charset), 8 * 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				entityStringBuilder.append(line + "\n");
			}
			return entityStringBuilder.toString();
		} catch (HttpException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return "";
	}
	public String getContent(String url){
		return this.getContent(url, "UTF-8");
	}
	public InputStream[] cloneInputStream(InputStream is, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream[] iss = new InputStream[size];
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < size; i++) {
			iss[i] = new ByteArrayInputStream(baos.toByteArray());
		}
		return iss;
	}

	public static void main(String[] args) {
		HttpClient client = new HttpClient();
		System.out.println(client.getContent("http://news.sohu.com/_scroll_newslist/20160902/news.inc","utf-8"));
		
	}
}
