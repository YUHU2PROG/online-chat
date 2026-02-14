package org.online.chat;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Objects;

public class Main {
    public final static Dotenv dotenv = Dotenv.configure().
            ignoreIfMissing().
            systemProperties().
            load();

    public static void main(String[] args) throws LifecycleException, URISyntaxException {
        String jarPath = Paths.get(
                Main.class.getProtectionDomain().
                        getCodeSource().
                        getLocation().
                        toURI()
        ).toFile().getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(dotenv.get("PORT", "8080")));
        tomcat.getConnector().setProperty("address", "0.0.0.0");

        Context ctx;
        WebResourceRoot resources;

        if (Objects.equals(dotenv.get("CONTEXT"), "dev")) {
            ctx = tomcat.addWebapp("", new File("src/main/webapp/").getAbsolutePath());
            resources = new StandardRoot(ctx);
            resources.addPreResources(
                    new DirResourceSet(resources, "/WEB-INF/classes",
                            new File("target/classes").getAbsolutePath(), "/")
            );
        } else if (Objects.equals(dotenv.get("CONTEXT"), "prod")) {
            ctx = tomcat.addWebapp(tomcat.getHost(), "", "/");
            resources = new StandardRoot(ctx);
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
        } else {
            throw new IllegalArgumentException("CONTEXT should be set to dev in the development environment and prod in production.");
        }

        ctx.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
    }

    public static Connection getConn() throws SQLException {
        String jdbcUrl = dotenv.get("JDBC_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }
}