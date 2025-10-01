package com.sa.tpjad.wildfly;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/butonCareCheamaTomcat")
public class ButtonServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String tomcatUrl = "http://localhost:8080/TomcatServletApp/hello";
        String tomcatResponse = "";

        try {
            URL url = new URL(tomcatUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            tomcatResponse = content.toString();
        } catch (Exception e) {
            tomcatResponse = "Nu vei afla niciodata: " + e.getMessage();
        }

        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(tomcatResponse);
    }
}
