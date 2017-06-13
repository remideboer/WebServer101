package nl.remideboer.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Remi
 */
public class WebServer {

    private static final Logger logger = Logger.getLogger(WebServer.class.getName());
    private static final String RESPONSE_204_NO_CONTENT = "HTTP/1.1 204 No Content\r\nContent-Length: 0\r\n\r";
    private static final String RESPONSE_405_METHOD_NOT_ALLOWED = "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
    private static final String HEADER_CONTENT_TYPE_TEXT_HTML = "Content-Type: text/html";
    private final String DEFAULT_RESOURCE_FILE = "index.html";
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            new WebServer().start();
        } catch (IOException ex) {
            // can't connect
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void start() throws IOException {
        logger.setLevel(Level.FINEST);
        serverSocket = new ServerSocket(80);
        logger.log(Level.INFO, "server listening on port 80");

        // start server loop
        while (true) {
            Socket clientSocket = serverSocket.accept();

            try (InputStream instream = clientSocket.getInputStream();) {
                Request request = null;
                try {
                    request = new Request(instream);

                    //if GET request
                    // read bytes dan kun je stoppen met \r\n\r\n
                    // bij ifs state dependent reader via interface
                    // nu nog erg procedureel, mss request/body parsing in constructor Request
                    if (request.getRequestMethod() == RequestMethod.GET || request.getRequestMethod() == RequestMethod.POST) {

                        if (!clientSocket.isClosed()) {

                            String out = "";
                            String outString = "";
                            try {
                                out = readFile(request.getResource());
                                outString = "HTTP/1.1 200 OK\r\n" + HEADER_CONTENT_TYPE_TEXT_HTML + "\r\n\r\n" + out;
                            } catch (Exception e) {
                                outString = "HTTP/1.1 404 Not Found\r\n" + HEADER_CONTENT_TYPE_TEXT_HTML + "\r\n\r\n";
                            } finally {
                                clientSocket.getOutputStream().write(outString.getBytes("UTF-8"));
                            }

                        }
                    }
                } catch (MethodNotAllowedException ex) {
                    String fileString = RESPONSE_405_METHOD_NOT_ALLOWED + ex.getMessage();
                    clientSocket.getOutputStream().write(fileString.getBytes("UTF-8"));
                }

                clientSocket.close();

            } catch (IOException ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }// catch nog te doen

        }
    }

    private String readFile(String resource) throws Exception {

        if (resource.equals("/")) {
            resource = DEFAULT_RESOURCE_FILE;
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        // gooit null als niet gevonden iets veranderen want nu vangt deze alles af
        File file = new File(classLoader.getResource("pages/" + resource).getFile());

        StringBuilder sb = new StringBuilder();

        try (FileReader fileReader = new FileReader(file); BufferedReader bufreader = new BufferedReader(fileReader);) {

            String line = null;
            while ((line = bufreader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
