package nl.reprototyping.bing;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;


public class BingSearch {
    // Enter a valid subscription key.
    static String subscriptionKey = "7b0426e17856426ebe0e771644666a79";

    /*
     * If you encounter unexpected authorization errors, double-check these value
     * against the endpoint for your Bing Web search instance in your Azure
     * dashboard.
     */
    static String host = "https://api.cognitive.microsoft.com";
    static String path = "/bing/v7.0/search";

    public static SearchResults SearchWeb (String searchQuery, int count) throws Exception {
        // Construct the URL.
        URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8") +
                "&responseFilter=Webpages&count=" + count);

        // Open the connection.
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // Receive the JSON response body.
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();

        // Construct the result object.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SearchResults results = objectMapper.readValue(response, SearchResults.class);
        stream.close();
        return results;
    }
}
