package com.axreng.backend.crawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrawlerTest {

    private static final String content = "A copy of the license is in<!--comment-->cluded in the section entitled " +
            "<span class=\"quote\">“<span class=\"quote\"><a class=\"literalurl\" " +
            "href=\"gfdl.html\" title=\"Appendix&nbsp;A.&nbsp;GNU Free<!--comment--> Documentation License\">GNU Free Documentation License</a></span>” 2021 ã</span>.";


    @Test
    void must_find_with_keyword() {
        assertTrue(new WebCrawler().find("license", content));
    }


    @Test
    void must_find_with_keyword_ignoring_case_sensitive() {
        assertTrue(new WebCrawler().find("LIcEnSE", content));
    }


    @Test
    void must_find_with_a_search_term() {
        assertTrue(new WebCrawler().find("GNU Free Documentation License", content));
    }


    @Test
    void must_find_even_with_html_tags() {
        assertTrue(new WebCrawler().find("entitled “GNU", content));
    }


    @Test
    void must_find_even_with_comment_tag() {
        assertTrue(new WebCrawler().find("included", content));
    }


    @Test
    void must_find_numbers() {
        assertTrue(new WebCrawler().find("2021", content));
    }


    @Test
    void must_find_special_characters() {
        assertTrue(new WebCrawler().find("ã", content));
    }


    @Test
    void must_not_find_a_non_existent_keyword() {
        assertFalse(new WebCrawler().find("asdasdasd", content));
    }


    @Test
    void must_not_find_a_blank_keyword() {
        assertFalse(new WebCrawler().find(" ", content));
    }


    @Test
    void must_not_find_a_empty_keyword() {
        assertFalse(new WebCrawler().find("", content));
    }


    @Test
    void must_not_find_a_null_keyword() {
        assertFalse(new WebCrawler().find(null, content));
    }


}
