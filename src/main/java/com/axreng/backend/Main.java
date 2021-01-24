package com.axreng.backend;

import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.service.CrawlerService;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {

    public static CrawlerService crawlerService = new CrawlerService();

    public static void main(String[] args) {

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
}
