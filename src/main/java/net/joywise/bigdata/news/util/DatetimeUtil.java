package net.joywise.bigdata.news.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeUtil {
	public static String datetime(String dateStr) throws ParseException {
		try {
			Date date = new Date();
			SimpleDateFormat year = new SimpleDateFormat("yyyy");
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm:ss");
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (dateStr.length() != 19) {
				switch (dateStr.length()) {
				case 11:
//					System.out.println(dateStr+"=="+year.format(date) + "-" + sdf2.format(sdf1.parse(dateStr)));
					return year.format(date) + "-" + sdf2.format(sdf1.parse(dateStr));
				case 0:
					return sdf3.format(date);
				default:
					String deal = otherDate(dateStr);
//					System.out.println(dateStr+"==="+deal);
					return deal;
				}
			} else {
				return dateStr;
			}
		} catch (Exception e) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		}

	}

	public static String otherDate(String line) {
		String date = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		if (line.contains("小时前")) {
			int hour = Integer.parseInt(line.split("小时前")[0]);
			c.add(Calendar.HOUR_OF_DAY, -hour);
			date = sdf.format(c.getTime());
		}
		if (line.contains("分钟前")) {
			int min = Integer.parseInt(line.split("分钟前")[0]);
			c.add(Calendar.MINUTE, -min);
			date = sdf.format(c.getTime());
		} else {
			date = sdf.format(new Date());
		}
		return date;
	}
}
