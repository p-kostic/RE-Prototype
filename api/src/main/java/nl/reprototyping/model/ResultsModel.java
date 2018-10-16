package nl.reprototyping.model;


import nl.reprototyping.bing.WebPage;

import java.util.List;


public class ResultsModel {
    List<WebPage> results;
    int pageSize;
    int totalSize;

    public ResultsModel(List<WebPage> results, int pageSize, int totalSize) {
        this.results = results;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }

    public List<WebPage> getResults() {
        return results;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalSize() {
        return totalSize;
    }
}
