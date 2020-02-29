package com.ecommerce.ecommApp.recommend.view.controller;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import com.ecommerce.ecommApp.recommend.view.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping(CommonsUtil.VIEWED)
public class ViewController {

    @Autowired
    private ViewService viewService;

    /**
     * post api for store the view product for same user
     * @param viewProductDto RequestBody contain product id and customer id
     * @return ResponseEntity of viewed product
     */
    @PostMapping(CommonsUtil.VIEW_PRODUCT)
    public ResponseEntity<ApiResponse> viewProduct(@Valid  @RequestBody ViewProductDto viewProductDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK,
                        "ok",viewService.viewProduct(viewProductDto)));
    }

}
