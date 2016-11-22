package com.icolor.unionpay.sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IcolorDateFromatUtils {

		private static final String FORMAT_1 = "yyyyMMddHHmmss";
		/**
		 * format date as yyyyMMddHHmmss
		 * @return
		 */
		public static String getCurrentTime() {
			return new SimpleDateFormat(FORMAT_1).format(new Date());
		}
		
}
