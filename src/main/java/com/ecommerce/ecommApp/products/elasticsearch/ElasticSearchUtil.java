package com.ecommerce.ecommApp.products.elasticsearch;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.Util.Communication;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to call the Request methods of the Elasticsearch.
 * It is used to format the data in the manner which is accepted by the Request methods.
 * @Author Sagar Bindal and Fatema Sagar.
 */
public class ElasticSearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchUtil.class);
    private static final String INET_ADDRESS = "http://localhost:9200";
    private static final String _INDEX = "products";
    private static final String _TYPE = "_doc";
    private static final String ENDPOINT = "%s/%s/%s/%s";

    /**
     * This method is called from the ProductService while adding the product to the database,
     * it also adds the product to the Elasticsearch.
     * @param product The product object which has to be added to the elasticsearch.
     * @return It returns a boolean value whether the object is added to elasticsearch or not.
     */
    public boolean insertProduct(Product product) {
        try {
            ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
            String json = objectMapper.writeValueAsString(product);
            String endpoint = String.format(ENDPOINT, INET_ADDRESS, _INDEX, _TYPE, product.getProductId());
            logger.info("Data inserted in elastic search : {} : {} " + endpoint, json);
            String response = Communication.sendHttpRequest(endpoint, json, RequestMethod.POST);
            logger.trace("Response : {} ", response);
            return true;
        } catch (Exception ex) {
            logger.error("Error in inserting data into the elastic seach : {} ", ex);
            return false;
        }
    }

    /**
     * This method is called from the ProductService each time the product is updated in the database.
     * @param product The existing Product which has to be updated in elasticsearch.
     * @return It returns a boolean value whether the object is updated to elasticsearch or not.
     */
    public boolean updateProduct(Product product) {
        try {
            ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
            String productJson = objectMapper.writeValueAsString(product);
            String jsonBody = String.format("{\"doc\":%s}", productJson);
            String endpoint = String.format(ENDPOINT, INET_ADDRESS, _INDEX, "_update", product.getProductId());
            Communication.sendHttpRequest(endpoint, jsonBody, RequestMethod.POST);
            logger.info("Data updated in elastic seach for product {}", product.getProductId());
            return true;
        } catch (JsonProcessingException ex) {
            logger.error("Error in Updating data into the Elastic seach : {} ", ex);
            return false;
        }
    }

    /**
     * This method is used to delete a product from the elasticsearch.
     * @param product_id The product which has to be deleted from the elasticsearch identity.
     * @return It returns a boolean value whether the object is deleted from elasticsearch or not.
     */
    public boolean deleteProduct(long product_id) {
        String endpoint = String.format(ENDPOINT, INET_ADDRESS, _INDEX, _TYPE, product_id);
        try {
            String response = Communication.sendDeleteRequest(endpoint);
            logger.trace("product deted for id : {} ", product_id);
            return true;
        } catch (Exception ex) {
            logger.error("Error in Deleting prouct for Product ID {} ", ex);
            return false;
        }
    }

    /**
     * This method is used to return all the Products added to the elasticsearch.
     * @return List of all Products available in Elasticsearch.
     */
    public List<Product> getAllProducts() {
        List<Product> allProducts;
        String endPoint = String.format("%s/%s/%s?q=*", INET_ADDRESS, _INDEX, "_search");
        String response = Communication.sendGetRequest(endPoint,null);
        allProducts = extractFromResponse(response);
        return allProducts;
    }

    /**
     * This method is used to search the json body sent for returning the specific product(s).
     * @param jsonBody The String formatted json body to search in elastic search.
     * @return List of Products matching the search text or else returns null.
     */
    public List<Product> searchProduct(String jsonBody) throws ElasticsearchException {
        List<Product> allProducts = new ArrayList<>();
        try {
            logger.info("Searching all products");
            String endpoint = String.format("%s/%s/%s", INET_ADDRESS, _INDEX, "_search");
            QueryBuilder queryBuilder = new QueryBuilder(jsonBody);
            jsonBody = queryBuilder.build();
            String response = Communication.sendHttpRequest(endpoint, jsonBody, RequestMethod.GET);
            allProducts = extractFromResponse(response);
            return allProducts;
        } catch (Exception ex) {
            throw new ElasticsearchException("Elasticsearch is not running "+ ex.getMessage());
        }
    }

    /**
     * This method is used to extract the response sent from the Elasticsearch in the form of a response.
     * @usages It is used in searchProduct() and in getAllProducts() to return a list of Products.
     * @param response The response received from elasticsearch.
     * @return List of Products extracted from the response.
     */
    private static List<Product> extractFromResponse(String response) {
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        List<Product> allProducts = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                object = object.getJSONObject("_source");
                String productJson = object.toString();
                Product product = objectMapper.readValue(productJson, Product.class);
                System.out.println(product);
                allProducts.add(product);
            }
            return allProducts;
        } catch (JSONException | JsonProcessingException e) {
            logger.error("JSON Error"+ e.getMessage());
            return null;
        }
    }

}