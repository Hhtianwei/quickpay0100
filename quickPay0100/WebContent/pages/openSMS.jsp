<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>

<form class="api-form" method="post" action="<%request.getContextPath();%>/ACPSample_WuTiaoZhuan/form03_6_6_OpenSMS" target="_blank">
<p>
<label>商户号：</label>
<input id="merId" type="text" name="merId" placeholder="" value="777290058140493" title="默认商户号仅作为联调测试使用，正式上线还请使用正式申请的商户号" required="required"/>
</p>
<p>
<label>订单发送时间：</label>
<input id="txnTime" type="text" name="txnTime" placeholder="订单发送时间" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="取北京时间，YYYYMMDDhhmmss格式" required="required"/>
</label>
<p>
<label>商户订单号：</label>
<input id="orderId" type="text" name="orderId" placeholder="商户订单号" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="自行定义，8-32位数字字母" required="required"/>
</p>

<p>
<label>短信通知手机号：</label>
<input id="phoneNo" type="text" name="phoneNo" placeholder="用来接收短信的手机号" value="13552535506" title="用来接收短信的手机号，测试环境使用测试卡号对应的手机号，不会真正收到短信" required="required"/>
</p>
<p>
<label>开通卡号：</label>
<input id="accNo" type="text" name="accNo" placeholder="要开通的卡号" value="6221558812340000" title="测试环境填写测试卡，生产环境填写真实卡" required="required"/>
</p>
<p>


<label>&nbsp;</label>
<input type="submit" class="button" value="提交" />
<input type="button" class="showFaqBtn" value="遇到问题？" />
</p>
</form>

<div class="question">
<hr />
<h4>消费撤销您可能会遇到...</h4>
<p class="faq">
暂无
</p>
<hr />
 <jsp:include  page="/pages/more_faq.jsp"/>
</div>