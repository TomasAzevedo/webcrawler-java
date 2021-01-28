package com.axreng.backend.controller;

import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.utils.ResponseError;

import static com.axreng.backend.utils.JsonUtil.*;
import static spark.Spark.*;

public class CrawlerController {

    public CrawlerController(CrawlerService crawlerService) {

        get("/crawl/:id", (req, res) -> {

            StatusDTO statusDTO = crawlerService.getStatus(req.params("id"));

            if (null != statusDTO) {
                return statusDTO;
            }

            res.status(404);
            return new ResponseError(String.valueOf(res.status()), "No search found with this id.", req.params("id"));

        }, json());


        post("/crawl", (req, res) -> crawlerService.search(fromJson(req.body(), SearchDTO.class)), json());

        before((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(String.valueOf(res.status()), e)));
        });

        notFound((req, res) -> toJson(new ResponseError(String.valueOf(res.status()), "Method not found.")));


    }

}
