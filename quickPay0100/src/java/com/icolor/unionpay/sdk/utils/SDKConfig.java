/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI��������������
 * =============================================================================
 */
package com.icolor.unionpay.sdk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.icolor.common.constant.QuickPayConstants;

public class SDKConfig {

	public static final String FILE_NAME = "acp_sdk.properties";

	private String frontRequestUrl;
	private String backRequestUrl;
	private String singleQueryUrl;
	private String batchQueryUrl;
	private String batchTransUrl;
	private String fileTransUrl;
	private String signCertPath;
	private String signCertPwd;
	private String signCertType;

	private String encryptTrackCertPath;
	private String encryptTrackKeyModulus;
	private String encryptTrackKeyExponent;
	private String cardRequestUrl;
	private String appRequestUrl;
	private String singleMode;

	private String jfFrontRequestUrl;
	private String jfBackRequestUrl;
	private String jfSingleQueryUrl;
	private String jfCardRequestUrl;
	private String jfAppRequestUrl;
	
	private String callbackFrontEnd;
	private String callbackBackEnd;
	
	private int connectionTimeout;
	private int readTimeout;

	private String merId;
	
	public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
	public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
	public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
	public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
	public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
	public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
	public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
	public static final String SDK_APP_URL = "acpsdk.appTransUrl";

	
	public static final String JF_SDK_FRONT_TRANS_URL= "acpsdk.jfFrontTransUrl";
	public static final String JF_SDK_BACK_TRANS_URL="acpsdk.jfBackTransUrl";
	public static final String JF_SDK_SINGLE_QUERY_URL="acpsdk.jfSingleQueryUrl";
	public static final String JF_SDK_CARD_TRANS_URL="acpsdk.jfCardTransUrl";
	public static final String JF_SDK_APP_TRANS_URL="acpsdk.jfAppTransUrl";
	
	
	public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
	public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
	public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
	public static final String SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk.encryptTrackKey.modulus";
	public static final String SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk.encryptTrackKey.exponent";

	public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
	public static final String SDK_DATE_ENC = "acpsdk.date.enc";
	public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
	public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
	private static SDKConfig config;
	private Properties properties;
	
	private static final String SDK_CALLBACK_FRONTEND="acpsdk.callback.frontend";
	
	private static final String SDK_CALLBACK_BACKEND="acpsdk.callback.backend";
	
	private static final String SDK_MERID = "acpsdk.merId";
	
	public static final String SDK_CONNECTIONTIMEOUT = "acpsdk.connectionTimeout";
	
	public static final String SDK_READTIMEOUT = "acpsdk.readTimeOut";

	/**
	 * 
	 * @return
	 */
	public static SDKConfig getConfig() {
		if (null == config) {
			config = new SDKConfig();
		}
		return config;
	}

	/**
	 * load properties from class path
	 */
	public void loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			LogUtil.writeLog("123 classpath: " +SDKConfig.class.getClassLoader().getResource("").getPath()+" aaaaaa " +FILE_NAME);
			in = SDKConfig.class.getClassLoader()
					.getResourceAsStream(FILE_NAME);
			if (null != in) {
				BufferedReader bf = new BufferedReader(new InputStreamReader(
						in, QuickPayConstants.ENCODING_UTF8));
				properties = new Properties();
				try {
					properties.load(bf);
				} catch (IOException e) {
					throw e;
				}
			} else {
				LogUtil.writeErrorLog(FILE_NAME + " can't find at  "+SDKConfig.class.getClassLoader().getResource("").getPath()+" !");
				return;
			}
			loadProperties(properties);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * load(java.util.Properties)}
	 * 
	 * @param pro
	 */
	public void loadProperties(Properties pro) {
		String value = null;
		value = pro.getProperty(SDK_SINGLEMODE);
		if (SDKUtil.isEmpty(value) || SDKConstants.TRUE_STRING.equals(value)) {
			this.singleMode = SDKConstants.TRUE_STRING;
			LogUtil.writeLog("SingleCertMode:[" + this.singleMode + "]");
			
			value = pro.getProperty(SDK_SIGNCERT_PWD);
			if (!SDKUtil.isEmpty(value)) {
				this.signCertPwd = value.trim();
			}
			value = pro.getProperty(SDK_SIGNCERT_TYPE);
			if (!SDKUtil.isEmpty(value)) {
				this.signCertType = value.trim();
			}
		} else {
			this.singleMode = SDKConstants.FALSE_STRING;
			LogUtil.writeLog("SingleMode:[" + this.singleMode + "]");
		}
		
		value = pro.getProperty(SDK_FRONT_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACK_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATTRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_FILETRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_SIGNQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_CARD_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_APP_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_ENCRYPTTRACKCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPath = value.trim();
		}
		
		/**�ɷѲ���**/
		value = pro.getProperty(JF_SDK_FRONT_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfFrontRequestUrl = value.trim();
		}

		value = pro.getProperty(JF_SDK_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfBackRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_SINGLE_QUERY_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfSingleQueryUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_CARD_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfCardRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_APP_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfAppRequestUrl = value.trim();
		}
		
		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_EXPONENT);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyExponent = value.trim();
		}

		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_MODULUS);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyModulus = value.trim();
		}
		
		value = pro.getProperty(SDK_CALLBACK_FRONTEND);
		if (!SDKUtil.isEmpty(value)) {
			this.callbackFrontEnd = value.trim();
		}
		
		value = pro.getProperty(SDK_CALLBACK_BACKEND);
		if (!SDKUtil.isEmpty(value)) {
			this.callbackBackEnd = value.trim();
		}
		
		value = pro.getProperty(SDK_MERID);
		if (!SDKUtil.isEmpty(value)) {
			this.merId = value.trim();
		}
		
		value = pro.getProperty(SDK_CONNECTIONTIMEOUT);
		if (!SDKUtil.isEmpty(value)) {
			this.connectionTimeout = Integer.parseInt(value.trim());
		}
		
		value = pro.getProperty(SDK_READTIMEOUT);
		if (!SDKUtil.isEmpty(value)) {
			this.readTimeout = Integer.parseInt(value.trim());
		}
		
	}

	public String getFrontRequestUrl() {
		return frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPath() {
		return signCertPath;
	}

	public void setSignCertPath(String signCertPath) {
		this.signCertPath = signCertPath;
	}

	public String getSignCertPwd() {
		return signCertPwd;
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public String getSignCertType() {
		return signCertType;
	}

	public void setSignCertType(String signCertType) {
		this.signCertType = signCertType;
	}

	public String getSingleQueryUrl() {
		return singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getCardRequestUrl() {
		return cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}
	
	public String getEncryptTrackCertPath() {
		return encryptTrackCertPath;
	}

	public void setEncryptTrackCertPath(String encryptTrackCertPath) {
		this.encryptTrackCertPath = encryptTrackCertPath;
	}
	
	public String getJfFrontRequestUrl() {
		return jfFrontRequestUrl;
	}

	public void setJfFrontRequestUrl(String jfFrontRequestUrl) {
		this.jfFrontRequestUrl = jfFrontRequestUrl;
	}

	public String getJfBackRequestUrl() {
		return jfBackRequestUrl;
	}

	public void setJfBackRequestUrl(String jfBackRequestUrl) {
		this.jfBackRequestUrl = jfBackRequestUrl;
	}

	public String getJfSingleQueryUrl() {
		return jfSingleQueryUrl;
	}

	public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
		this.jfSingleQueryUrl = jfSingleQueryUrl;
	}

	public String getJfCardRequestUrl() {
		return jfCardRequestUrl;
	}

	public void setJfCardRequestUrl(String jfCardRequestUrl) {
		this.jfCardRequestUrl = jfCardRequestUrl;
	}

	public String getJfAppRequestUrl() {
		return jfAppRequestUrl;
	}

	public void setJfAppRequestUrl(String jfAppRequestUrl) {
		this.jfAppRequestUrl = jfAppRequestUrl;
	}

	public String getSingleMode() {
		return singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}

	public SDKConfig() {
		super();
	}

	public String getEncryptTrackKeyExponent() {
		return encryptTrackKeyExponent;
	}

	public void setEncryptTrackKeyExponent(String encryptTrackKeyExponent) {
		this.encryptTrackKeyExponent = encryptTrackKeyExponent;
	}

	public String getEncryptTrackKeyModulus() {
		return encryptTrackKeyModulus;
	}

	public void setEncryptTrackKeyModulus(String encryptTrackKeyModulus) {
		this.encryptTrackKeyModulus = encryptTrackKeyModulus;
	}

	public String getCallbackFrontEnd() {
		return callbackFrontEnd;
	}

	public void setCallbackFrontEnd(String callbackFrontEnd) {
		this.callbackFrontEnd = callbackFrontEnd;
	}

	public String getCallbackBackEnd() {
		return callbackBackEnd;
	}

	public void setCallbackBackEnd(String callbackBackEnd) {
		this.callbackBackEnd = callbackBackEnd;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}


}
