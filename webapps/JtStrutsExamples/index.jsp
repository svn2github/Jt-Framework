<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<html:html locale="true">
<head>
<title>Jt Struts Example</title>
<html:base/>
</head>
<body bgcolor="white">

<html:errors />

<p>
<html:link page="/HelloWorld.do?jtMsgId=JtHello">Click this link to run the HelloWorld.do action which invokes the Jt Struts Adapter</html:link>
<p>
<html:link page="/BPMHelloWorld.do?jtMsgId=JtACTIVATE">Click this link to invoke HelloWorld1.do action which invokes the HelloWorld JPBM Business Process. This action also invokes the Jt Struts Adapter</html:link>
<p>
<html:link page="/HelloWorld.do?jtMsgId=JtHello1">This link will produced an error (Invalid message ID)</html:link>
<p>
<html:link forward="form">Click this link to invoke an example involving a form</html:link>
<p>
<html:link forward="bpmform">Click this link to invoke an example involving a form and a BPM process</html:link>
<p>
<html:link page="/ajax.jsp">Ajax example</html:link>



</body>
</html:html>














