<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>


<html:html locale="true">
<head>
<title>Jt Struts Example</title>
<html:base/>
</head>
<body bgcolor="white">


<table>
<c:choose>

<c:when test="${jtReply != null}">
  <c:forEach items="${jtReply}" var="currentRecord">
  <tr>
      <td><c:out value="${currentRecord.email}"/></td>
      <td><c:out value="${currentRecord.firstname}"/></td>
      <td><c:out value="${currentRecord.lastname}"/></td>
      <td><c:out value="${currentRecord.subject}"/></td>
      <td><c:out value="${currentRecord.status}"/></td>
      <td><c:out value="${currentRecord.location}"/></td>
      <td><c:out value="${currentRecord.tstamp}"/></td>
      <td><c:out value="${currentRecord.mdate}"/></td>
      <td><c:out value="${currentRecord.comments}"/></td>
  </tr>
  </c:forEach>
</c:when>
<c:otherwise>
No records found.
</c:otherwise>
</c:choose>



</table>
</body>
</html:html>














