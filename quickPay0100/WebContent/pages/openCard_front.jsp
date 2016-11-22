<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>

<form class="api-form" method="post" action="<%request.getContextPath();%>/ACPSample_WuTiaoZhuan/form03_6_2_OpenCard_Front" target="_blank">
<p>
<label>商户号：</label>
<input id="merId" type="text" name="merId" placeholder="" value="777290058140493" title="商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】" required="required"/>
</p>
<p>
<label>交易金额：</label>
<input id="txnAmt" type="text" name="txnAmt" placeholder="交易金额" value="1000" title="单位为分,不能带小数点 " required="required"/>
</p>
<p>
<label>订单发送时间：</label>
<input id="txnTime" type="text" name="txnTime" placeholder="订单发送时间，YYYYMMDDhhmmss格式" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="取北京时间,，YYYYMMDDhhmmss格式" required="required"/>
</p>
<p>
<label>商户订单号：</label>
<input id="orderId" type="text" name="orderId" placeholder="商户订单号" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="自行定义，8-32位数字字母 " required="required"/>
</p>
<p>
<label>&nbsp;</label>
<input type="submit" class="button" value="跳转到银联页面开通" />
<input type="button" class="showFaqBtn" value="遇到问题？" />
</p>
</form>

<div class="question">
<hr />
<h4>代收您可能会遇到...</h4>
<p class="faq">
1.交易返回包含“HTTP Status 400”的错误html串<br>
&nbsp;&nbsp;1)不要出现规范不存在的字段。<br>
&nbsp;&nbsp;2)如果您是自己实现接口的，请注意http头加上Content-Type，Content-Length注意不要不小心设了0。<br><br>
2.<a href="https://open.unionpay.com/ajweb/help/respCode/respCodeList?respCode=9100004">交易失败 11[9100004]Signature verification failed</a><br><br>
</p>
<hr />
 <jsp:include  page="/pages/more_faq.jsp"/>
</div>
