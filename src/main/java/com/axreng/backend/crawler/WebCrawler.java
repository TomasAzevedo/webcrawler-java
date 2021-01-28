package com.axreng.backend.crawler;

import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.model.StatusEnum;
import com.axreng.backend.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebCrawler.class);


    /**
     * Starts the search process on a particular website.
     *
     * @param keyword    - keyword for the search..
     * @param root       - site that the algorithm starts looking for.
     * @param maxResults - maximum number of urls to fetch.
     * @param searchId   - search id.
     */
    public void search(String keyword, final String root, int maxResults, String searchId) {

        try {

            StatusDTO statusDTO = getNewSatusDTO(searchId);
            SearchRepository.save(statusDTO);

            Queue<String> queue = new LinkedList<>();
            Set<String> marked = new HashSet<>();
            Set<String> results = new HashSet<>();

            queue.add(root);

            while (!queue.isEmpty()) {

                StringBuilder crawledUrl = new StringBuilder(queue.poll());

                if (results.size() >= maxResults && maxResults > -1) {
                    SearchRepository.setStatus(statusDTO.getId(), StatusEnum.DONE);
                    return;
                }

                String content = getContent(crawledUrl, queue);

                if (find(keyword, content)) {
                    results.add(crawledUrl.toString());
                    SearchRepository.addUrl(statusDTO.getId(), crawledUrl.toString());
                }

                Stream<String> links = getLinks(content, root);

                links.forEach(link -> {

                    if (!link.isEmpty() && !marked.contains(link)) {
                        marked.add(link);
                        queue.add(link);
                    }

                });

            }

            SearchRepository.setStatus(statusDTO.getId(), StatusEnum.DONE);

        } catch (IOException ioe) {
            LOGGER.error("Error during process: id = {}, {}", searchId, ioe.getMessage());
        }

    }


    /**
     * Method that performs the search for the keyword.
     *
     * @param keyword - keyword for the search..
     * @param content - content where search will be carried out.
     */
    public boolean find(String keyword, String content) {

        if (null == keyword || keyword.isBlank()) {
            return false;
        }

        boolean found;

        Pattern pattern = Pattern.compile(keyword, CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        found = matcher.find();

        if (!found) {
            // Try without html tags and comments
            matcher = pattern.matcher(content.trim()
                                             .replaceAll("<!--.*?-->", "")
                                             .replaceAll("<[^>]+>", ""));
            found = matcher.find();
        }

        return found;

    }


    /**
     * Method to instantiate a new StatusDTO.
     *
     * @param searchId - search id.
     * @return new StatusDTO
     */
    private StatusDTO getNewSatusDTO(String searchId) {

        StatusDTO statusDTO = new StatusDTO();

        statusDTO.setId(searchId);
        statusDTO.setUrls(new ArrayList<String>());
        statusDTO.setStatus(StatusEnum.ACTIVE.toString());

        return statusDTO;
    }


    /**
     * Método que varre o conteudo de uma url e procura pelos links (href).
     *
     * @param content - conteúdo html de uma url.
     * @param baseUrl - url base para consulta.
     * @return lista de links encontrados.
     */
    private Stream<String> getLinks(String content, String baseUrl) {

        List<String> links = new ArrayList<>();

        Pattern pattern = Pattern.compile("href\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {

            String link = getValidLink(matcher.group(1), baseUrl);

            links.add(link);

        }

        return links.stream();

    }


    /**
     * Returns the content of a given url.
     * If the provided url is not valid, the method takes the next one in the queue.
     *
     * @param crawledUrl - url provided to get the content..
     * @param queue      - URL queue.
     * @return content of a given url.
     * @throws IOException for errors with the url
     */
    private String getContent(StringBuilder crawledUrl, Queue<String> queue) throws IOException {

        BufferedReader br = null;
        URL url;
        boolean validUrl = false;

        while (!validUrl) {

            try {

                url = new URL(Objects.requireNonNull(crawledUrl.toString()));
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                validUrl = true;

            } catch (IOException ioe) {
                crawledUrl.setLength(0);
                crawledUrl.append(queue.poll());
                validUrl = false;
            }

        }

        StringBuilder sb = new StringBuilder();
        String content;

        while ((content = br.readLine()) != null) {
            sb.append(content);
        }

        content = sb.toString();

        br.close();

        return content;

    }


    /**
     * Método que trata os links encontrados.
     * Se o link não tiver um endereço http o método assume que é um link interno do site.
     * Se o link não pertencer a url base não será considerado um link válido para busca.
     *
     * @param href - link to validate.
     * @return uma url válida.
     */
    private static String getValidLink(String href, String baseUrl) {

        String url = "";

        if (!href.contains("http")) {
            url += baseUrl + href;
        } else {
            if (href.contains(baseUrl)) {
                url = href;
            }
        }

        return url;

    }


}
