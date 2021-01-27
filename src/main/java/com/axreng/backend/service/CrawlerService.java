package com.axreng.backend.service;

import com.axreng.backend.crawler.WebCrawler;
import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.dto.SearchIdDTO;
import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.repository.SearchRepository;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Module that implements the Crawler.
 */
public class CrawlerService {

    public static String BASE_URL;
    public static int MAX_RESULTS;

    /**
     * Method that performs the search.
     *
     * @param searchDTO - object containing the search keyword.
     * @return search id.
     */
    public SearchIdDTO search(SearchDTO searchDTO) {

        if (null == searchDTO || null == searchDTO.getKeyword() || searchDTO.getKeyword().isBlank()) {
            throw new IllegalArgumentException("You must enter at least one keyword for the search.");
        }

        if (searchDTO.getKeyword().length() < 4 ||
            searchDTO.getKeyword().length() > 32) {
            throw new IllegalArgumentException("The keyword must be between 4 and 32 characters long.");
        }

        WebCrawler webCrawler = new WebCrawler();
        String searchId = generateSearchId();

        CompletableFuture.runAsync(() -> {
            webCrawler.search(searchDTO.getKeyword(), BASE_URL, MAX_RESULTS, searchId);
        });

        return new SearchIdDTO(searchId);
    }


    /**
     * Method that checks the status of the search.
     *
     * @param id - search id.
     * @return search status and their found urls.
     */
    public StatusDTO getStatus(String id) {

        return SearchRepository.findById(id);

    }


    /**
     * Method that creates a unique search id.
     *
     * @return unique id.
     */
    private final String generateSearchId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0,8);
    }

}
