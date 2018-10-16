package nl.reprototyping.bing;


import java.util.List;


public class WebPages {
    String webSearchUrl, totalEstimatedMatches;
    List<WebPage> value;

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public String getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void setTotalEstimatedMatches(String totalEstimatedMatches) {
        this.totalEstimatedMatches = totalEstimatedMatches;
    }

    public List<WebPage> getValue() {
        return value;
    }

    public void setValue(List<WebPage> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WebPages{" +
                "webSearchUrl='" + webSearchUrl + '\'' +
                ", totalEstimatedMatches='" + totalEstimatedMatches + '\'' +
                ", value=" + value +
                '}';
    }
}
