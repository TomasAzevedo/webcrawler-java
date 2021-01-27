package com.axreng.backend.controller;

import com.axreng.backend.Main;
import com.axreng.backend.dto.SearchDTO;
import com.axreng.backend.dto.SearchIdDTO;
import com.axreng.backend.dto.StatusDTO;
import org.junit.jupiter.api.*;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.axreng.backend.utils.JsonUtil.fromJson;
import static com.axreng.backend.utils.JsonUtil.toJson;
import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.awaitInitialization;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrawlerControllerTest {

    private static SearchIdDTO searchIdDTO;


    @BeforeAll
    public static void beforeClass() throws Exception {
        Main.main(null);
        awaitInitialization();
    }


    @AfterAll
    public static void afterClass() {
        Spark.stop();
    }


    @Test
    public void the_keyword_must_be_between_4_and_32_characters_long() {

        // Less than 4
        TestResponse res1 = request("POST", "/crawl", toJson(new SearchDTO("123")));

        assert res1 != null;
        assertEquals(400, res1.status);

        // More than 32
        TestResponse res2 = request("POST", "/crawl", toJson(new SearchDTO("123456789012345678901234567890123")));

        assert res2 != null;
        assertEquals(400, res2.status);

        // Between 4 and 32
        TestResponse res3 = request("POST", "/crawl", toJson(new SearchDTO("12345678901234567890123456789012")));

        assert res3 != null;
        assertEquals(200, res3.status);

    }


    @Test
    @Order(1)
    public void a_new_search_with_an_impossible_keyword_must_be_started() {

        SearchDTO searchDTO = new SearchDTO("tomasalvesazevedoasdadsasdasd");

        TestResponse res = request("POST", "/crawl", toJson(searchDTO));

        assert res != null;
        searchIdDTO = res.json(SearchIdDTO.class);

        assertEquals(200, res.status);
        assertNotNull(searchIdDTO);
        assertNotNull(searchIdDTO.getId());

    }


    @Test
    @Order(2)
    public void no_url_should_be_returned_for_the_search_with_an_impossible_keyword() {

        TestResponse res = request("GET", "/crawl/" + searchIdDTO.getId(), "");

        assert res != null;
        StatusDTO statusDTO = res.json(StatusDTO.class);

        assertEquals(200, res.status);
        assertNotNull(statusDTO);
        assertTrue(statusDTO.getUrls().isEmpty());

    }


    @Test
    public void special_character_must_be_accepted() {

        SearchDTO searchDTO = new SearchDTO("ãasdd");

        TestResponse res = request("POST", "/crawl", toJson(searchDTO));

        assert res != null;
        SearchIdDTO searchIdDTO = res.json(SearchIdDTO.class);

        assertEquals(200, res.status);
        assertNotNull(searchIdDTO);
        assertNotNull(searchIdDTO.getId());

    }


    @Test
    public void a_blank_keyword_should_return_http_status_400() {

        SearchDTO searchDTO = new SearchDTO(" ");

        TestResponse res = request("POST", "/crawl", toJson(searchDTO));

        assert res != null;
        assertEquals(400, res.status);

    }


    @Test
    public void an_empty_keyword_property_should_return_http_status_400() {

        SearchDTO searchDTO = new SearchDTO("");

        TestResponse res = request("POST", "/crawl", toJson(searchDTO));

        assert res != null;
        assertEquals(400, res.status);

    }


    @Test
    public void a_null_keyword_json_should_return_http_status_400() {

        TestResponse res = request("POST", "/crawl", null);

        assert res != null;
        assertEquals(400, res.status);

    }


    @Test
    public void a_null_keyword_property_should_return_http_status_400() {

        SearchDTO searchDTO = new SearchDTO();

        TestResponse res = request("POST", "/crawl", toJson(searchDTO));

        assert res != null;
        assertEquals(400, res.status);

    }


    @Test
    public void an_invalid_search_id_must_return_http_status_404() {

        TestResponse res = request("GET", "/crawl/zzzaaasadaqwe", "");

        assert res != null;
        assertEquals(404, res.status);

    }


    @Test
    public void no_search_id_must_return_http_status_404() {

        TestResponse res = request("GET", "/crawl/", "");

        assert res != null;
        assertEquals(404, res.status);

    }


    private TestResponse request(String method, String path, String json) {

        try {
            URL url = new URL("http://0.0.0.0:4567" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            //connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            if (null != json && !json.isBlank()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            connection.connect();
            String body = "";
            if (connection.getResponseCode() != 400 && connection.getResponseCode() != 404) {
                body = IOUtils.toString(connection.getInputStream());
            }

            return new TestResponse(connection.getResponseCode(), body);

        } catch (IOException e) {
            fail("Sending request failed: " + e.getMessage());
            return null;
        }

    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public <T> T json(Class<T> classOfT) {
            return fromJson(body, classOfT);
        }

    }

}
