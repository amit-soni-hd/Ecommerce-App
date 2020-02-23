package com.ecommerce.ecommApp.products.services;

import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.ElasticSearchUtil;
import com.ecommerce.ecommApp.products.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.products.exceptions.NotEnoughQuantityException;
import com.ecommerce.ecommApp.products.repositories.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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

    public List<Product> getProductsList() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        logger.info("Adding the following Product {} to the db..", product);
        Product generatedProduct = productRepository.save(product);
        ElasticSearchUtil.insertProduct(generatedProduct);
        return generatedProduct;
    }

    public Product updateProduct(Product product) throws ElementNotFoundException {
        if (productRepository.existsById(product.getProductId())) {
            logger.info("Updated Product: {}", product);
            ElasticSearchUtil.updateProduct(product);
            return productRepository.save(product);
        } else {
            throw new ElementNotFoundException("Product ID is not available");
        }
    }

    public void deductProducts(List<ItemsDTO> product) throws ElementNotFoundException, NotEnoughQuantityException {
        for (ItemsDTO element : product) {
            if (productRepository.existsById(element.getProductID())) {
                Product invent = productRepository.findById(element.getProductID()).get();
                if (invent.getQuantity() >= element.getQuantity()) {
                    invent.setQuantity(invent.getQuantity() - element.getQuantity());
                    productRepository.save(invent);
                    ElasticSearchUtil.updateProduct(invent);
                } else {
                    throw new NotEnoughQuantityException("The product you are trying to update does not enough quantity");
                }
            } else {
                throw new ElementNotFoundException("Element does not exist");
            }
        }
    }

    public Product increaseProductCount(Product product) throws ElementNotFoundException {
        if (productRepository.existsById(product.getProductId())) {
            Product existingProduct = productRepository.findById(product.getProductId()).get();
            existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());
            ElasticSearchUtil.updateProduct(existingProduct);
            return productRepository.save(existingProduct);
        } else {
            throw new ElementNotFoundException("Unable to update the quantity for the product, as it is not available with the database.");
        }
    }

    public List<String> getAllCategories() {
        return productRepository.getCategory();
    }

    public Product getProduct(long productId) throws ElementNotFoundException {
        if (productRepository.existsById(productId)) {
            return productRepository.findById(productId).get();
        } else {
            throw new ElementNotFoundException("ProductId not found");
        }
    }

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
