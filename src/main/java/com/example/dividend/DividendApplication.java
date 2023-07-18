package com.example.dividend;

import com.example.dividend.model.Company;
import com.example.dividend.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;

import java.io.IOException;

@SpringBootApplication
public class DividendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendApplication.class, args);
        YahooFinanceScraper yahooFinanceScraper = new YahooFinanceScraper();
        var v1 = yahooFinanceScraper.scrap(Company.builder()
                .ticker("COKE")
                .build());
        System.out.println(v1);

        var v = yahooFinanceScraper.scrapCompanyByTicker("COKE");
        System.out.println(v);
    }
}