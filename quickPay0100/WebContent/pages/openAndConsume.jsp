<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="java.text.*" import="java.util.*" pageEncoding="UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>quick pay</title>

<script src="../static/jquery-1.11.2.min.js"></script>

<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

a:link {
	color: #FF0000;
	text-decoration: underline;
}

a:visited {
	color: #00FF00;
	text-decoration: none;
}

a:hover {
	color: #000000;
	text-decoration: none;
}

a:active {
	color: #FFFFFF;
	text-decoration: none;
}

a:link, a:visited {
	color: #FF0000;
	text-decoration: underline;
}

a:hover, a:active {
	color: #000000;
	text-decoration: none;
}

.pay-form {
	width: 500px;
	margin: 200px auto;
}

.pay-form p {
	height: 50px;
	line-height: 50px;
	font-family: "黑体";
	font-size: 20px;
}

a {
	height: 21px;
	line-height: 21px;
	padding: 0 11px;
	background: #02bafa;
	border: 1px #26bbdb solid;
	border-radius: 3px;
	/*color: #fff;*/
	display: inline-block;
	text-decoration: none;
	font-size: 12px;
	outline: none;
}

#submitForm {
	background: #E27575;
	border: none;
	padding: 10px 25px 10px 25px;
	color: #FFF;
	box-shadow: 1px 1px 5px #B6B6B6;
	border-radius: 3px;
	text-shadow: 1px 1px 1px #9E3F3F;
	cursor: pointer;
}
</style>
<script type="text/javascript">
	$(function(){

		$(".cardcheck").on("click",function(){
			var orderNo = $("#orderId").val();
			var txnAmt = $("#txnAmt").val();
			var accNo = $("#accNo").val();
			var checkData = {"orderNo":orderNo,"accNo":accNo};
			 
			$.ajax({
				url:"/quickPay0100/icolorCheckServlet",
				async:false,
				type:"post",
				data:checkData,
				success:function(data){
					if(data == null){
						alert("can't get unionpay callback");
						return;
					}
					$form = $(".pay-form");
					if("true" == data){
						alert("quick pay has opend,please get SMS code,and pay order");
						$form.find(".phoneLab").show();
						$form.find(".smsLab").show();
						$form.attr("action","/quickPay0100/icolorConsumeServlet");
					}else if("false" == data){
						$form.find(".phoneLab").hide();
						$form.find(".smsLab").hide();
						alert("not opend quick pay");
						$("#submitForm").val("开通并支付");
						$form.attr("action","/quickPay0100/icolorOpenAndConsumeServlet");
					}else{
						alert("result:" + data);
					}
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert(XMLHttpRequest.status);
                    alert(XMLHttpRequest.readyState);
                    alert(textStatus);
                }
			});
		});
		
		$(".phoneNo").on("click",function(){
			var phoneNo = $("#phoneNo").val();
			if(!(/^1[34578]\d{9}$/.test(phoneNo))){ 
		        alert("手机号码有误，请重填");  
		        return false; 
		    }
			
			var orderNo = $("#orderId").val();
			var txnAmt = $("#txnAmt").val();
			var accNo = $("#accNo").val();
			var smsData = {"orderNo":orderNo,"phoneNo":phoneNo,"txnAmt":txnAmt,"accNo":accNo};
			
			$.ajax({
				async:false,
				url:"/quickPay0100/icolorSendConsumeSMSServlet",
				type:"post",
				data:smsData,
				success:function(data){
					if("true" == data){
						alert("please input the sms code");
					}else{
						alert("unionpay send the smsCode error");
					}
				},
				error:function(){
					alert("request unionpay send SMS code error");
				}
			});
		});
	});
</script>

</head>

<body>
	<form class="pay-form" method="post"
		action="/quickPay0100/icolorOpenAndConsumeServlet">

		<p>
			<label>商户订单号：</label> <input id="orderId" type="text" name="orderId" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>"/>
		</p>

		<p>
			<label>交易金额：</label> &nbsp;&nbsp;<input id="txnAmt" type="text"
				name="txnAmt"  value="10" />
		</p>

		<p class="">
			<label>卡号：</label> &nbsp;<input id="accNo" type="text" name="accNo" value="6216261000000000018" /><a class="cardcheck">check</a>
		</p>
		
		<p class="phoneLab" style="display:none;">
			<label>手机号：</label> &nbsp;<input id="phoneNo" type="text" name="phoneNo"/><a class="phoneNo">获取短信验证码</a>
		</p>
		
		<p class="smsLab" style="display:none;">
			<label>验证码：</label> &nbsp;<input id="smsCode" type="text" name="smsCode"/>
		</p>

		<p>
		<input id="submitForm" type="submit" value="支付"/>
		</p>
	</form>
</body>

</html>