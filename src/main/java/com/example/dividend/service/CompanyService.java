package com.example.dividend.service;

import com.example.dividend.entity.CompanyEntity;
import com.example.dividend.entity.DividendEntity;
import com.example.dividend.exception.impl.NoCompanyException;
import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.repository.CompanyRepository;
import com.example.dividend.repository.DividendRepository;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
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
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);
        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividendList().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .toList();
        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }


    public String deleteCompany(String ticker) {
        CompanyEntity company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);
        this.deleteAutocompleteKeyword(company.getName());
        return company.getName();
    }
}
