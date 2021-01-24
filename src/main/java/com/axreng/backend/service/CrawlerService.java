package com.axreng.backend.service;

import com.axreng.backend.crawler.WebCrawler;
import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.dto.SearchIdDTO;
import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.repository.SearchRepository;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Classe responsável pela encapsular a lógica da aplicação.
 *
 */
public class CrawlerService {

    private static final String BASE_URL = "http://hiring.axreng.com/";


    /**
     *
     * Método responsável pela busca.
     *
     * @param searchDTO - objeto que contém a keyword para busca.
     *
     * @return id da busca startada.
     */
    public SearchIdDTO search(SearchDTO searchDTO) {

        WebCrawler webCrawler = new WebCrawler();
        String searchId = generateSearchId();

        CompletableFuture.runAsync(() -> {
            webCrawler.find(searchDTO.getKeyword(), BASE_URL, searchId);
        });

        return new SearchIdDTO(searchId);
    }


    /**
     *
     * Método que verifica o status da busca.
     *
     * @param id - identificador da busca realizada.
     *
     * @return status da busca e suas urls encontradas.
     */
    public StatusDTO getStatus(String id) {

        return SearchRepository.findById(id);

    }


    /**
     *
     * Método que cria um id única para a busca.
     *
     * @return identificador único.
     */
    private final String generateSearchId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
