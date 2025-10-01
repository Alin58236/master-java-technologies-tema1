<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>CE ZI E AZI</title>
</head>
<body>
<h1>WildFly Servlet de unde apelez Tomcat care apeleaza Jetty si afli ce zi e azi</h1>
<form id="dayForm">
    <button type="submit">Ce zi e azi?</button>
</form>

<p id="message"></p>

<script>
    document.getElementById("dayForm").addEventListener("submit", function(e) {
        e.preventDefault(); // oprește submit normal
        fetch('butonCareCheamaTomcat') // cere servlet-ul
            .then(response => response.text())
            .then(data => {
                document.getElementById("message").innerText = data; // update dinamic
            })
            .catch(err => console.error(err));
    });
</script>

<p>${message}</p>
</body>
</html>
