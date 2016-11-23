<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.icolor.unionpay.file.FileOperation,com.icolor.unionpay.sdk.utils.SDKConfig" %>
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
<form action="/quickPay0100/icolorQueryOrderServlet" method="post" class="smart-green">
	
	<h1>query order
		<span>Please fill all the texts in the fields.</span>
	</h1>
	
	<label>
		<span>商户订单号：</span>
		<input id="orderId" type="text" name="orderId" placeholder="填写被查询交易的商户订单号" />
	</label>
	
	<label>
		<span>订单发送时间：</span>
		<input id="txnTime" type="text" name="txnTime" placeholder="填写被查询交易的订单发送时间" title="填写被查询交易的订单发送时间，YYYYMMDDhhmmss格式"/>
	</label>
	
	<label>
		<span>&nbsp;</span>
		<input type="submit" class="button" value="query" />
	</label>
</form>

<hr />
<h4>交易状态查询您可能会遇到...</h4>
<p class="faq">
	交易状态查询提示2600000：这个是正常的交易未查询到的状态，如您确定交易成功，请和原交易核对商户号+订单号+时间这3个字段匹配一下。<br>
	<br>
</p>
<hr />
<h1>测试数据</h1>
<%
String orderList = FileOperation.readTxtFile(SDKConfig.getConfig().getOrderFile());
//response.getWriter().write(orderList);
out.println(orderList);
%>
${orderList }
</body>
</html>