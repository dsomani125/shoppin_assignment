package org.example.service.impl;

import org.apache.coyote.Response;
import org.example.dto.request.ProductUrlsRequest;
import org.example.service.ProductCrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ProductCrawlerServiceImpl implements ProductCrawlerService {

    private static final List<String> PRODUCT_KEYWORDS = Arrays.asList("/product/", "/item/", "/p/");
    private static final int THREAD_COUNT = 10;
    private static final int TIMEOUT = 10000; // 10 seconds

    @Override
    public Object getProductUrlsList(List<String> request) {
        Map<String, Set<String>> results = crawl(request);
        return results;
    }

    public Map<String, Set<String>> crawl(List<String> domains) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        Map<String, Set<String>> domainToUrls = new ConcurrentHashMap<>();

        for (String domain : domains) {

                try {
                    Set<String> productUrls = discoverProductUrls(domain);
                    domainToUrls.put(domain, productUrls);
                } catch (IOException e) {
                    System.err.println("Error crawling domain: " + domain);
                    e.printStackTrace();
                }
        }

        return domainToUrls;
    }

    private Set<String> discoverProductUrls(String domain) throws IOException {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> productUrls = new HashSet<>();

        queue.add(domain);

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            if (visited.contains(currentUrl)) continue;

            try {
                Document doc = Jsoup.connect(currentUrl).timeout(TIMEOUT).get();
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    String url = link.absUrl("href");
                    if (!visited.contains(url)) {
                        if (isProductUrl(url)) {
                            productUrls.add(url);
                        } else if (url.startsWith(domain)) {
                            queue.add(url);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error accessing URL: " + currentUrl);
            }

            visited.add(currentUrl);
        }

        return productUrls;
    }

    private boolean isProductUrl(String url) {
        return PRODUCT_KEYWORDS.stream().anyMatch(url::contains);
    }

    private void saveResults(Map<String, Set<String>> results, String outputFile) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (Map.Entry<String, Set<String>> entry : results.entrySet()) {
                writer.write("Domain: " + entry.getKey() + "\n");
                for (String url : entry.getValue()) {
                    writer.write(url + "\n");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing results to file.");
            e.printStackTrace();
        }
    }
}
