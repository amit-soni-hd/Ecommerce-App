package com.ecommerce.ecommApp.recommend.recommendation.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.services.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private ProductService productService;

    @Mock
    private FetchViewProductsStream viewProductsStream;

    private Objects objects;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();
    }

    @Test
    public void fetchRecommendedProductTest() throws ElementNotFoundException, InterruptedException {
        Product product = objects.getProduct();

        List<Long> viewProductList = new ArrayList<>();
        viewProductList.add(1L);

        when(viewProductsStream.start(anyLong())).thenReturn(viewProductList);
        when(productService.getProduct(anyLong())).thenReturn(product);

        Set<Product> products = recommendationService.fetchRecommendedProduct(1L);

        verify(productService, times(1)).getProduct(anyLong());
        assertTrue(products.size() > 0);
    }

    @Test
    public void fetchRecommendedProductExceptionTest_1() throws ElementNotFoundException, InterruptedException {

        List<Long> viewProductList = new ArrayList<>();
        viewProductList.add(1L);

        when(viewProductsStream.start(anyLong())).thenReturn(viewProductList);
        when(productService.getProduct(anyLong())).thenThrow(ElementNotFoundException.class);

        Set<Product> products = recommendationService.fetchRecommendedProduct(1L);
        assertTrue(products.size() == 0);
        verify(viewProductsStream, times(1)).start(anyLong());

    }

    @Test(expected = NullPointerException.class)
    public void fetchRecommendedProductExceptionTest_2() throws ElementNotFoundException, InterruptedException {

        List<Long> viewProductList = new ArrayList<>();
        viewProductList.add(1L);

        when(viewProductsStream.start(anyLong())).thenReturn(viewProductList);
        when(productService.getProduct(anyLong())).thenReturn(null);

        recommendationService.fetchRecommendedProduct(1L);
    }


}
