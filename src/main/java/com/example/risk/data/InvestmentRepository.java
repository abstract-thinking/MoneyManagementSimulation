package com.example.risk.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvestmentRepository extends CrudRepository<Investment, Long> {

    List<Investment> findAllByMoneyManagementId(Long id);
}