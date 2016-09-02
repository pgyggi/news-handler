package net.joywise.bigdata.news.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class OutputTask implements Runnable {
	private Logger logger = Logger.getLogger(OutputTask.class);
	private String fileName;

	public OutputTask(String fileName) {
		this.fileName = fileName;
	}
	public void run() {
		while (true) {
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LinkedList<String> list = WriterQueue.getQueue().takeAll();
			write2Disk(list);
			list = null;
		}
	}

	private void write2Disk(LinkedList<String> list) {
		if (list == null || list.size() == 0) {
			logger.info("no data...");
			return;
		}
		logger.info("begin write record to file: " + list.size());
		String path = fileName;
		File outputFile = new File(path);
		if (outputFile == null || !outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;

		try {
			out = new FileOutputStream(outputFile, true);
			writer = new OutputStreamWriter(out);
			bw = new BufferedWriter(writer);

			for (String content : list) {
				bw.write(content);
				bw.newLine();
				bw.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sleep(int millis) throws InterruptedException {
		Thread.sleep(millis);
	}

}