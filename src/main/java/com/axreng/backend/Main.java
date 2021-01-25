package com.axreng.backend;

import com.axreng.backend.controller.CrawlerController;
import com.axreng.backend.service.CrawlerService;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {


    public static void main(String[] args) throws Exception {

        initialize();

        new CrawlerController(new CrawlerService());

    }


    /**
     * Boot validation method.
     *
     * @throws Exception
     */
    public static void initialize() throws Exception {

        //TODO Improve that.

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
