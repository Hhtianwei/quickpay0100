package com.icolor.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.icolor.unionpay.sdk.utils.SDKConfig;

public class AutoLoadServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		SDKConfig.getConfig().loadPropertiesFromSrc();
		super.init();
	}
}
