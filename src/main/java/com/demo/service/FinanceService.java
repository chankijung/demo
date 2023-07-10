package com.demo.service;

import com.demo.model.Company;
import com.demo.model.Dividend;
import com.demo.model.ScrapedResult;
import com.demo.persist.CompanyRepository;
import com.demo.persist.DividendRepository;
import com.demo.persist.entity.CompanyEntity;
import com.demo.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName){
        //1.회사 명을 기준으로 회사정보 조회
        CompanyEntity company=this.companyRepository.findByName(companyName)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 회사명입니다"));



        //2. 조회된 회사 ID로 배당금 정보 조회
        List< DividendEntity> dividendEntities=this.dividendRepository.findAllByCompanyId(company.getId());

        //3. 결과 조합 후 반환
        List<Dividend> dividends=new ArrayList<>();
        for(var entity: dividendEntities){
            dividends.add(Dividend.builder()
                            .date(entity.getDate())
                            .dividend(entity.getDividend())
                    .build());
        }


        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(), dividends);


    }
}
