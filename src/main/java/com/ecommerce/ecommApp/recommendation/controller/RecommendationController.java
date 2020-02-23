package com.ecommerce.ecommApp.recommendation.controller;

import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.recommendation.service.RecommendationService;
import com.ecommerce.ecommApp.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.view.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/fetch")
    public ResponseEntity<ApiResponse> fetchRecommendation(@RequestParam( value = "id", required = true) Long customerId) {
        Set<Product> products = recommendationService.fetchRecommendedProduct(customerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK, "Recommended Product", products));
    }
}
