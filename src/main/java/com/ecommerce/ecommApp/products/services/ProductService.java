package com.ecommerce.ecommApp.products.services;

import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.elasticsearch.ElasticSearchUtil;
import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.commons.exceptions.NotEnoughQuantityException;
import com.ecommerce.ecommApp.products.elasticsearch.ElasticsearchObject;
import com.ecommerce.ecommApp.products.repositories.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    ProductRepository productRepository;

    ElasticSearchUtil elasticSearchUtil = ElasticsearchObject.getElasticsearchObject();

    public List<Product> getProductsList() {
        return productRepository.findAll();
    }

    /**
     * The method is used to add the Product in the database and later add the product to the Elasticsearch.
     *
     * @param product The product sent from the user.
     * @return The generated product added to the database.
     */
    public Product createProduct(Product product) {
        try {
            logger.info("Adding the following Product {} to the db..", product);
            Product generatedProduct = productRepository.save(product);
            elasticSearchUtil.insertProduct(generatedProduct);
            return generatedProduct;
        } catch (ElasticsearchException ex) {
            throw new ElasticsearchException("Seems like elastic search is not up !! " +
                    "Kindly run the docker image to continue adding to the Elastic search.");
        }

    }

    /**
     * Updates the product sent by the user only if the Product is already available in the database.
     *
     * @param product The Product object sent by the user.
     * @return The generated product after updating it.
     * @throws ElementNotFoundException In case the product is not found, returns an exception.
     */
    public Product updateProduct(Product product) throws ElementNotFoundException {
        if (productRepository.existsById(product.getProductId())) {
            logger.info("Updated Product: {}", product);
            elasticSearchUtil.updateProduct(product);
            return productRepository.save(product);
        } else {
            throw new ElementNotFoundException("Product ID is not available");
        }
    }

    /**
     * After an order is placed, this method is called to deduct the available quantity of the product.
     *
     * @param product The ItemsDTO object which contains the productID and quantity which has to be deducted from the db.
     * @throws ElementNotFoundException   In case the product is not found, returns an exception.
     * @throws NotEnoughQuantityException In case the product does not have the appropriate quantity which has to be reduced.
     */
    public void deductProducts(List<ItemsDTO> product) throws ElementNotFoundException, NotEnoughQuantityException {
        for (ItemsDTO element : product) {
            if (productRepository.existsById(element.getProductID())) {
                Product invent = productRepository.findById(element.getProductID()).get();
                if (invent.getQuantity() >= element.getQuantity()) {
                    invent.setQuantity(invent.getQuantity() - element.getQuantity());
                    productRepository.save(invent);
                    elasticSearchUtil.updateProduct(invent);
                } else {
                    throw new NotEnoughQuantityException("The product you are trying to update does not have enough quantity");
                }
            } else {
                throw new ElementNotFoundException("Element does not exist");
            }
        }
    }

    /**
     * This method is used to increase the quantity of an existing product. It updates the quantity in the db
     * and later updates it in the elasticsearch.
     *
     * @param product The Product object whose quantity has to be added in the existing products quantity.
     * @return The updated product.
     * @throws ElementNotFoundException In case the product is not found, returns an exception.
     */
    public Product increaseProductCount(Product product) throws ElementNotFoundException {
        if (productRepository.existsById(product.getProductId())) {
            Product existingProduct = productRepository.findById(product.getProductId()).get();
            existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());
            elasticSearchUtil.updateProduct(existingProduct);
            return productRepository.save(existingProduct);
        } else {
            throw new ElementNotFoundException("Unable to update the quantity for the product, as it is not available with the database.");
        }
    }

    /**
     * This method returns the list of all distinct categories.
     *
     * @return List of categories available in the database.
     */
    public List<String> getAllCategories() {
        return productRepository.getCategory();
    }

    /**
     * This method is used to get all details of a particular product using its Product ID.
     *
     * @param productId The product id sent from the API.
     * @return The Product with the given product id.
     * @throws ElementNotFoundException In case the product id is not found, returns an exception.
     */
    @Cacheable("product")
    public Product getProduct(long productId) throws ElementNotFoundException, InterruptedException {
        if (productRepository.existsById(productId)) {

            System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
            Thread.sleep(1000*5);
            return productRepository.findById(productId).get();
        } else {
            throw new ElementNotFoundException("ProductId not found");
        }
    }

    /**
     * This method is used to read the csv file to directly generate the products from the csv file.
     */
    public void generateProducts() {
        long id = 0;
        URL res = getClass().getClassLoader().getResource("flipkart_com-ecommerce_sample.csv");
        try (
                Reader reader = Files.newBufferedReader(Paths.get(res.toURI()));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        ) {
            for (CSVRecord csvRecord : csvParser) {
                if (id == 0) {
                    id++;
                    continue;
                }
                // Accessing Values by Column Index
                System.out.println(csvRecord.size() + " : " + csvRecord.toString());
                Product product = new Product();
                product.setProductId(id++);
                product.setName(csvRecord.get(3));
                product.setPrice((csvRecord.get(6).equals("")) ? 0 : Integer.parseInt(csvRecord.get(6)));
                product.setImage("Image Dummy");
                product.setProductDescription(csvRecord.get(10));
                String category = csvRecord.get(4).replace("[\"", "").replace("]", "");
                String text = category.split(">>")[0].trim();
                product.setCategory(text);
                Random random = new Random();
                product.setQuantity(random.nextInt(100));
                product.setSize("M");
                createProduct(product);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
