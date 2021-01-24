package com.axreng.backend;

import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.service.CrawlerService;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

import static spark.Spark.*;

public class Main {

    public static CrawlerService crawlerService = new CrawlerService();

    public static void main(String[] args) throws Exception {

        initialize();

        get("/crawl/:id", (req, res) -> {

            res.type("application/json");

            return new Gson().toJson(crawlerService.getStatus(req.params("id")));

        });

        post("/crawl", (req, res) -> {

            res.type("application/json");

            SearchDTO keyword = new Gson().fromJson(req.body(), SearchDTO.class);
            return new Gson().toJson(crawlerService.search(keyword));

        });


    }

    public static void initialize() throws Exception {

        CrawlerService.BASE_URL = System.getenv("BASE_URL");

        try {
            new URL(CrawlerService.BASE_URL);
        } catch (MalformedURLException malformedURLException) {
            throw new Exception("Invalid URL of BASE_URL env var -> " + malformedURLException.getMessage());
        }


        try {

            String max = System.getenv("MAX_RESULTS");
            CrawlerService.MAX_RESULTS = (null != max && !max.isBlank()) ? Integer.parseInt(max) : -1;

        } catch (NumberFormatException numberFormatException) {
            throw new Exception("Invalid content for MAX_RESULTS env var -> " + numberFormatException.getMessage());
        }

        if (CrawlerService.MAX_RESULTS < -1) {
            CrawlerService.MAX_RESULTS = -1;
        }

    }

}
