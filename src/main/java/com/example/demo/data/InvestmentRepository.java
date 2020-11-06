package com.example.demo.data;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvestmentRepository extends PagingAndSortingRepository<Investment, Long> {

}