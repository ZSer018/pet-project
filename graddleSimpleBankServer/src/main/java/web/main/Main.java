package web.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import web.servlet.SignInServlet;
import web.servlet.SignUpServlet;

public class Main {

    public static void main(String[] args) throws Exception {

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new SignInServlet()), "/signin");
        servletContextHandler.addServlet(new ServletHolder(new SignUpServlet()), "/signup");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("pages");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers( new Handler[]{resourceHandler, servletContextHandler} );

        Server server = new Server(8080);
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
