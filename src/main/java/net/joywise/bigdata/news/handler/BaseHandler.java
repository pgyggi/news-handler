package net.joywise.bigdata.news.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;


public class BaseHandler {
	private Logger logger = Logger.getLogger(BaseHandler.class);

	public String getContent(String url) {
		HttpClient httpClient = new HttpClient();
		// httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
		// "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X)
		// AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143
		// Safari/601.1 joywise.net");
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "wechat_count1.0");
		try {
			HttpConnectionManager httpConnManager = httpClient.getHttpConnectionManager();
			BufferedReader bufferedReader;
			StringBuilder entityStringBuilder = new StringBuilder();
			if (httpConnManager != null) {
				HttpConnectionManagerParams mgrParams = new HttpConnectionManagerParams();
				mgrParams.setSoTimeout(20000);
				mgrParams.setTcpNoDelay(true);
				mgrParams.setConnectionTimeout(20000);
				mgrParams.setLinger(0);
				mgrParams.setStaleCheckingEnabled(false);
				httpConnManager.setParams(mgrParams);
			}
			GetMethod methodGet = new GetMethod(url);

			httpClient.executeMethod(methodGet);
			InputStream[] is = cloneInputStream(methodGet.getResponseBodyAsStream(), 2);
			String charset = getCharSet(is[0]);
			httpClient.executeMethod(methodGet);
			bufferedReader = new BufferedReader(new InputStreamReader(is[1], charset), 8 * 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				entityStringBuilder.append(line + "\n");
			}
			return entityStringBuilder.toString();
		} catch (Exception e) {
			logger.error(url+":"+e.getMessage());
		}

		return "";
	}

	public String getCharSet(String content) {
		String regex = "[<meta|<META].+?charset=[^\\w]?([-\\w]+).*";
		Pattern pattern = Pattern.compile(regex);
		for (String s : content.split("\n")) {
			Matcher matcher = pattern.matcher(s);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return "UTF-8";
	}

	public String getCharSet(InputStream content) throws IOException {
		BufferedReader bufferedReader;
		bufferedReader = new BufferedReader(new InputStreamReader(content, "UTF-8"), 8 * 1024);
		String regex = "[<meta|<META].+?charset=[^\\w]?([-\\w]+).*";
		Pattern pattern = Pattern.compile(regex);
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find())
				return matcher.group(1);
		}
		return "UTF-8";
	}

	public String charReplace(String charSequence) {
		return charSequence.replaceAll("<[^>]+>", "").replaceAll("\\s{1,}", " ").replaceAll("\t", "").replaceAll("\n", "")
				.trim();
		
	}

	public String emojjReplace(String title) {
		String pattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
		Pattern emoji = Pattern.compile(pattern);
		Matcher emojiMatcher = emoji.matcher(title);
		String textInPage = emojiMatcher.replaceAll("");
		return textInPage;
	}
	public String trimStyle(String content) {
		String regEx = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content.toLowerCase());
		String result = content;
		if (m.find()) {
			result = m.replaceAll("");
		}
		return result;
	}

	public String trimScript(String content) {
		String regEx = "<script[^>]*>[\\d\\D]*?</script>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content.toLowerCase());
		String result = content;
		if (m.find()) {
			result = m.replaceAll("");
		}
		return result;
	}
	
	public InputStream[] cloneInputStream(InputStream is,int size){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream[] iss= new InputStream[size];
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
		for(int i=0;i<size;i++){
			iss[i]=new ByteArrayInputStream(baos.toByteArray());
		}
		return iss;
	}
	
	public String titleHandler(String title) {
		String titleReg = "(.*?)[-_|].*";
		Pattern p = Pattern.compile(titleReg);
		Matcher m = p.matcher(title);
		if (m.find()) {
			return m.group(1);
		} else {
			return title;
		}
	}
	public InputStream getHttpStream(String url) {
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, "wechat_count1.0");
		HttpConnectionManager httpConnManager = httpClient.getHttpConnectionManager();

		if (httpConnManager != null) {
			HttpConnectionManagerParams mgrParams = new HttpConnectionManagerParams();
			mgrParams.setSoTimeout(5000);
			mgrParams.setTcpNoDelay(true);
			mgrParams.setConnectionTimeout(5000);
			mgrParams.setLinger(0);
			mgrParams.setStaleCheckingEnabled(false);
			httpConnManager.setParams(mgrParams);
		}

		GetMethod methodGet = new GetMethod(url);
		try {
			httpClient.executeMethod(methodGet);
			methodGet.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return methodGet.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getHttpString(InputStream is) {
		BufferedReader bufferedReader;
		StringBuilder entityStringBuilder = new StringBuilder();
		InputStream[] contentInputStream = cloneInputStream(is,2);
		try {
			String charset = getCharSet(contentInputStream[0]);
			bufferedReader = new BufferedReader(new InputStreamReader(contentInputStream[1], charset), 8 * 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {

				entityStringBuilder.append(line + "\n");
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				contentInputStream[0].close();
				contentInputStream[1].close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entityStringBuilder.toString();
	}


}
