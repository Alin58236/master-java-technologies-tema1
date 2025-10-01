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
import java.net.URL;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/GET HelloServlet");

        String datetime = "Habar n-am";

        try{

            //request catre JettyServletApp
            URL url = new URL("http://localhost:8081/JettyServletApp/datetime");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Formatare raspuns
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            datetime = in.readLine();
            in.close();

            System.out.println("Received from JettyServletApp: "+datetime);
        }catch (Exception e){
            System.err.println("ERROR connecting to JettyServletApp: "+e.getMessage());
            System.out.println("Using default datetime.");
        }

        resp.setContentType("text/plain");
        resp.getWriter().println("Servus! Ora este: "+datetime);
    }
}
