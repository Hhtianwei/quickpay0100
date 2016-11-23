package com.icolor.unionpay.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class SDKUtil {

	/**
	 * 生成签名(SHA1摘要算法)
	 * 
	 * @param data
	 *            待签名数据Map键对形式
	 * @param encoding
	 *            编码
	 * @return 签名是否成功
	 */
	public static boolean sign(Map<String, String> data, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		
		// 设置签名证书序列
		data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
		// 将Map信息转换成key1=value1&key2=value2的形
		String stringData = coverMap2String(data);
		LogUtil.writeLog("待签名请求报文串:[" + stringData + "]");
		
		/**
		 * 签名\base64编码
		 */
		byte[] byteSign = null;
		String stringSign = null;
		try {
			// 通过SHA1进行摘要并转16进制
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(CertUtil.getSignCertPrivateKey(), signDigest));
			stringSign = new String(byteSign);
			// 设置签名域
			data.put(SDKConstants.param_signature, stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
			return false;
		}
	}

	/**
	 * 通过传入的证书绝对路径和证书密码读取签名证书进行签名并返回签名<br>
	 * 
	 * @param data
	 *            待签名数据Map键对形式
	 * @param encoding
	 *            编码
	 * @param certPath
	 *            证书绝对路径
	 * @param certPwd
	 *            证书密码
	 * @return 签名
	 */
	public static boolean signByCertInfo(Map<String, String> data, String certPath, String certPwd, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(certPath) || isEmpty(certPwd)) {
			LogUtil.writeLog("Invalid Parameter:CertPath=[" + certPath + "],CertPwd=[" + certPwd + "]");
			return false;
		}
		// 设置签名证书序列
		data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
		// 将Map信息转换成key1=value1&key2=value2的形
		String stringData = coverMap2String(data);
		/**
		 * 签名\base64编码
		 */
		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil.base64Encode(
					SecureUtil.signBySoft(CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
			stringSign = new String(byteSign);
			// 设置签名域
			data.put(SDKConstants.param_signature, stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
			return false;
		}
	}

	/**
	 * 将Map中的数据转换成按照Key的ascii码排序后的key1=value1&key2=value2的形 不包含签名域signature
	 * 
	 * @param data
	 *            待拼接的Map数据
	 * @return 拼接好后的字符串
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (SDKConstants.param_signature.equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + SDKConstants.EQUAL + en.getValue() + SDKConstants.AMPERSAND);
		}
		return sf.substring(0, sf.length() - 1);
	}

	/**
	 * 兼容老方 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;
		try {

			if (StringUtils.isNotBlank(result)) {
				if (result.startsWith("{") && result.endsWith("}")) {
					System.out.println(result.length());
					result = result.substring(1, result.length() - 1);
				}
				map = parseQString(result);
			}

		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return map;
	}

	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            要解析的字符
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str) throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;// 值里有嵌
		char openName = 0;
		if (len > 0) {
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符
				curChar = str.charAt(i);// 取当前字
				if (isKey) {// 如果当前生成的是key

					if (curChar == '=') {// 如果读取=分隔
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else {// 如果当前生成的是value
					if (isOpen) {
						if (curChar == openName) {
							isOpen = false;
						}

					} else {// 如果没开启嵌
						if (curChar == '{') {// 如果碰到，就启嵌
							isOpen = true;
							openName = '}';
						}
						if (curChar == '[') {
							isOpen = true;
							openName = ']';
						}
					}
					if (curChar == '&' && !isOpen) {// 如果读取&分割,同时这个分割符不是域，这时将map里添
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}

			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey, String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 判断字符串是否为NULL或空
	 * 
	 * @param s
	 *            待判断的字符串数
	 * @return 判断结果 true- false-
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim());
	}

	/**
	 * 过滤请求报文中的空字符串或空字符
	 * 
	 * @param contentData
	 * @return
	 */
	public static Map<String, String> filterBlank(Map<String, String> contentData) {
		LogUtil.writeLog("打印请求报文 :");
		Map<String, String> submitFromData = new HashMap<String, String>();
		Set<String> keyset = contentData.keySet();

		for (String key : keyset) {
			String value = contentData.get(key);
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(key, value.trim());
				LogUtil.writeLog(key + "-->" + String.valueOf(value));
			}
		}
		return submitFromData;
	}

	/**
	 * 组装请求，返回报文字符串用于显示
	 * 
	 * @param data
	 * @return
	 */
	public static String getHtmlResult(Map<String, String> data) {

		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			String key = en.getKey();
			String value = en.getValue();
			if ("respCode".equals(key)) {
				sf.append("<b>" + key + SDKConstants.EQUAL + value + "</br></b>");
			} else {
				sf.append(key + SDKConstants.EQUAL + value + "</br>");
			}
		}
		return sf.toString();
	}

	/**
	 * 1 yuan convert 100 switch money,from yuan to feng
	 * 
	 * @param yuan
	 * @return
	 */
	public static String yuanSwitchfen(final String yuan) {
		if (StringUtils.isEmpty(yuan)) {
			return null;
		}

		BigDecimal yuanDecimal = new BigDecimal(yuan);
		yuanDecimal = yuanDecimal.multiply(new BigDecimal(SDKConstants.CURRENCY_RATE));
		String fen = yuanDecimal.toString();
		int count = fen.indexOf(SDKConstants.POINT);
		if (count == -1) {
			count = fen.length();
		}
		return fen = fen.substring(0, count);
	}

	/**
	 * 100 fen convert 1 yuan
	 *
	 * @param fen
	 * @return
	 */
	public static String fenSwitchyuan(final String fen) {
		if (StringUtils.isEmpty(fen)) {
			return null;
		}
		BigDecimal fenDecimal = new BigDecimal(fen);
		fenDecimal = fenDecimal.divide(new BigDecimal(SDKConstants.CURRENCY_RATE));
		return fenDecimal.toString();
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null == temp) {
			return null;
		}
		while (temp.hasMoreElements()) {
			String en = (String) temp.nextElement();
			String value = request.getParameter(en);
			
			// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段
			if (null == value || "".equals(value)) {
				continue;
			}
			res.put(en, value);
		}
		return res;
	}

	public static Map<String, String> encodingUnionpayParams(Map<String, String> reqParam, String encoding) {
		if (null == reqParam || reqParam.isEmpty()) {
			return null;
		}
		Map<String,String> valideData = new HashMap<String, String>(reqParam.size());
		for (Map.Entry<String, String> entry : reqParam.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			try {
				value = new String(value.getBytes(encoding), encoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			valideData.put(key, value);
		}
		return valideData;
	}

}
