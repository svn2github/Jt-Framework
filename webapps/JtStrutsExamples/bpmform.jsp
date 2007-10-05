<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<html:html locale="true">
<head>
<title></title>
<html:base/>
</head>
<body bgcolor="white">


<html:errors />

<html:form action= "BPMFindRecords">

  <table width="45%" border="0">
  <tr>
    <td>Email: </td>
    <td><html:text property="email" /></td>
  </tr>
  <tr>
    <td colspan="2" align="center"><html:submit /></td>
  </tr>
  </table>

</html:form>


</body>
</html:html>














