package com.ecommerce.ecommApp.commons.Util;

import com.ecommerce.ecommApp.EcommAppApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class Communication {

  private static final Logger logger = LoggerFactory.getLogger(Communication.class);

  public static String sendGetRequest(String endpoint) {
    try {
      logger.info("Sending request for GET.");
      URL url = new URL(endpoint);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(RequestMethod.GET.toString());
      int responseCode = con.getResponseCode();
      System.out.println("response code : " + responseCode);
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        return response.toString();
      }
    } catch (IOException ex) {

    }
    return null;
  }

  /**
   *
   * @param endpoint The URL at which the Search query should hit the elastic search.
   * @param json The jsonBody Which should be sent with the URL
   * @param method The particular RequestMethod (POST, PUT, DELETE, GET) to be used with the given URL and JSON body.
   *               All these are sent with a particular body to the elastic search.
   * @return Returns a response to the calling method with JSON objects in the form of String.
   */
  public static String sendHttpRequest(String endpoint, String json,RequestMethod method) {
    try {
      logger.info("Sending HTTP Request for {} method", method);
      URL url = new URL(endpoint);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();

      switch(method.toString())
      {
        case "POST":
          con.setRequestMethod(RequestMethod.POST.toString());
          break;
        case "PUT" :
          con.setRequestMethod(RequestMethod.PUT.toString());
          break;
        case "DELETE" :
          con.setRequestMethod(RequestMethod.DELETE.toString());
          break;
        case "GET" :
          con.setRequestMethod(RequestMethod.GET.toString());
          break;
      }
      con.setRequestProperty("Content-Type", "application/json; utf-8");
      con.setRequestProperty("Accept", "application/json");
      con.setDoOutput(true);
      try (OutputStream os = con.getOutputStream()) {
        byte[] input = json.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      try (BufferedReader br = new BufferedReader(
              new InputStreamReader(con.getInputStream(), "utf-8"))) {
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        return response.toString();
      }
    } catch (IOException io) {
      return null;
    }
  }

  public static String sendDeleteRequest(String endpoint) {
    try {
      logger.info("Sending DELETE request to elasticsearch");
      URL url = new URL(endpoint);
      HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
      httpCon.setDoOutput(true);
      httpCon.setRequestProperty(
              "Content-Type", "application/x-www-form-urlencoded");
      httpCon.setRequestMethod("DELETE");
      httpCon.connect();
      return httpCon.getResponseMessage();
    } catch (IOException ex) {
      logger.error("IOException occurred in DELETE request" + ex.getMessage());
      return null;
    }
  }

  public static String getApplicationAddress() throws Exception {
    try {
      InetAddress address = InetAddress.getLocalHost();
      return address.getHostAddress() + ":" + EcommAppApplication.environment.
              getRequiredProperty(CommonsUtil.SERVER_PORT);
    } catch (UnknownHostException ex) {
      throw new Exception("Host is not available");
    }
  }
  public static void main(String[] g) {
//        System.out.println(sendGetRequest("https://jsonplaceholder.typicode.com/todos/2"));
//        System.out.println(sendPostRequest("https://jsonplaceholder.typicode.com/posts","{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}"));
//          System.out.println(sendHttpRequest("https://jsonplaceholder.typicode.com/posts","{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}"
//                ,RequestMethod.POST));
  }

}

