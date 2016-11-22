package com.icolor.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icolor.unionpay.sdk.utils.AcpService;
import com.icolor.unionpay.sdk.utils.LogUtil;
import com.icolor.unionpay.sdk.utils.SDKConstants;
import com.icolor.unionpay.sdk.utils.SDKUtil;


public class IcolorBackendResponseServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		LogUtil.writeLog("BackRcvResponse接收后台通知开始");

		String encoding = req.getParameter(SDKConstants.PARAM_ENCODING);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = SDKUtil.getAllRequestParam(req);

		//LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		
		if (null == reqParam || reqParam.isEmpty()) {
			return;
		}
		
		valideData = SDKUtil.encodingUnionpayParams(reqParam,encoding);

		// 重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			// 验签失败，需解决验签问题

		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			// 交易成功，更新商户订单状态
			String respCode = valideData.get("respCode");
			String respMsg = valideData.get("respMsg");
			
			String orderId = valideData.get("orderId"); // 获取后台通知的数据，其他字段也可用类似方式获取
			
			if(SDKConstants.RESP_SUCCESS.equals(respCode)){
				LogUtil.writeMessage("order:" + orderId +" paid successfully");
			}else{
				LogUtil.writeMessage("order:" + orderId +" paid failure,reqspCode:" + respCode + ",errorMsg is :" + respMsg);
			}
		}
		LogUtil.writeLog("BackRcvResponse接收后台通知结束");
		// 返回给银联服务器http 200状态码
		resp.getWriter().print(SDKConstants.CALLBACK_NOTIFY);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

}
