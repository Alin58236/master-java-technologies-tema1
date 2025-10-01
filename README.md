# master-java-technologies-tema1

Acest repository contile 3 aplicatii Servlet pe 3 medii diferite: Tomcat, Jetty, Wildfly



# a. TomcatServletApp

    Nume: TomcatServletApp
    Clasa: HelloServlet.java
    Port:8080

    Prima aplicatie creata a fost o aplicatie Java care expune un singur endpoint "/hello". Aceasta aplicatie a fost testata pe serverul Tomcat pe portul 8080, iar ca raspuns aceasta returneaza textul "Salut! Este ora <data si ora curenta preluate din urmatorul microserviciu>". In cazul in care cel de-al doilea microserviciu nu este activ pe portul 8081 aplicatia va returna utilizatorului un text default, in cazul de fata "Habar n-am".

    Pentru setup-ul acestei aplicatii a fost nevoie sa creez un proiect in Intellij IDEA de tipul Maven Acrhetype de tipul maven-archetype-webapp. Pentru dependinte am completat in fisierul pom.xml :

    ```
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>5.0.0</version>
        <scope>provided</scope>
    </dependency>
    ```

    Am folosit <scope>provided</scope> pentru a nu include aceasta dependinta in WAR-ul final (Tomcat contine deja servlet-api).

    Apoi am creat directorul "java" pe care l-am setat ca SourcesRoot. In interiorul acestuia am creat pachetul com.sa.tpjad.tomcat care contine clasa HelloServlet.java :

     ```
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
     ```
    Aceasta clasa expune endpoint-ul /hello prin intermediul adnotarii @WebServlet("/hello"). Cand este apelata, <br/> face un apel API la urmatorul serviciu pentru a extrage data si ora curenta si returneaza un sir <br/> de caractere care sa contina acest raspuns.

    Datorita faptului ca am folosit adnotarea pentru expunerea endpoint-ului, singurul lucru pe care l-am scris in <br/> web.xml a fost doar legatura dintre numele servlet-ului (HelloServlet) si clasa care contine <br/> logica din spatele API call-ului (com.sa.tpjad.tomcat.HelloServlet).

    Pentru pornirea acestei aplicatii pe Tomcat trebuie compilat proiectul de maven utilizand comanda mvn clean install. <br/> In urma acesteia rezulta fisierul TomcatServletApp.war care trebuie copiat in fisierul webapps <br/> din directorul in care se afla tomcat. Dupa copiere, daca rulam start.bat din tomcat/bin serverul va rula si <br/> vom putea apela endpoint-ul http://localhost:8080/TomcatServletApp/hello



# b. JettyServletApp

    Nume: JettyServletApp
    Clasa: DateTimeServlet.java
    Port: 8081

    Cea de-a doua aplicație creată a fost un microserviciu Java care expune un singur endpoint /datetime. Aceasta <br/> aplicație rulează pe serverul Jetty și returnează data și ora curentă sub formă de text simplu (text/plain).

     Pentru setup-ul acestei aplicatii a fost nevoie sa creez un proiect in Intellij IDEA de tipul Maven Acrhetype <br/> de tipul maven-archetype-webapp. Pentru dependinte am completat in fisierul pom.xml :

    `<dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>5.0.0</version>
        <scope>provided</scope>
    </dependency>`

    Am folosit <scope>provided</scope> pentru a nu include aceasta dependinta in WAR-ul final (Tomcat contine deja servlet-api).<br/>
    Pentru setup-ul acestei aplicații a fost nevoie să creez un Jetty base denumit jetty_tpjad_base și să plasez WAR-ul <br/> aplicației în directorul webapps al acestui base. Tot in interiorul acestui base am creat un fisier <br/> denumit start.ini care contine modulele deploy, http, si server, necesare pentru initializarea connector-ului care <br/> asculta pe portul 8081 si directioneaza request-urile spre servlet. Acest server rulează pe portul 8080, <br/> dar portul poate fi modificat prin adăugarea proprietății <port>.
    
    Serverul Jetty a fost pornit folosind linia de comandă:

    ```cd jetty_tpjad_base```
    ```java -Djetty.port=8081 -jar ../start.jar```

    Aplicația conține clasa DateTimeServlet.java în pachetul com.sa.tpjad.jetty, care implementează metoda doGet astfel:

    ```
    public class DateTimeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("text/plain");
            String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            resp.getWriter().write(now);
        }
    }
    ```

    Endpoint-ul /datetime este mapat în web.xml astfel încât Jetty să știe ce servlet să ruleze:
```
    <servlet>
        <servlet-name>DateTimeServlet</servlet-name>
        <servlet-class>com.sa.tpjad.jetty.DateTimeServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>DateTimeServlet</servlet-name>
        <url-pattern>/datetime</url-pattern>
    </servlet-mapping>
```
    In aceasta aplicatie nu am folosit adnotari, deci a fost necesara maparea in web.xml

    Astfel, atunci când aplicația este accesată prin URL-ul:

    http://localhost:8081/JettyServletApp/datetime





    

        
