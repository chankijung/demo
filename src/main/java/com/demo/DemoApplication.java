package com.demo;

import com.demo.model.Company;
import com.demo.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DemoApplication {

    public static void main(String[] args) {
       SpringApplication.run(DemoApplication.class, args);

        //YahooFinanceScraper scraper=new YahooFinanceScraper();
        //var result= scraper.scrapCompanyByTicker("O");
        //System.out.println(result);
    }

}
