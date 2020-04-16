package com.eeyore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import static com.eeyore.HelloServlet.*;

@WebServlet(name = "DoitServlet", urlPatterns = {"doit"})
public class DoitServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getParameter("token");
            String ticketNumber = request.getParameter("ticketNumber");
            String email = request.getParameter("email");
            String messageBody = request.getParameter("messageBody");

            sendMail(ticketNumber,email,messageBody);
            FDTicket updatedTicket = setTicketToOpen(token,ticketNumber);
            FDContact requester = getContact(token,updatedTicket.getRequester_id());

            response.setContentType("text/html;charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();
            buildHelloScreen(out,token,updatedTicket,requester);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMail(String ticketNumber,String email,String messageBody) throws MessagingException {

            Properties prop = System.getProperties();
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.port", "25");
            prop.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(prop, null);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("eeyoreOnFD@patientsknowbest.com"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            msg.setSubject("FD-"+ticketNumber);
            msg.setText(messageBody);
            msg.setSentDate(new Date());

            SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "steve@patientsknowbest.com", "hjdhynmquhwpgyau");
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

    }

    private FDTicket setTicketToOpen(String token,String ticketNumber) throws IOException {

            URL url = new URL("https://pkb.freshdesk.com/api/v2/tickets/" + ticketNumber);
            String authHeaderValue = getAuthHeader(token);
            String json = "{\"status\": 2}";

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Authorization", authHeaderValue);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            InputStream stream = urlConnection.getInputStream();

            Gson gson = new Gson();
            return gson.fromJson(readWholeStream(stream),new TypeToken<FDTicket>(){}.getType());
    }
}
