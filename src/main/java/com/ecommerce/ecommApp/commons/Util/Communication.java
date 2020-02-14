package com.ecommerce.ecommApp.commons.Util;

import com.ecommerce.ecommApp.EcommAppApplication;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Communication {

  private static final Logger logger = LoggerFactory.getLogger(Communication.class);

  public static String sendGetRequest(String endpoint) {
    try {
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

  public static String sendGetForSearchRequest(String endpoint, String json, RequestMethod method) {
    logger.info("Sending search request to the Elasticsearch...");
    try {
      String filteredProduct = "";
      URL url = new URL(endpoint);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(method.toString());
      con.setRequestProperty("Content-Type", "application/json; utf-8");
      con.setRequestProperty("Accept", "application/json");
      con.setDoOutput(true);
      try (OutputStream os = con.getOutputStream()) {
        byte[] input = json.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      return filteredProduct;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return "Malformed url exception";
    } catch (IOException e) {
      e.printStackTrace();
      return "IOException occurred";
    }
  }

  public static String sendHttpRequest(String endpoint, String json,RequestMethod method) {
    try {
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
      URL url = new URL(endpoint);
      HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
      httpCon.setDoOutput(true);
      httpCon.setRequestProperty(
              "Content-Type", "application/x-www-form-urlencoded");
      httpCon.setRequestMethod("DELETE");
      httpCon.connect();
      return httpCon.getResponseMessage();
    } catch (IOException ex) {
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

