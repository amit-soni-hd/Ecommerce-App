package com.ecommerce.ecommApp.recommend.view.controller;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import com.ecommerce.ecommApp.recommend.view.service.ViewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.elasticsearch.core.internal.io.IOUtils.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ViewControllerTest {

    @InjectMocks
    private ViewController viewController;

    @Mock
    private ViewService viewService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Objects objects;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(viewController)
                .setControllerAdvice()
                .build();
        this.objects = new Objects();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void viewProductTest() throws Exception {

        ViewProductDto viewProductDto = objects.getViewProductDto();
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, "Successfully viewed");

        when(viewService.viewProduct(any())).thenReturn(apiResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CommonsUtil.VIEWED + CommonsUtil.VIEW_PRODUCT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(viewProductDto))
                .characterEncoding(UTF_8);

        MvcResult result = mockMvc
                .perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void viewProductWrongMethodTest() throws Exception {

        ViewProductDto viewProductDto = objects.getViewProductDto();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CommonsUtil.VIEWED + CommonsUtil.VIEW_PRODUCT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(viewProductDto))
                .characterEncoding(UTF_8);

        MvcResult result = mockMvc
                .perform(requestBuilder)
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertEquals(405, result.getResponse().getStatus());
    }

    @Test
    public void viewProductBadRequestTest_1() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CommonsUtil.VIEWED + CommonsUtil.VIEW_PRODUCT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes("viewProductDto"))
                .characterEncoding(UTF_8);

        MvcResult result = mockMvc
                .perform(requestBuilder)
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void viewProductBadRequestTest_2() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CommonsUtil.VIEWED + CommonsUtil.VIEW_PRODUCT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(UTF_8);

        MvcResult result = mockMvc
                .perform(requestBuilder)
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }


}
