<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="Jt.*" %>



<html>



<body bgcolor="white">



<!-- HelloWorld.jsp - Example of JSP support -->


<!-- Create the Jt business objects  -->

<jsp:useBean id='main' class='Jt.JtFactory' scope='request'/>


<!-- Load Jt resources from a Resource Stream. The resource file .Jtrc should be placed -->
<!-- under /WEB-INF -->

<jsp:setProperty name="main" property="resourceStream" value="<%= application.getResourceAsStream("/WEB-INF/.Jtrc") %>"/>


<!- Create an instance of HelloWorld -->

<jsp:useBean id='helloWorld' class='Jt.examples.HelloWorld' scope='request'/>



<!-- Implement the business logic  -->

<%  Object jtReply = main.sendMessage (helloWorld, new JtMessage ("JtHello")); %>



<!-- View the results -->


<%= jtReply %>


</body>
</html>