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
 * 交易：消费：后台资金类交易，有同步应答和后台通知应答<br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障<br>
 * 交易说明:1）确定交易成功机制：商户需开发后台通知接口或交易状态查询接口（Form03_6_5_Query）确定交易是否成功，建议发起查询交易的机制：可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）
 *       2）交易要素卡号+短息验证码(默认验证短信，如果配置了不验证短信则不送短信验证码）
 */

public class IcolorConsumeServlet  extends HttpServlet  {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String orderId = req.getParameter("orderId");
		String txnAmt = req.getParameter("txnAmt");
		
		String smsCode = req.getParameter("smsCode");
		String accountNo = req.getParameter("accNo");
		
		Map<String, String> contentData = new HashMap<String, String>();

		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", QuickPayConstants.VERSION);                  //版本号
		contentData.put("encoding", QuickPayConstants.ENCODING_UTF8);           //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod",SDKConstants.SIGNMETHOD);                           //签名方法 目前只支持01-RSA方式证书加密
		contentData.put("txnType", SDKConstants.TXNTYPE_PAY);                              //交易类型 01-消费
		contentData.put("txnSubType",SDKConstants.TXNSUBTYPE_CONSUME);                           //交易子类型 01-消费
		contentData.put("bizType", SDKConstants.BIZTYPE_CERTIFIED_PAYMENTS);                          //业务类型 认证支付2.0
		contentData.put("channelType", SDKConstants.CHANNEL_TYPE_PC);                          //渠道类型07-PC
		
		/***商户接入参数***/
		contentData.put("merId",SDKConfig.getConfig().getMerId());    //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", SDKConstants.ACCESSTYPE);       //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime",IcolorDateFromatUtils.getCurrentTime());        				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("currencyCode", SDKConstants.CURRENCYCODE);						   //交易币种（境内商户一般是156 人民币）
		contentData.put("txnAmt", txnAmt);							   //交易金额，单位分，不要带小数点
		//contentData.put("accType", "01");//SDKConstants.ACCTYPE);     //文档中无此参数                         //账号类型
		
		//消费：交易要素卡号+验证码看业务配置(默认要短信验证码)。
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("smsCode", "111111");//smsCode			    	//短信验证码,测试环境不会真实收到短信，固定填111111
		
		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
		String accNo = AcpService.encryptData(accountNo, QuickPayConstants.ENCODING_UTF8);  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accountNo,QuickPayConstants.ENCODING_UTF8);
		//////////
		
		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", accountNo);            		//这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		//String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,accountNo,AcpService.encoding_UTF8);
		////////
		
		contentData.put("customerInfo", customerInfoStr);
//		contentData.put("reqReserved", "透传字段");        					//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		
		//后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  代收产品接口规范 代收交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", SDKConfig.getConfig().getCallbackBackEnd());

		//分期付款用法（商户自行设计分期付款展示界面）：
		//修改txnSubType=03，增加instalTransInfo域
		//【测试环境】固定使用测试卡号6221558812340000，测试金额固定在100-1000元之间，分期数固定填06
		//【生产环境】支持的银行列表清单请联系银联业务运营接口人索要
//		contentData.put("txnSubType", "03");                           //交易子类型 03-分期付款
//		contentData.put("instalTransInfo","{numberOfInstallments=06}");//分期付款信息域，numberOfInstallments期数
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,QuickPayConstants.ENCODING_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,QuickPayConstants.ENCODING_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, QuickPayConstants.ENCODING_UTF8)){
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode") ;
				String respMsg = rspData.get("respMsg");
				if(SDKConstants.RESP_SUCCESS.equals(respCode)){
					//交易已受理(不代表交易已成功），等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
					//TODO
					//如果是配置了敏感信息加密，如果需要获取卡号的铭文，可以按以下方法解密卡号
					//String accNo1 = rspData.get("accNo");
					//String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //解密卡号使用的证书是商户签名私钥证书acpsdk.signCert.path
					//LogUtil.writeLog("解密后的卡号："+accNo2);
					resp.getWriter().write("unionpay has accept the order["+orderId+"] successfully");
					resp.getWriter().write("<hr>");
					resp.getWriter().write("</br>");
					String contextPath = req.getContextPath();
					resp.getWriter().write("<a href='"+contextPath+"/pages/openAndConsume.jsp'>continue</a>");
					resp.getWriter().write("</br>");
					resp.getWriter().write("<a href='"+contextPath+"/index.jsp'>back home page</a>");
				}else if(SDKConstants.RESP_TIMEOUT.equals(respCode)||
						SDKConstants.RESP_UNKNOW.equals(respCode)||
						SDKConstants.RESP_HANDLING.equals(respCode)){
					//后续需发起交易状态查询交易确定交易状态
					//TODO
					resp.getWriter().write("unionpay is handling the order["+orderId+"],it need to query unionpay");
					LogUtil.writeLog("pay order, payment info has commit,waitting callback or query order status");
				}else{
					//其他应答码为失败请排查原因
					//TODO
					resp.getWriter().write("unionpay handle the order error:errorCode is: "+respCode + ",errorMsg:" + respMsg);
					LogUtil.writeErrorLog("pay order , unionpay response errorCode:" + respCode);
					
				}
			}else{
				LogUtil.writeErrorLog("验证签名失败");
				//TODO 检查验证签名失败的原因
				resp.getWriter().write("check sign error");
			}
		}else{
			resp.getWriter().write("unionpay can't back right stats code,failure");
			//未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}
		String reqMessage = SDKUtil.getHtmlResult(reqData);
		String rspMessage = SDKUtil.getHtmlResult(rspData);
		LogUtil.writeLog("reqMsg:"+reqMessage);
		LogUtil.writeLog("rspMessage:"+rspMessage);
		
		//resp.getWriter().write("请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}
