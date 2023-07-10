package com.demo.service;

import com.demo.model.Company;
import com.demo.model.Dividend;
import com.demo.model.ScrapedResult;
import com.demo.persist.CompanyRepository;
import com.demo.persist.DividendRepository;
import com.demo.persist.entity.CompanyEntity;
import com.demo.persist.entity.DividendEntity;
import com.demo.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker->" + ticker);
        }
        return null;
    }


    private Company storeCompanyAndDividend(String ticker) {

        // ticker 기준으로 회사 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker ->" + ticker);
        }
        //회사 존재하면 회사의 배당금 정보 스크래핑

        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        //스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList =
                scrapedResult.getDividends().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);

    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword)
                .keySet()
                .stream()
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
    public List<String> getCompanyNamesByKeyWord(String keyword){
        Pageable limit= PageRequest.of(0,10);
       Page<CompanyEntity> companyEntities =this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);
       return companyEntities.stream()
               .map(e->e.getName())
               .collect(Collectors.toList());
    }
}
