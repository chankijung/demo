package com.demo.scraper;

import com.demo.model.Company;
import com.demo.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
