package com.axreng.backend.repository;

import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.model.StatusEnum;

import java.util.TreeMap;

/**
 *
 * Classe que representa a camada de persistência da aplicação.
 *
 */
public class SearchRepository {

    public static TreeMap<String, StatusDTO> searches = new TreeMap<>();

    public static void save(StatusDTO statusDTO) {
        searches.put(statusDTO.getId(), statusDTO);
    }

    public static void addUrl(String id, String url) {
        searches.get(id).getUrls().add(url);
    }

    public static StatusDTO findById(String id) {
        return searches.get(id);
    }

    public static void setStatus(String id, StatusEnum statusEnum) {
        searches.get(id).setStatus(statusEnum.toString());
    }

}
