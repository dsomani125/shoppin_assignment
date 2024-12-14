package org.example.service;


import org.example.dto.request.ProductUrlsRequest;

import java.util.List;

public interface ProductCrawlerService {
    Object getProductUrlsList(List<String> request);
}
