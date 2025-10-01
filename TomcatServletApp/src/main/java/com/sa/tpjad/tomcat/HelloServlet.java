package com.sa.tpjad.tomcat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("/GET HelloServlet");

        String datetime = "Habar n-am";

        try{
            datetime = callJetty("http://localhost:8081/JettyServletApp/datetime");
        }catch (Exception e){
            System.err.println("ERROR connecting to JettyServletApp: "+e.getMessage()+"\nUsing default datetime.\"");
        }

        resp.setContentType("text/plain");
        resp.getWriter().println("Servus! Ora este: "+datetime);
    }

    public String callJetty(String urlString) throws IOException {

        //request catre JettyServletApp
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        //Formatare raspuns
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String datetime = in.readLine();
        System.out.println("Received from JettyServletApp: "+datetime);
        in.close();

        return datetime;
    }
}
