<html>
<!--
  Copyright 2006 Freedom Software

-->

<%@ page session="false"%>
<%@ page import="Jt.*" %>

<body bgcolor="white">



<!-- HelloWorld.jsp - Example of JSP support -->



<!- Create an instance of HelloWorld -->

<%
   JtFactory main = new JtFactory (); // Create the factory class

   // Load Jt resources from a Resource Stream. The resource file .Jtrc should be placed
   // under /WEB-INF
     
   main.setResourceStream (application.getResourceAsStream("/WEB-INF/.Jtrc"));


   // Create the instance of the Jt objects

   JtObject helloWorld = (JtObject) main.createObject ("Jt.examples.HelloWorld", "helloWorld");   


   Object jtReply = main.sendMessage (helloWorld, new JtMessage ("JtHello"));
%>

<!-- Send a message to the object and print the reply -->

<%= jtReply %>




</body>
</html>