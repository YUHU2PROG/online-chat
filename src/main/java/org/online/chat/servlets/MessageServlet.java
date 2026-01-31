package org.online.chat.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.online.chat.models.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet({"/api/messages", "/api/messages/"})
public class MessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Message> messages = new ArrayList<>();
            Connection conn = ((Connection) req.getAttribute("conn"));
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM messages");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(new Message(rs.getString("name"),
                        rs.getString("message")));
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter writer = resp.getWriter()) {
                JsonObject result = new JsonObject();
                result.add("messages", new Gson().toJsonTree(messages));
                writer.write(result.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Message message;
            try (BufferedReader reader = req.getReader()) {
                message = new Gson().fromJson(reader.lines().collect(Collectors.joining()), Message.class);
            }

            if (message.getName() == null || message.getMessage() == null) throw new JsonSyntaxException("Message is incorrect");

            Connection conn = ((Connection) req.getAttribute("conn"));
            PreparedStatement ps = conn.prepareStatement("INSERT INTO messages (name, message) VALUES (?, ?)");
            ps.setString(1, message.getName());
            ps.setString(2, message.getMessage());
            ps.executeUpdate();

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter writer = resp.getWriter()) {
                JsonObject result = new JsonObject();
                result.addProperty("status", "success");
                writer.write(result.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter writer = resp.getWriter()) {
                JsonObject result = new JsonObject();
                result.addProperty("status", "error");
                result.addProperty("message", "Incorrect format");
                writer.write(result.toString());
            }
        }
    }
}
