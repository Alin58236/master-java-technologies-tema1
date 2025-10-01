# master-java-technologies-tema1

Acest repository contine 3 aplicatii Servlet pe 3 medii diferite: Tomcat, Jetty, Wildfly



# a. TomcatServletApp

Nume: TomcatServletApp

Clasa: HelloServlet.java

Port:8080

Prima aplicatie creata a fost o aplicatie Java care expune un singur endpoint "/hello". Aceasta aplicatie a fost testata pe serverul Tomcat pe portul 8080, iar ca raspuns aceasta returneaza textul "Salut! Este ora `data si ora curenta preluate din microserviciul Jetty`". In cazul in care cel de-al doilea microserviciu nu este activ pe portul 8081 aplicatia va returna utilizatorului un text default, in cazul de fata "Habar n-am".

Pentru setup-ul acestei aplicatii a fost nevoie sa creez un proiect in Intellij IDEA de tipul Maven Acrhetype de tipul maven-archetype-webapp. Pentru dependinte am completat in fisierul pom.xml :

```
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

Am folosit `<scope>provided</scope>` pentru a nu include aceasta dependinta in WAR-ul final (Tomcat contine deja servlet-api).

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
Aceasta clasa expune endpoint-ul /hello prin intermediul adnotarii @WebServlet("/hello"). Cand este apelata, face un apel API la urmatorul serviciu pentru a extrage data si ora curenta si returneaza un sir de caractere care sa contina acest raspuns.

Datorita faptului ca am folosit adnotarea pentru expunerea endpoint-ului, singurul lucru pe care l-am scris in web.xml a fost doar legatura dintre numele servlet-ului (HelloServlet) si clasa care contine logica din spatele API call-ului (com.sa.tpjad.tomcat.HelloServlet).

### Configurare Tomcat

Pentru pornirea acestei aplicatii pe Tomcat trebuie compilat proiectul de maven utilizand comanda `mvn clean install`. 

In urma acesteia rezulta fisierul TomcatServletApp.war care trebuie copiat in fisierul webapps din directorul in care se afla tomcat. Dupa copiere, daca rulam start.bat din tomcat/bin serverul va rula si vom putea apela endpoint-ul http://localhost:8080/TomcatServletApp/hello .



# b. JettyServletApp

Nume: JettyServletApp

Clasa: DateTimeServlet.java

Port: 8081

Cea de-a doua aplicație creată a fost un microserviciu Java care expune un singur endpoint /datetime. Aceasta aplicație rulează pe serverul Jetty și returnează data și ora curentă sub formă de text simplu (text/plain).

Pentru setup-ul acestei aplicatii a fost nevoie sa creez un proiect in Intellij IDEA de tipul Maven Acrhetype de tipul maven-archetype-webapp. Pentru dependinte am completat in fisierul pom.xml :
```
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

Am folosit `<scope>provided</scope>` pentru a nu include aceasta dependinta in WAR-ul final (Tomcat contine deja servlet-api).

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

### Configurare Jetty

Pentru setup-ul acestei aplicații a fost nevoie să creez un Jetty base denumit jetty_tpjad_base și să plasez WAR-ul aplicației în directorul webapps al acestui base. Tot in interiorul acestui base am creat un fisier denumit start.ini care contine modulele deploy, http, si server, necesare pentru initializarea connector-ului care asculta pe portul 8081 si directioneaza request-urile spre servlet. Acest server rulează pe portul 8080, dar portul poate fi modificat prin adăugarea proprietății -Djetty.port `<port>`.

Serverul Jetty a fost pornit folosind linia de comandă:

```
cd jetty_tpjad_base
java -Djetty.port=8081 -jar ../start.jar
```

Astfel, aplicația pornita poate fi accesată prin URL-ul:

http://localhost:8081/JettyServletApp/datetime



# c. WildflyEJBApp

Nume: WildflyEJBApp (WildflyEJBApp-1.1-SNAPSHOT redenumit pentru deployment url)

Clasa: ButtonServlet.java + index.jsp

Port: 8082

Aceasta aplicație este un microserviciu Java EE care rulează pe serverul WildFly și expune diverse endpoint-uri prin EJB-uri și Servlets. În exemplul nostru, aplicația a fost testată printr-un simplu EJB expus, iar răspunsul include date preluate din `TomcatServletApp`.

Proiectul a fost creat în IntelliJ IDEA folosind un Maven Archetype pentru maven-archetype-webapp.

Dependințele relevante în pom.xml:

```
    <dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>9.1.0</version>
    <scope>provided</scope>
    </dependency>

```

`<scope>provided</scope>` este folosit pentru că WildFly conține deja API-urile Jakarta EE.

Aplicatia poate fi accesata direct in root endpoint la adresa http://localhost:8082/WildflyEJBApp. In momentul in care accesam acest URL raspunsul la GET request din partea wildfly il reprezinta pagina index.jsp:

```
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
```

Comunicarea cu celelalte microservicii se face prin intermediul butonului `Ce zi e azi?`. Acest buton are in spate un script de JS care apeleaza endpoint-ul /GET/[http://localhost:8082/WildflyEJBApp/butonCareCheamaTomcat](http://localhost:8082/WildflyEJBApp/butonCareCheamaTomcat). Acest apel este preluat de catre servlet-ul `butonCareCheamaTomcat` definit in clasa `ButtonServlet` :

```

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

```

Aceasta clasa face request-ul propriu-zis catre TomcatServletApp si trimite raspunsul primit de la Tomcat la codul JS, care mai departe va modifica textul din variabila `${message}` din JSP, astfel afisand data si ora curenta in UI.


### Configurare WildFly

Am folosit WildFly 19.1.0.Final.

Aplicația este împachetată ca WAR: WildFlyEJBApp-1.1-SNAPSHOT.war.

WAR-ul trebuie copiat în directorul standalone/deployments al WildFly.

În standalone.xml, asigurați-vă că nu mai există `<deployment>` vechi pentru versiuni anterioare. (2 ore am stat...)

Pentru a porni serverul de wildfly:
```
    cd %WILDFLY_HOME%\bin
    standalone.bat -Djboss.http.port=8082
```
        
