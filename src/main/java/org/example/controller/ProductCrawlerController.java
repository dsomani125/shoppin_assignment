package org.example.controller;

import org.example.dto.request.ProductUrlsRequest;
import org.example.service.ProductCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shoppin")
public class ProductCrawlerController {

    @Autowired private ProductCrawlerService productCrawlerService;

    @PostMapping("/list")
    public ResponseEntity<Object> getProductUrlsList(
            @RequestBody ArrayList<String> request) {
        return ResponseEntity.ok(productCrawlerService.getProductUrlsList(request));
    }
}
