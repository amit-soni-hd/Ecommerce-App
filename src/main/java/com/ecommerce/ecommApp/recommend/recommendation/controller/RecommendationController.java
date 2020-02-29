package com.ecommerce.ecommApp.recommend.recommendation.controller;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.recommend.recommendation.service.RecommendationService;
import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping(CommonsUtil.RECOMMENDATION)
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * get api for fetching the recommended product
     * @param customerId request param of customer id
     * @return list of recommended products
     */
    @GetMapping(CommonsUtil.FETCH_RECOMMENDATION)
    public ResponseEntity<ApiResponse> fetchRecommendation(@RequestParam( value = "id", required = true) Long customerId) {
        Set<Product> products = recommendationService.fetchRecommendedProduct(customerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK, "Recommended Product", products));
    }
}
