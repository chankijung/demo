package com.demo.scheduler;

import com.demo.model.Company;
import com.demo.model.ScrapedResult;
import com.demo.model.constants.CacheKey;
import com.demo.persist.CompanyRepository;
import com.demo.persist.DividendRepository;
import com.demo.persist.entity.CompanyEntity;
import com.demo.persist.entity.DividendEntity;
import com.demo.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;


    /* @Scheduled(fixedDelay = 1000)
     public void test1() throws InterruptedException{
         Thread.sleep(10000);
         System.out.println(Thread.currentThread().getName()+"->테스트 1 :"+ LocalDateTime.now());

     }
     @Scheduled(fixedDelay = 1000)
     public void test2(){
         System.out.println(Thread.currentThread().getName()+"->테스트 2 :"+ LocalDateTime.now());

     }*/
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        //저장된 회사 목록 조회

        List<CompanyEntity> companies = this.companyRepository.findAll();

        //회사 마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started ->" + company.getName());
            ScrapedResult scrapedResult =
                    this.yahooFinanceScraper.scrap(new Company(company.getTicker(), company.getName()));
            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    //디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    //엘리먼트를 하나씩 디비든레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }


        }
    }
}
