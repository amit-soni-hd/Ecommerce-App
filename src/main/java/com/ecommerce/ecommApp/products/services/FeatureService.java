package com.ecommerce.ecommApp.products.services;

import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.elasticsearch.ElasticSearchUtil;
import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.products.elasticsearch.ElasticsearchObject;
import com.ecommerce.ecommApp.products.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureService {

    @Autowired
    private ProductRepository productRepository;

    ElasticSearchUtil elasticSearchUtil = ElasticsearchObject.getElasticsearchObject();

    /**
     * This method is used to return all the items of a particular category.
     *
     * @param category The category
     * @return It returns a list of all products of a particular category.
     */

    public List<Product> getByCategory(String category) {
        List<Product> sortedProducts = productRepository.getElementsByCategory(category);
        return sortedProducts;
    }

    /**
     * This method is responsible for sending the search query to the Elasticsearch Util class where it forwards the query
     * to search the products using elastic search.
     *
     * @param searchQuery The json body which is sent from the postman UI to search.
     * @return The list of products returned from the elasticsearch.
     * @throws ElementNotFoundException
     */
    @Cacheable("product")
    public List<Product> getSearchedElements(String searchQuery) throws ElementNotFoundException, InterruptedException {
        List<Product> searchedProducts = elasticSearchUtil.searchProduct(searchQuery);
        if (searchedProducts.size() != 0) {
            System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
            Thread.sleep(1000 * 5);
            return searchedProducts;
        } else {
            throw new ElementNotFoundException(" No elements found for the given search");
        }
    }
}








//package com.ecommerce.ecommApp.products.services;
//
//import com.ecommerce.ecommApp.commons.pojo.products.Product;
//import com.ecommerce.ecommApp.products.ElasticSearchUtil;
//import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
//import com.ecommerce.ecommApp.products.repositories.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class FeatureService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    /**
//     * This method is used to return all the items of a particular category.
//     *
//     * @param category The category
//     * @return It returns a list of all products of a particular category.
//     */
//
//    public List<Product> getByCategory(String category) {
//        List<Product> sortedProducts = productRepository.getElementsByCategory(category);
//        return sortedProducts;
//    }
//
//
//    /**
//     * This method is responsible for sending the search query to the Elasticsearch Util class where it forwards the query
//     * to search the products using elastic search.
//     *
//     * @param searchQuery The json body which is sent from the postman UI to search.
//     * @return The list of products returned from the elasticsearch.
//     * @throws ElementNotFoundException
//     */
//
//    @Cacheable("product")
//    public List<Product> getSearchedElements(String searchQuery) throws ElementNotFoundException, InterruptedException {
//        List<Product> searchedProducts = ElasticSearchUtil.searchProduct(searchQuery);
//        if (searchedProducts.size() != 0) {
//            System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
//            Thread.sleep(1000 * 5);
//            return searchedProducts;
//        } else {
//            throw new ElementNotFoundException(" No elements found for the given search");
//
//        }
//    }
//}
