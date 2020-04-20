package com.eeyore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.eeyore.Utils.*;

@WebServlet(name = "DoSetStatusServlet", urlPatterns = {"/setStatus"})
public class DoSetStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getParameter("token");
            String ticketNumber = request.getParameter("ticketNumber");

            response.setStatus(setTicketToOpen(token,ticketNumber));
        } catch(Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    private int  setTicketToOpen(String token,String ticketNumber) throws IOException {

            URL url = new URL("http://localhost:9999/api/v2/tickets/" + ticketNumber);
            String authHeaderValue = getAuthHeader(token);
            String json = "{\"status\": 2}";

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Authorization", authHeaderValue);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            int retcode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            return retcode;
    }
}
