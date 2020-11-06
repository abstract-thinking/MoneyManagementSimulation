package com.example.demo.data;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoneyManagementRepository extends PagingAndSortingRepository<MoneyManagementValues, Long> {

}