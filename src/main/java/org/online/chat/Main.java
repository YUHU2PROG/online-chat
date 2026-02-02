package org.online.chat;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.sql.*;
import java.util.TimeZone;

public class Main {
    private final static Dotenv dotenv = Dotenv.configure().systemProperties().ignoreIfMissing().load();
    private static Connection conn = null;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // todo understand why it doesn't work without it
    }

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(System.getenv().getOrDefault("PORT", "8080")));

        Context ctx = tomcat.addWebapp("", new File("src/main/resources/webapp").getAbsolutePath());

        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes",
                        new File("target/classes").getAbsolutePath(), "/")
        );
        ctx.setResources(resources);

        tomcat.enableNaming();
        tomcat.getConnector();

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