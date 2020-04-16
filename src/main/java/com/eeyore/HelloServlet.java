package com.eeyore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = request.getParameter("token");
        String ticketNumber = request.getParameter("ticketNumber");

        FDTicket ticket = getTicket(token,ticketNumber);
        FDContact requester = getContact(token,ticket.getRequester_id());

        response.setContentType("text/html;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        buildHelloScreen(out,token,ticket,requester);
    }

    private FDTicket getTicket(String token, String ticketNumber) throws IOException {

        URL url = new URL("https://pkb.freshdesk.com/api/v2/tickets/"+ticketNumber);
        String authHeaderValue = getAuthHeader(token);

        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", authHeaderValue);
        InputStream stream = urlConnection.getInputStream();

        Gson gson = new Gson();
        return gson.fromJson(readWholeStream(stream),new TypeToken<FDTicket>(){}.getType());
    }

    public static FDContact getContact(String token, String contactNumber) throws IOException {
        URL url = new URL("https://pkb.freshdesk.com/api/v2/contacts/"+contactNumber);
        String authHeaderValue = getAuthHeader(token);

        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", authHeaderValue);
        InputStream stream = urlConnection.getInputStream();

        Gson gson = new Gson();
        return gson.fromJson(readWholeStream(stream),new TypeToken<FDContact>(){}.getType());
    }

    public static void buildHelloScreen(ServletOutputStream out, String token, FDTicket ticket, FDContact requester) throws IOException {
        out.println("<html>");
        out.println("   <p> Ticket: "+ticket.getId() + "</p>");
        out.println("   <p> Requester: "+requester.getName() + "</p>");
        out.println("   <p> Status: "+getStatusString(ticket.getStatus())+ "</p>");
        out.println("   <p> Subject: "+ ticket.getSubject());
        out.println("   <form method='POST' action='/doit'>");
        out.println("      <input type='hidden' name='token' value='"+token+"'/>");
        out.println("      <input type='hidden' name='ticketNumber' value='"+ticket.getId()+"'/>");
        out.println("      <input type='hidden' name='email' value='"+requester.getEmail()+"'/>");
        out.println("      <p><textarea name='messageBody' rows='5' cols='50' placeholder='Message for originator'></textarea></p>");
        out.println("      <p><input type='submit' value='Send Message and Mark Open'/></p>");
        out.println("   </form>");
        out.print("</html>");
    }

    public static String getStatusString(int status) {
        switch(status) {
            case 2: return "OPEN";
            case 3: return "PENDING";
            case 4: return "RESOLVED";
            case 5: return "CLOSED";
            case 9: return "WAITING ON DEVELOPER";
        }
        return "UNKNOWN";
    }

    public static String getAuthHeader(String token) {
        String auth = token + ":X";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedAuth;
    }

    public static String readWholeStream(InputStream stream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        int charsRead;
        while((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }


}