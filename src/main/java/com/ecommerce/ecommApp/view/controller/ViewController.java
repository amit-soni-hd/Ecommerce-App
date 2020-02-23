package com.ecommerce.ecommApp.view.controller;

import com.ecommerce.ecommApp.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.view.dto.response.ApiResponse;
import com.ecommerce.ecommApp.view.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private ViewService viewService;

    @PostMapping("/product")
    public ResponseEntity<ApiResponse> viewProduct(@Valid  @RequestBody ViewProductDto viewProductDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK,
                        "ok",viewService.viewProduct(viewProductDto)));
    }

}
