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
            return new ResponseError("No search found with this id.", req.params("id"));

        }, json());


        post("/crawl", (req, res) -> crawlerService.search(fromJson(req.body(), SearchDTO.class)), json());


        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });


        notFound((req, res) -> {
            return toJson(new ResponseError("Method not found."));
        });


        after((req, res) -> {
            res.type("application/json");
        });


    }

}
