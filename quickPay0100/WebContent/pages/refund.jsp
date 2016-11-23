<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>
<%@ page import="com.icolor.unionpay.file.FileOperation,com.icolor.unionpay.sdk.utils.SDKConfig" %>

<!DOCTYPE form PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
.smart-green {
margin-left:auto;
margin-right:auto;
max-width: 500px;
background: #F8F8F8;
padding: 30px 30px 20px 30px;
font: 12px Arial, Helvetica, sans-serif;
color: #666;
border-radius: 5px;
-webkit-border-radius: 5px;
-moz-border-radius: 5px;
}
.smart-green h1 {	
font: 24px "Trebuchet MS", Arial, Helvetica, sans-serif;
padding: 20px 0px 20px 40px;
display: block;
margin: -30px -30px 10px -30px;
color: #FFF; 
background: #9DC45F;
text-shadow: 1px 1px 1px #949494;
border-radius: 5px 5px 0px 0px;
-webkit-border-radius: 5px 5px 0px 0px;
-moz-border-radius: 5px 5px 0px 0px;
border-bottom:1px solid #89AF4C;
}
.smart-green h1>span {
display: block;
font-size: 11px;
color: #FFF;
}
.smart-green label {
display: block;
margin: 0px 0px 5px;
}
.smart-green label>span {
float: left;
margin-top: 10px;
color: #5E5E5E;
}
.smart-green input[type="text"], .smart-green input[type="email"], .smart-green textarea, .smart-green select {
color: #555;
height: 30px;
line-height:15px;
width: 100%;
padding: 0px 0px 0px 10px;
margin-top: 2px;
border: 1px solid #E5E5E5;
background: #FBFBFB;
outline: 0;
-webkit-box-shadow: inset 1px 1px 2px rgba(238, 238, 238, 0.2);
box-shadow: inset 1px 1px 2px rgba(238, 238, 238, 0.2);
font: normal 14px/14px Arial, Helvetica, sans-serif;
}
.smart-green textarea{
height:100px;
padding-top: 10px;
}

.smart-green .button {
background-color: #9DC45F;
border-radius: 5px;
-webkit-border-radius: 5px;
-moz-border-border-radius: 5px;
border: none;
padding: 10px 25px 10px 25px;
color: #FFF;
text-shadow: 1px 1px 1px #949494;
}
.smart-green .button:hover {
background-color:#80A24A;
}
</style>
</head>
<body style="background-color: #e5eecc;">


<form class="smart-green" method="post" action="/quickPay0100/icolorRefundServlet">

<p>
<label>原交易流水号：</label>
<input id="origQryId" type="text" name="origQryId" placeholder="原交易流水号" value="" title="原交易流水号，从交易状态查询返回报文或代收的通知报文中获取 " required="required"/>
</p>
<p>

<p>
<label>退款金额：</label>
<input id="txnAmt" type="text" name="txnAmt"/>
</p>

<label>&nbsp;</label>
<input type="submit" class="button" value="提交" />
</p>
</form>

<hr />
<h1>测试数据</h1>
<%
String orderList = FileOperation.readTxtFile(SDKConfig.getConfig().getOrderFile());
//response.getWriter().write(orderList);
out.println(orderList);
%>
${orderList }

<div class="question">
<hr />
<h4>退货接口您可能会遇到...</h4>
<p class="faq">
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2010002" target="_blank">订单重复[2010002]</a><br><br>
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2040004" target="_blank"> 原交易状态不正确[2040004]</a><br><br>
<a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=2050004" target="_blank">与原交易信息不符[2050004]</a><br><br>
</p>
</div>
</body>
</html>