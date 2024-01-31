package com.asktech.admin.reports.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.exception.ValidationExceptions;

public class GeneralUtils implements ErrorValues {

	public static boolean dateValidator(String strDate) throws ValidationExceptions {

		SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
		sdfrmt.setLenient(false);
		try {
			sdfrmt.parse(strDate);
			return true;
		}
		/* Date format is invalid */
		catch (ParseException e) {
			return false;

		}

	}

	public static boolean dateCompare(String fromDate, String toDate) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(fromDate);
		Date date2 = sdf.parse(toDate);

		int result = date1.compareTo(date2);
		System.out.println("result: " + result);

		if (result > 0) {
			return false;
		}

		return true;
	}

	public static String convertString(String s) {
		int ctr = 0;
		int n = s.length();
		char ch[] = s.toCharArray();
		int c = 0;
		for (int i = 0; i < n; i++) {
			if (i == 0)
				ch[i] = Character.toUpperCase(ch[i]);
			if (ch[i] == ' ') {
				ctr++;
				ch[i + 1] = Character.toUpperCase(ch[i + 1]);
				continue;
			} else
				ch[c++] = ch[i];
		}
		return String.valueOf(ch, 0, n - ctr);
	}
    

}
