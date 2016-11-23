package com.icolor.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icolor.common.constant.QuickPayConstants;
import com.icolor.unionpay.sdk.utils.AcpService;
import com.icolor.unionpay.sdk.utils.LogUtil;
import com.icolor.unionpay.sdk.utils.SDKConfig;
import com.icolor.unionpay.sdk.utils.SDKConstants;
import com.icolor.unionpay.sdk.utils.SDKUtil;

public class IcolorQueryOrderServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", QuickPayConstants.VERSION);                 	//版本号
		data.put("encoding", QuickPayConstants.ENCODING_UTF8);               //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", SDKConstants.SIGNMETHOD);                     //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", SDKConstants.TXNTYPE_QUERY);                     //交易类型 00-默认
		data.put("txnSubType", SDKConstants.TXNSUBTYPE_QUERY);               //交易子类型  默认00
		data.put("bizType", SDKConstants.BIZTYPE_CERTIFIED_PAYMENTS);        //业务类型
		
		/***商户接入参数***/
		String merId = SDKConfig.getConfig().getMerId();
		data.put("merId", merId);                  			   //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", SDKConstants.ACCESSTYPE);                           //接入类型，商户接入固定填0，不需修改
		
		/***要调通交易以下字段必须修改***/
		data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		
		Map<String, String> reqData = AcpService.sign(data,QuickPayConstants.ENCODING_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getSingleQueryUrl();								//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		Map<String, String> rspData = AcpService.post(reqData, url,QuickPayConstants.ENCODING_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, QuickPayConstants.ENCODING_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respMsg = rspData.get("respMsg");
				String respCode = rspData.get("respCode");
				if(("00").equals(respCode)){//如果查询交易成功
					LogUtil.writeLog(String.format("order[%s] query successfully,result is:", orderId));
					
					String origRespCode = rspData.get("origRespCode");
					
					if(("00").equals(origRespCode)){
						LogUtil.writeMessage(String.format("order [%s] status is successfully", orderId));
						resp.getWriter().write(String.format("order [%s] status is successfully", orderId));
						//交易成功，更新商户订单状态
						//TODO
					}else if(("03").equals(origRespCode)||
							 ("04").equals(origRespCode)||
							 ("05").equals(origRespCode)){
						//订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
						//TODO
						resp.getWriter().write(String.format("订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】"));
					}else{
						//其他应答码为交易失败
						//TODO
						LogUtil.writeErrorLog(String.format("order [%s] paid failure",orderId));
						resp.getWriter().write(String.format("order [%s] paid failure",orderId));
					}
				}else if("34".equals(respCode)){
					LogUtil.writeErrorLog(String.format("unionpay can't find the order[%s],it needs query again later", orderId));
					resp.getWriter().write(String.format("unionpay can't find the order[%s],it needs query again later", orderId));
					//订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
					
				}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
					//TODO
					LogUtil.writeErrorLog(String.format("unionpay query order[%s] error,error code is :%s,error msg is : ", orderId,respCode,respMsg));
					resp.getWriter().write(String.format("unionpay query order[%s] error,error code is :%s,error msg is : ", orderId,respCode,respMsg));
				}
			}else{
				LogUtil.writeErrorLog(String.format("order[%s] check signature failure", orderId));
				resp.getWriter().write(String.format("order[%s] check signature failure", orderId));
				//TODO 检查验证签名失败的原因
				
			}
		}else{
			//未返回正确的http状态
			LogUtil.writeErrorLog(String.format("order [%s] 未获取到返回报文或返回http状态码非200",orderId));
			resp.getWriter().write(String.format("order [%s] 未获取到返回报文或返回http状态码非200",orderId));
		}
		
		resp.getWriter().write("<br>");
		resp.getWriter().write("<hr>");
		String contextPath = req.getContextPath();
		resp.getWriter().write("<a href='"+contextPath+"/pages/openAndConsume.jsp'>continue</a>");
		resp.getWriter().write("</br>");
		resp.getWriter().write("<a href='"+contextPath+"/index.jsp'>back home page</a>");
		String reqMessage = SDKUtil.getHtmlResult(reqData);
		String rspMessage =SDKUtil.getHtmlResult(rspData);
		//resp.getWriter().write("交易状态查询交易</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}
