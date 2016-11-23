package com.icolor.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icolor.common.constant.QuickPayConstants;
import com.icolor.unionpay.sdk.utils.AcpService;
import com.icolor.unionpay.sdk.utils.IcolorDateFromatUtils;
import com.icolor.unionpay.sdk.utils.LogUtil;
import com.icolor.unionpay.sdk.utils.SDKConfig;
import com.icolor.unionpay.sdk.utils.SDKConstants;
import com.icolor.unionpay.sdk.utils.SDKUtil;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：无跳转产品<br>
 * 交易：消费短信：后台交易，无通知<br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障<br>
 * 交易说明: 金额  + 卡号  + 手机号phoneNo（customerInfo域），同步应答确定交易成功。
 */

public class IcolorSendConsumeSMSServlet  extends HttpServlet  {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//String merId = req.getParameter("merId");
		String orderId = req.getParameter("orderNo");
		//String txnTime = req.getParameter("txnTime");
		String txnAmt = req.getParameter("txnAmt");
		String phoneNo = req.getParameter("phoneNo");
		String accountNo = req.getParameter("accNo");

		Map<String, String> contentData = new HashMap<String, String>();

		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", QuickPayConstants.VERSION);                  //版本号
		contentData.put("encoding", QuickPayConstants.ENCODING_UTF8);                //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", SDKConstants.SIGNMETHOD);                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", SDKConstants.TXNTYPE_SEND_SMS);                              //交易类型 77-发短信
		contentData.put("txnSubType", SDKConstants.TXNSUBTYPE_SEND_SMS);                           //交易子类型 02-消费短信
		contentData.put("bizType", SDKConstants.BIZTYPE_CERTIFIED_PAYMENTS);                          //业务类型 认证支付2.0
		contentData.put("channelType", SDKConstants.CHANNEL_TYPE_PC);                          //渠道类型07-PC
		
		/***商户接入参数***/
		contentData.put("merId", SDKConfig.getConfig().getMerId());                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", SDKConstants.ACCESSTYPE);                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", IcolorDateFromatUtils.getCurrentTime());         				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("currencyCode", SDKConstants.CURRENCYCODE);						   //交易币种（境内商户一般是156 人民币）
		contentData.put("txnAmt", txnAmt);							   //交易金额，单位分，不要带小数点
		//contentData.put("accType", "01");                              //账号类型
		
		//必送手机号
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("phoneNo", phoneNo);
		
		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo,phoneNo加密使用：
		String accNo = AcpService.encryptData(accountNo, QuickPayConstants.ENCODING_UTF8);  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,QuickPayConstants.ENCODING_UTF8);
		
		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", accountNo);            		//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		//String customerInfoStr = DemoBase.getCustomerInfo(customerInfoMap,accountNo,DemoBase.encoding_UTF8);
		////////
		
		contentData.put("customerInfo", customerInfoStr);
		//contentData.put("reqReserved", "透传字段");        					//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,QuickPayConstants.ENCODING_UTF8);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   								 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,QuickPayConstants.ENCODING_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		boolean result = false;
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, QuickPayConstants.ENCODING_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode") ;
				String respMsg = rspData.get("respMsg");

				if(SDKConstants.RESP_SUCCESS.equals(respCode)){
					//成功
					result = true;
					//TODO
				}else{
					//其他应答码为失败请排查原因或做失败处理
					//TODO
					LogUtil.writeErrorLog("unionpay send sms code error,error code is:" + respCode + ",error msg is :" + respMsg);
				}
			}else{
				LogUtil.writeErrorLog("验证签名失败");
				//TODO 检查验证签名失败的原因
				
			}
		}else{
			//未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}
		resp.getWriter().print(result);
		String reqMessage = SDKUtil.getHtmlResult(reqData);
		String rspMessage = SDKUtil.getHtmlResult(rspData);
		//resp.getWriter().write("请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}
