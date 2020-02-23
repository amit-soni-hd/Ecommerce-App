package com.ecommerce.ecommApp.recommendation.service;

import com.ecommerce.ecommApp.commons.Util.ElasticSearchQueryBuilder;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.kafka.consumer.FetchViewedProduct;
import com.ecommerce.ecommApp.kafka.stream.FetchViewProductsStream;
import com.ecommerce.ecommApp.products.ElasticSearchUtil;
import com.ecommerce.ecommApp.products.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.products.services.ProductService;
import com.ecommerce.ecommApp.view.dto.ViewProductDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecommendationService {

    @Autowired
    private FetchViewedProduct fetchViewedProduct;

    @Autowired
    private FetchViewProductsStream viewProductsStream;

    @Autowired
    private ProductService productService;


    public Set<Product> fetchRecommendedProduct(Long customerId) {

        List<Long> viewProductList;
        Set<Product> products = new HashSet<>();
        viewProductList = viewProductsStream.start(customerId);
//        viewProductList = fetchViewedProduct.getViewProduct(customerId);
        viewProductList.forEach(productId -> {
            try {
                Product product = productService.getProduct(Long.valueOf(productId));
                products.addAll(searchProductElasticSearch(product));
            } catch (ElementNotFoundException e) {
                log.warn("Element not fount ");
            }
        });
        return products;
    }

    private Set<Product> searchProductElasticSearch(Product product) {
        Set<Product> products = new HashSet<>();
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("productId", product.getProductId());
            products.addAll(ElasticSearchUtil.searchProduct(jsonObject.toString()));
            jsonObject.remove("productId");
            jsonObject.put("brand", product.getBrand());
            products.addAll(ElasticSearchUtil.searchProduct(jsonObject.toString()));
            jsonObject.put("category", product.getCategory());
            products.addAll(ElasticSearchUtil.searchProduct(jsonObject.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
       return products;
    }

}
