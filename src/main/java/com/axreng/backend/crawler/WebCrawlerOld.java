package com.axreng.backend.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerOld {

    public static Queue<String> queue = new LinkedList<>();
    public static Set<String> marked = new HashSet<>();
    public static String genericUrlRegex = "http[s]*://(\\w+\\.)*(\\w+)";
    public static String hrefRegex = "href=\\\"(.*?)\\\"";
    public static String baseUrlRegex = "^.+?[^\\/:](?=[?\\/]|$)";

    public static final String BASE_URL = "http://hiring.axreng.com/";


    public static void bfsAlgorithm(String root) throws IOException {

        queue.add(root);

        BufferedReader br = null;

        while (!queue.isEmpty()) {

            String crawledUrl = queue.poll();
            System.out.println("Site crawled: " + crawledUrl);

            //TODO Get from env var
            if (marked.size() > 100) {
                return;
            }

            boolean ok = false;
            URL url = null;

            while (!ok) {

                try {

                    url = new URL(Objects.requireNonNull(crawledUrl));
                    br = new BufferedReader(new InputStreamReader(url.openStream()));
                    ok = true;

                } catch (MalformedURLException mue) {
                    //TODO Validate the URL on init
                    System.out.println("Malformed URL: " + crawledUrl);
                    crawledUrl = queue.poll();
                    ok = false;
                } catch (IOException ioe) {
                    //TODO Handle erros to API
                    System.out.println("IOException for URL: " + crawledUrl);
                    crawledUrl = queue.poll();
                    ok = false;
                }

            }

            StringBuilder sb = new StringBuilder();
            String tmp = null;

            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }

            tmp = sb.toString();

            Pattern pattern = Pattern.compile(hrefRegex);
            Matcher matcher = pattern.matcher(tmp);

            while (matcher.find()) {

                String w = validateMatch(matcher.group());

                if (!w.isEmpty() && !marked.contains(w)) {
                    marked.add(w);
                    System.out.println("Site added for crawling: " + w);
                    queue.add(w);
                }

            }

        }

        if (br != null) {
            br.close();
        }

    }


    private static String validateMatch(String group) {

        String tmp = group.substring(group.indexOf("href=")+6, group.length()-1);

        String url = "";

        if (!group.contains("http")) {
            url += BASE_URL + tmp;
        } else {
            if (tmp.contains(BASE_URL)) {
                url = tmp;
            }
        }

        return url;

    }


    private static boolean isFromBaseUrl(String crawledUrl) {

        //TODO Get base url from env var
       // return crawledUrl.contains("http://hiring.axreng.com/");
        return true;

    }


    public static void showResults() {

        System.out.println("Results: ");
        System.out.println("Web sites crawled: " + marked.size());

        for (String s : marked) {
            System.out.println("* " + s);
        }

    }


    public static void main(String[] args) {
        try {
            bfsAlgorithm(BASE_URL);
            //bfsAlgorithm("https://tartarosgrill.com.br/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        showResults();
    }


}
