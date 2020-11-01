package com.example.demo.boundary;

import com.example.demo.control.managment.MoneyManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MoneyManagementBoundaryTest {

    @Autowired
    private MoneyManagementBoundary boundary;

    @Test
    public void shouldGetMoneyManagement() {
        MoneyManagement moneyManagement = boundary.moneyManagement();

        assertThat(moneyManagement).isNotNull();
    }

}