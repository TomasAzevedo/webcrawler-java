package com.axreng.backend.crawler;

import com.axreng.backend.dto.StatusDTO;
import com.axreng.backend.model.StatusEnum;
import com.axreng.backend.repository.SearchRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WebCrawler {

    private final String hrefRegex = "href=\\\"(.*?)\\\"";


    /**
     * Inicia o processo de busca em um determinado site.
     *
     * @param keyword - palavra chave para a busca.
     * @param root    - site que o algoritmo começar a procura.
     */
    public void find(String keyword, String root, String searchId) {

        try {

            StatusDTO statusDTO = getNewSatusDTO(searchId);
            SearchRepository.save(statusDTO);

            Queue<String> queue = new LinkedList<>();
            Set<String> marked = new HashSet<>();
            String urlBase = root;

            queue.add(root);

            while (!queue.isEmpty()) {

                String crawledUrl = queue.poll();

                //TODO Get from env var
                if (marked.size() > 100) {
                    SearchRepository.setStatus(statusDTO.getId(), StatusEnum.DONE);
                    return;
                }

                String content = getContent(crawledUrl, queue);

                if (find(keyword, content)) {
                    SearchRepository.addUrl(statusDTO.getId(), crawledUrl);
                }

                Stream<String> links = getLinks(content, urlBase);

                links.forEach(link -> {

                    if (!link.isEmpty() && !marked.contains(link)) {
                        marked.add(link);
                        queue.add(link);
                        System.out.println("Crawled: " + link);
                    }

                });

            }

        } catch (IOException ioe) {
            //TODO tratar erros
            System.out.println("Erro.");
        }

    }


    /**
     * Método que realiza a busca pela palavra chave.
     *
     * @param keyword  - palavre chave da busca.
     * @param content  - conteúdo retornado pela url.
     */
    private boolean find(String keyword, String content) {

        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(content);

        return matcher.find();

    }


    /**
     * Método para instaciar um novo StatusDTO.
     *
     * @param searchId - id da busca.
     * @return
     */
    private StatusDTO getNewSatusDTO(String searchId) {

        StatusDTO statusDTO = new StatusDTO();

        statusDTO.setId(searchId);
        statusDTO.setUrls(new ArrayList());
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

        Pattern pattern = Pattern.compile(hrefRegex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {

            String link = getValidLink(matcher.group(), baseUrl);

            links.add(link);

        }

        return links.stream();

    }


    /**
     * Método que retorna o conteúdo de uma determinada url.
     * Se a url fornecida não for válida o método pega a próxima da fila.
     *
     * @param crawledUrl - url fornecida para obter o conteúdo.
     * @param queue      - fila de urls.
     * @return
     * @throws IOException
     */
    private String getContent(String crawledUrl, Queue<String> queue) throws IOException {

        BufferedReader br = null;
        URL url = null;
        boolean validUrl = false;

        while (!validUrl) {

            try {

                url = new URL(Objects.requireNonNull(crawledUrl));
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                validUrl = true;

            } catch (MalformedURLException mue) {
                crawledUrl = queue.poll();
                validUrl = false;
            } catch (IOException ioe) {
                crawledUrl = queue.poll();
                validUrl = false;
            }

        }

        StringBuilder sb = new StringBuilder();
        String content = null;

        while ((content = br.readLine()) != null) {
            sb.append(content);
        }

        content = sb.toString();

        if (br != null) {
            br.close();
        }

        return content;

    }


    /**
     * Método que trata os links encontrados.
     * Se o link não tiver um endereço http o método assume que é um link interno do site.
     * Se o link não pertencer a url base não será considerado um link válido para busca.
     *
     * @param group - href com o endereço concatenado.
     * @return uma url válida.
     */
    private static String getValidLink(String group, String baseUrl) {

        String tmp = group.substring(group.indexOf("href=") + 6, group.length() - 1);

        String url = "";

        if (!group.contains("http")) {
            url += baseUrl + tmp;
        } else {
            if (tmp.contains(baseUrl)) {
                url = tmp;
            }
        }

        return url;

    }


}
