package org.online.chat;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.http.parser.Host;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.TimeZone;

public class Main {
    public final static Dotenv dotenv = Dotenv.configure().
            ignoreIfMissing().
            systemProperties().
            load();

    private static Connection conn = null;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // todo understand why it doesn't work without it
    }

    public static void main(String[] args) throws LifecycleException, URISyntaxException {
        String jarPath = Paths.get(
                Main.class.getProtectionDomain().
                        getCodeSource().
                        getLocation().
                        toURI()
        ).toFile().getAbsolutePath(); // todo

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(System.getenv().getOrDefault("PORT", "8080")));

        Context ctx = tomcat.addWebapp(tomcat.getHost(), "", "/");

        StandardRoot resources = new StandardRoot(ctx);
        resources.addJarResources(
                new JarResourceSet(
                        resources,
                        "/WEB-INF/classes",
                        jarPath,
                        "/"
                )
        );

        resources.addJarResources(
                new JarResourceSet(
                        resources,
                        "/",
                        jarPath,
                        "/webapp"
                )
        );

        ctx.setResources(resources);

        tomcat.enableNaming(); // todo
        tomcat.getConnector().setProperty("address", "0.0.0.0");

        tomcat.start();
        tomcat.getServer().await();
    }

    public static Connection getConn() throws SQLException {
        if (conn == null || !conn.isValid(1)) conn = createConnection();
        return conn;
    }

    public static Connection createConnection() throws SQLException {
        String jdbcUrl = dotenv.get("JDBC_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }
}