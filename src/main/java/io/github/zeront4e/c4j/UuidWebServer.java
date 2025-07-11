package io.github.zeront4e.c4j;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

class UuidWebServer extends NanoHTTPD {
    private static final Logger LOGGER = LoggerFactory.getLogger(UuidWebServer.class);

    private final UUID uuid;

    public UuidWebServer() {
        super(findFreePort());

        this.uuid = UUID.randomUUID();
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);

            return socket.getLocalPort();
        }
        catch (IOException exception) {
            LOGGER.error("Failed to find a free port. Use fallback port \"8589\".", exception);

            return 8589;
        }
    }

    /**
     * Returns a random UUID that is used to find the browser window.
     * @return The random UUID.
     */
    public String getUuid() {
        return uuid.toString();
    }

    /**
     * Returns the URL to the server-site, including the random generated port.
     * @return The URL to the server-site.
     */
    public String getUrl() {
        return "http://localhost:" + getListeningPort();
    }

    @Override
    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public void start(int timeout, boolean daemon) throws IOException {
        super.start(timeout, daemon);

        LOGGER.info("Local server started on port \"{}\". Generated UUID: {}", getListeningPort(), uuid);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("GET".equals(session.getMethod().name())) {
            String htmlResponse = generateHtmlResponse();

            return newFixedLengthResponse(Response.Status.OK, "text/html", htmlResponse);
        }
        else {
            return newFixedLengthResponse(Response.Status.FORBIDDEN, "text/html", "Invalid request.");
        }
    }

    private String generateHtmlResponse() {
        return """
                   <!DOCTYPE html>
                   <html lang="en">
                   <head>
                       <meta charset="UTF-8">
                       <meta name="viewport" content="width=device-width, initial-scale=1.0">
                       <title>%s</title>
                       <!-- Robot favicon using emoji as data URI -->
                       <link rel="icon" href="data:image/svg+xml,<svg xmlns=%%22http://www.w3.org/2000/svg%%22 viewBox=%%220 0 100 100%%22><text y=%%22.9em%%22 font-size=%%2290%%22>🤖</text></svg>">
                       <style>
                           body {
                               font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                               background-color: #121212;
                               color: #e0e0e0;
                               display: flex;
                               flex-direction: column;
                               justify-content: center;
                               align-items: center;
                               height: 100vh;
                               margin: 0;
                               padding: 0;
                               text-align: center;
                           }
                           h1 {
                               font-size: 2.5rem;
                               margin-bottom: 1rem;
                               color: #f9a232;
                           }
                           h2 {
                               font-size: 1.5rem;
                               margin-bottom: 1rem;
                               color: #f9a232;
                           }
                           h3 {
                               font-size: 1rem;
                               color: white;
                           }
                           .container {
                               background-color: #1e1e1e;
                               padding: 2rem;
                               border-radius: 2px;
                               box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
                               animation: fadeIn 0.5s ease-in;
                           }
                           @keyframes fadeIn {
                               from { opacity: 0; transform: translateY(-20px); }
                               to { opacity: 1; transform: translateY(0); }
                           }
                       </style>
                   </head>
                   <body>
                       <div class="container">
                           <h1>⏳ Please wait...</h1>
                           <h2>Do NOT close this window! This window is managed by chromium4j.</h2>
                           <h3>Browser ID: %s</h3>
                       </div>
                   </body>
                   </html>
                   """.formatted(uuid, uuid);
    }
}
