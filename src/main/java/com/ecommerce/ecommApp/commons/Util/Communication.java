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

/**
 * This class is required to establish connection with the elasticsearch and provide primary communication
 * of GET, POST, PUT and DELETE methods with the elasticsearch.
 */
public class Communication {

  private static final Logger logger = LoggerFactory.getLogger(Communication.class);

  /**
   * This method is used to send a Get Request to the elasticsearch to return all the elements.
   * This method does not require any body to fetch the data from elasticsearch.
   * @param endpoint The elasticsearch URL to read the data from.
   * @return It returns a response from the elastic search.
   */
  public static String sendGetRequest(String endpoint, String jwtToken) {
    try {
      logger.info("Sending request for GET.");
      URL url = new URL(endpoint);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(RequestMethod.GET.toString());
      logger.info("Sending request to "+endpoint);
      if(jwtToken!=null)
        con.setRequestProperty("Authorization",jwtToken);
      int responseCode = con.getResponseCode();
      logger.info("response code : " + responseCode);
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
    } catch (IOException io) {
      logger.error("An IOException occurred while receiving response from the Endpoint" +
              "{}", io.getMessage()+" "+endpoint);
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
      logger.error("An IOException occurred while receiving response from the Elasticsearch" +
              "{}", io.getMessage());
      return null;
    }
  }

  /**
   * This method is used to delete a entity from the provided index of the elastic search.
   * This method does not require the body and simply needs an ID to delete an entity.
   * @param endpoint The request URL for DELETE method.
   * @return Returns a response message.
   */
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
    } catch (IOException io) {
      logger.error("IOException occurred in DELETE request" + io.getMessage());
      return null;
    }
  }

  /**
   * This method is required to establish a connection to the elasticsearch server.
   * @return The Host address.
   * @throws Exception In case the host is not available throws an UnknownHostException.
   */
  public static String getApplicationAddress() throws Exception {
    try {
      InetAddress address = InetAddress.getLocalHost();
      return "localhost"+":" + EcommAppApplication.environment.
              getRequiredProperty(CommonsUtil.SERVER_PORT);
    } catch (UnknownHostException ex) {
      throw new Exception("Host is not available");
    }
  }

}

