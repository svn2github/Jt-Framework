
<html>
<head>

<title> Jt Ajax example </title>

<script type="text/javascript">

var req;

// clear the input field

function clear() {

   var key = document.getElementById("key");
   key.value = "";


}

// clear the reply

function clearReply() {

   var key = document.getElementById("reply");
   key.value = "";


}


// Send the Ajax request to the JtAjaxServlet

function sendAjaxRequest() {

   var key = document.getElementById ("key");


   var url = "/JtStrutsExamples/JtAjaxServlet?jtMsgId=JtHello&jtClassName=Jt.examples.HelloWorld";

   if (window.XMLHttpRequest) {
     req = new XMLHttpRequest ();

   }
   else if (window.ActiveXObject) {
     req = new ActiveXObject ("Microsoft.XMLHTTP");
   }


   req.open ("Get", url, true);
   req.onreadystatechange = callback;
   req.send (null);

}

function callback() {
  if (req.readyState==4) {
    if (req.status == 200) {

      //var replyl = document.getElementById ('reply');
      //reply.value = req.responseText;


      if (window.XMLHttpRequest) {
        nonMSPopulate ();
      } else if (window.ActiveXObject) {
        msPopulate ();
      }

    }

  }
  clear ();
}


// Parse the XML response

function nonMSPopulate () {

   xmlDoc = document.implementation.createDocument("","", null);

   var resp = req.responseText;

   var parser = new DOMParser ();

   var dom = parser.parseFromString (resp, "text/xml");

   strVal = dom.getElementsByTagName ("value");

   var reply = document.getElementById ('reply');  
   reply.value = strVal[0].childNodes[0].nodeValue;

}


// Parse the XML response (Microsoft browser)

function msPopulate() {


   var resp = req.responseText;

   var xmlDoc = new ActiveXObject ("Microsoft.XMLDOM");

   xmlDoc.async="false";
   xmlDoc.loadXML (resp);

   nodes=xmlDoc.documentElement.childNodes;


   str = xmlDoc.getElementsByTagName ('value');

   var reply = document.getElementById ('reply');  


   reply.value = str[0].firstChild.data;


}


</script>
<title>Ajax example</title>

</head>
<body>


<table>
 <tr>
  <td>
     Enter Key to send an Ajax request to the Jt servlet (JtAjaxServlet):
     <input type="text" id="key" name="key"
           onkeyup="sendAjaxRequest();">
  </td>
  <p>
 </tr>
 <tr>
   <td>Reply: <input type="text" readonly id="reply"></td>
 </tr>
<table>
<p>
<input type="button" onclick="clearReply()" value="Clear reply" />
<p>
 <tr>
  <td>
   The request Ajax is sent to the Jt servlet (JtAjaxServlet) when a key is entered. The reply is displayed above.
  </td>
  <p>
 </tr>
</body>
</html>














