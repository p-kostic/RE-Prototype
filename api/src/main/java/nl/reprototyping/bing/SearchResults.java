package nl.reprototyping.bing;


public class SearchResults {
    private WebPages webPages;

    public WebPages getWebPages() {
        return webPages;
    }

    public void setWebPages(WebPages webPages) {
        this.webPages = webPages;
    }

    @Override
    public String toString() {
        return "SearchResults{" +
                "value=" + webPages +
                '}';
    }
}
