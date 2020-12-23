//package com.example.mm.boundary;
//
//
//import com.example.mm.data.Investment;
//import com.example.mm.data.MoneyManagement;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static java.math.BigDecimal.ONE;
//import static java.math.BigDecimal.TEN;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.offset;
//
//class MoneyManagementTests {
//
//    private MoneyManagement moneyManagement;
//
//    public List<Investment> createInvestments() {
//        List<Investment> investments = new ArrayList<>();
//        investments.add(createInvestment("ABC", TEN, 10, BigDecimal.valueOf(20)));
//        investments.add(createInvestment("DEF", TEN, 1, BigDecimal.valueOf(5)));
//
//        return investments;
//    }
//
//    private static Investment createInvestment(String name, BigDecimal purchasePrice, int quality, BigDecimal notationalSalesPrice) {
//        return Investment.builder()
//                .name(name)
//                .purchasePrice(purchasePrice)
//                .quantity(quality)
//                .notionalSalesPrice(notationalSalesPrice)
//                .purchaseCost(ONE)
//                .build();
//    }
//
//    @BeforeEach
//    private void setUp() {
//        moneyManagement = MoneyManagement.builder()
//                .totalCapital(BigDecimal.valueOf(30000))
//                .individualPositionRiskInPercent(2)
//                .investments(createInvestments())
//                .build();
//    }
//
//    @Test
//    public void getIndividualPositionRisk() {
//        BigDecimal individualPositionRisk = moneyManagement.getIndividualPositionRisk();
//
//        assertThat(individualPositionRisk).isEqualByComparingTo(BigDecimal.valueOf(600.00));
//    }
//
//    @Test
//    public void getPortfolioRisk() {
//        double portfolioRisk = moneyManagement.getRiskInPercent();
//
//        assertThat(portfolioRisk).isCloseTo(5.454, offset(0.001));
//    }
//
//    @Test
//    public void getTotalRisk() {
//        double totalRisk = moneyManagement.getTotalRiskInPercent();
//
//        assertThat(totalRisk).isCloseTo(0.02, offset(0.001));
//    }
//
//    @Test
//    void shouldGetTotalSum() {
//        assertThat(moneyManagement.getTotalSum()).isEqualTo(BigDecimal.valueOf(110));
//    }
//
//    @Test
//    void shouldGetTotalRevenue() {
//        assertThat(moneyManagement.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(205));
//    }
//
//    @Test
//    void shouldGetTotalLossAbs() {
//        assertThat(moneyManagement.getTotalRisk()).isEqualTo(BigDecimal.valueOf(6));
//    }
//
//}
//
