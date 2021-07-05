package com.example.risk.provider;

import com.example.risk.data.Investment;
import com.example.risk.service.tradier.Quote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {

    public static Investment createInvestment(long riskManagementId) {
        return Investment.builder()
                .symbol("AAPL")
                .currentStopPrice(BigDecimal.TEN)
                .name("Apple")
                .quantity(10)
                .purchasePrice(BigDecimal.valueOf(130.3))
                .riskManagementId(riskManagementId)
                .stopPrice(BigDecimal.valueOf(130))
                .transactionCosts(BigDecimal.valueOf(60))
                .build();
    }

    public static List<Quote> createQuotes() {
        List<Quote> quotes = new ArrayList<>();
        quotes.add(new Quote(LocalDate.parse("2019-12-09"), BigDecimal.valueOf(67.5), BigDecimal.valueOf(68.825), BigDecimal.valueOf(66.2275), BigDecimal.valueOf(68.7875), 569633070));
        quotes.add(new Quote(LocalDate.parse("2019-12-16"), BigDecimal.valueOf(69.25), BigDecimal.valueOf(70.6625), BigDecimal.valueOf(69.245), BigDecimal.valueOf(69.86), 733365120));
        quotes.add(new Quote(LocalDate.parse("2019-12-23"), BigDecimal.valueOf(70.1325), BigDecimal.valueOf(73.4925), BigDecimal.valueOf(70.093375), BigDecimal.valueOf(72.45), 386898150));
        quotes.add(new Quote(LocalDate.parse("2019-12-30"), BigDecimal.valueOf(72.365), BigDecimal.valueOf(75.15), BigDecimal.valueOf(71.305), BigDecimal.valueOf(74.3575), 527411920));
        quotes.add(new Quote(LocalDate.parse("2020-01-06"), BigDecimal.valueOf(73.4475), BigDecimal.valueOf(78.1675), BigDecimal.valueOf(73.1875), BigDecimal.valueOf(77.5825), 673808240));
        quotes.add(new Quote(LocalDate.parse("2020-01-13"), BigDecimal.valueOf(77.91), BigDecimal.valueOf(79.685), BigDecimal.valueOf(77.3875), BigDecimal.valueOf(79.6825), 653269730));
        quotes.add(new Quote(LocalDate.parse("2020-01-20"), BigDecimal.valueOf(79.2975), BigDecimal.valueOf(80.8325), BigDecimal.valueOf(78.9125), BigDecimal.valueOf(79.5775), 463685210));
        quotes.add(new Quote(LocalDate.parse("2020-01-27"), BigDecimal.valueOf(77.515), BigDecimal.valueOf(81.9625), BigDecimal.valueOf(76.22), BigDecimal.valueOf(77.3775), 867105290));
        quotes.add(new Quote(LocalDate.parse("2020-02-03"), BigDecimal.valueOf(76.075), BigDecimal.valueOf(81.305), BigDecimal.valueOf(75.555), BigDecimal.valueOf(80.0075), 652538600));
        quotes.add(new Quote(LocalDate.parse("2020-02-10"), BigDecimal.valueOf(78.545), BigDecimal.valueOf(81.805), BigDecimal.valueOf(78.4625), BigDecimal.valueOf(81.2375), 492263630));
        quotes.add(new Quote(LocalDate.parse("2020-02-17"), BigDecimal.valueOf(78.84), BigDecimal.valueOf(81.1625), BigDecimal.valueOf(77.625), BigDecimal.valueOf(78.2625), 477017760));
        quotes.add(new Quote(LocalDate.parse("2020-02-24"), BigDecimal.valueOf(74.315), BigDecimal.valueOf(76.045), BigDecimal.valueOf(64.0925), BigDecimal.valueOf(68.34), 1399072900));
        quotes.add(new Quote(LocalDate.parse("2020-03-02"), BigDecimal.valueOf(70.57), BigDecimal.valueOf(76.0), BigDecimal.valueOf(69.43), BigDecimal.valueOf(72.2575), 1293800900));
        quotes.add(new Quote(LocalDate.parse("2020-03-09"), BigDecimal.valueOf(65.9375), BigDecimal.valueOf(71.61), BigDecimal.valueOf(62.0), BigDecimal.valueOf(69.4925), 1617621000));
        quotes.add(new Quote(LocalDate.parse("2020-03-16"), BigDecimal.valueOf(60.4875), BigDecimal.valueOf(64.77), BigDecimal.valueOf(57.0), BigDecimal.valueOf(57.31), 1620263300));
        quotes.add(new Quote(LocalDate.parse("2020-03-23"), BigDecimal.valueOf(57.02), BigDecimal.valueOf(64.67), BigDecimal.valueOf(53.1525), BigDecimal.valueOf(61.935), 1384663300));
        quotes.add(new Quote(LocalDate.parse("2020-03-30"), BigDecimal.valueOf(62.685), BigDecimal.valueOf(65.6225), BigDecimal.valueOf(59.225), BigDecimal.valueOf(60.3525), 837011040));
        quotes.add(new Quote(LocalDate.parse("2020-04-06"), BigDecimal.valueOf(62.725), BigDecimal.valueOf(67.925), BigDecimal.valueOf(62.345), BigDecimal.valueOf(66.9975), 735719380));
        quotes.add(new Quote(LocalDate.parse("2020-04-13"), BigDecimal.valueOf(67.0775), BigDecimal.valueOf(72.0625), BigDecimal.valueOf(66.4575), BigDecimal.valueOf(70.7), 829547250));
        quotes.add(new Quote(LocalDate.parse("2020-04-20"), BigDecimal.valueOf(69.4875), BigDecimal.valueOf(70.7525), BigDecimal.valueOf(66.3575), BigDecimal.valueOf(70.7425), 679387000));
        quotes.add(new Quote(LocalDate.parse("2020-04-27"), BigDecimal.valueOf(70.45), BigDecimal.valueOf(74.75), BigDecimal.valueOf(69.55), BigDecimal.valueOf(72.2675), 790053710));
        quotes.add(new Quote(LocalDate.parse("2020-05-04"), BigDecimal.valueOf(72.2925), BigDecimal.valueOf(77.5875), BigDecimal.valueOf(71.5793), BigDecimal.valueOf(77.5325), 672915870));
        quotes.add(new Quote(LocalDate.parse("2020-05-11"), BigDecimal.valueOf(77.025), BigDecimal.valueOf(79.922), BigDecimal.valueOf(75.0525), BigDecimal.valueOf(76.9275), 834147300));
        quotes.add(new Quote(LocalDate.parse("2020-05-18"), BigDecimal.valueOf(78.2925), BigDecimal.valueOf(80.2225), BigDecimal.valueOf(77.581025), BigDecimal.valueOf(79.7225), 533098760));
        quotes.add(new Quote(LocalDate.parse("2020-05-25"), BigDecimal.valueOf(80.875), BigDecimal.valueOf(81.06), BigDecimal.valueOf(78.2725), BigDecimal.valueOf(79.485), 525861450));
        quotes.add(new Quote(LocalDate.parse("2020-06-01"), BigDecimal.valueOf(79.4375), BigDecimal.valueOf(82.9375), BigDecimal.valueOf(79.3025), BigDecimal.valueOf(82.875), 497963210));
        quotes.add(new Quote(LocalDate.parse("2020-06-08"), BigDecimal.valueOf(82.5625), BigDecimal.valueOf(88.6925), BigDecimal.valueOf(81.83), BigDecimal.valueOf(84.7), 811827160));
        quotes.add(new Quote(LocalDate.parse("2020-06-15"), BigDecimal.valueOf(83.3125), BigDecimal.valueOf(89.14), BigDecimal.valueOf(83.145), BigDecimal.valueOf(87.43), 779940340));
        quotes.add(new Quote(LocalDate.parse("2020-06-22"), BigDecimal.valueOf(87.835), BigDecimal.valueOf(93.095), BigDecimal.valueOf(87.7875), BigDecimal.valueOf(88.4075), 883003490));
        quotes.add(new Quote(LocalDate.parse("2020-06-29"), BigDecimal.valueOf(88.3125), BigDecimal.valueOf(92.6175), BigDecimal.valueOf(87.82), BigDecimal.valueOf(91.0275), 495648060));
        quotes.add(new Quote(LocalDate.parse("2020-07-06"), BigDecimal.valueOf(92.5), BigDecimal.valueOf(96.3175), BigDecimal.valueOf(92.4675), BigDecimal.valueOf(95.92), 564071920));
        quotes.add(new Quote(LocalDate.parse("2020-07-13"), BigDecimal.valueOf(97.265), BigDecimal.valueOf(99.955), BigDecimal.valueOf(93.8775), BigDecimal.valueOf(96.3275), 718601010));
        quotes.add(new Quote(LocalDate.parse("2020-07-20"), BigDecimal.valueOf(96.41625), BigDecimal.valueOf(99.25), BigDecimal.valueOf(89.145), BigDecimal.valueOf(92.615), 665408690));
        quotes.add(new Quote(LocalDate.parse("2020-07-27"), BigDecimal.valueOf(93.71), BigDecimal.valueOf(106.415), BigDecimal.valueOf(93.2475), BigDecimal.valueOf(106.26), 847594440));
        quotes.add(new Quote(LocalDate.parse("2020-08-03"), BigDecimal.valueOf(108.2), BigDecimal.valueOf(114.4125), BigDecimal.valueOf(107.8925), BigDecimal.valueOf(111.1125), 1003410220));
        quotes.add(new Quote(LocalDate.parse("2020-08-10"), BigDecimal.valueOf(112.6), BigDecimal.valueOf(116.0425), BigDecimal.valueOf(109.106675), BigDecimal.valueOf(114.9075), 941897890));
        quotes.add(new Quote(LocalDate.parse("2020-08-17"), BigDecimal.valueOf(116.0625), BigDecimal.valueOf(124.868), BigDecimal.valueOf(113.962525), BigDecimal.valueOf(124.37), 835694820));
        quotes.add(new Quote(LocalDate.parse("2020-08-24"), BigDecimal.valueOf(128.6975), BigDecimal.valueOf(128.785), BigDecimal.valueOf(123.0525), BigDecimal.valueOf(124.8075), 1063638120));
        quotes.add(new Quote(LocalDate.parse("2020-08-31"), BigDecimal.valueOf(127.58), BigDecimal.valueOf(137.98), BigDecimal.valueOf(110.89), BigDecimal.valueOf(120.96), 1168498620));
        quotes.add(new Quote(LocalDate.parse("2020-09-07"), BigDecimal.valueOf(113.95), BigDecimal.valueOf(120.5), BigDecimal.valueOf(110.0), BigDecimal.valueOf(112.0), 771441740));
        quotes.add(new Quote(LocalDate.parse("2020-09-14"), BigDecimal.valueOf(114.72), BigDecimal.valueOf(118.829), BigDecimal.valueOf(106.09), BigDecimal.valueOf(106.84), 944934660));
        quotes.add(new Quote(LocalDate.parse("2020-09-21"), BigDecimal.valueOf(104.54), BigDecimal.valueOf(112.86), BigDecimal.valueOf(103.1), BigDecimal.valueOf(112.28), 847212650));
        quotes.add(new Quote(LocalDate.parse("2020-09-28"), BigDecimal.valueOf(115.01), BigDecimal.valueOf(117.72), BigDecimal.valueOf(112.22), BigDecimal.valueOf(113.02), 641240540));
        quotes.add(new Quote(LocalDate.parse("2020-10-05"), BigDecimal.valueOf(113.91), BigDecimal.valueOf(117.0), BigDecimal.valueOf(112.25), BigDecimal.valueOf(116.97), 548575050));
        quotes.add(new Quote(LocalDate.parse("2020-10-12"), BigDecimal.valueOf(120.06), BigDecimal.valueOf(125.39), BigDecimal.valueOf(118.15), BigDecimal.valueOf(119.02), 881572560));
        quotes.add(new Quote(LocalDate.parse("2020-10-19"), BigDecimal.valueOf(119.96), BigDecimal.valueOf(120.419), BigDecimal.valueOf(114.28), BigDecimal.valueOf(115.04), 519569640));
        quotes.add(new Quote(LocalDate.parse("2020-10-26"), BigDecimal.valueOf(114.01), BigDecimal.valueOf(117.28), BigDecimal.valueOf(107.72), BigDecimal.valueOf(108.86), 684767900));
        quotes.add(new Quote(LocalDate.parse("2020-11-02"), BigDecimal.valueOf(109.11), BigDecimal.valueOf(119.62), BigDecimal.valueOf(107.32), BigDecimal.valueOf(118.69), 609571820));
        quotes.add(new Quote(LocalDate.parse("2020-11-09"), BigDecimal.valueOf(120.5), BigDecimal.valueOf(121.99), BigDecimal.valueOf(114.13), BigDecimal.valueOf(119.26), 589872920));
        quotes.add(new Quote(LocalDate.parse("2020-11-16"), BigDecimal.valueOf(118.92), BigDecimal.valueOf(120.99), BigDecimal.valueOf(116.81), BigDecimal.valueOf(117.34), 389493360));
        quotes.add(new Quote(LocalDate.parse("2020-11-23"), BigDecimal.valueOf(117.18), BigDecimal.valueOf(117.6202), BigDecimal.valueOf(112.59), BigDecimal.valueOf(116.59), 365024100));
        quotes.add(new Quote(LocalDate.parse("2020-11-30"), BigDecimal.valueOf(116.97), BigDecimal.valueOf(123.78), BigDecimal.valueOf(116.81), BigDecimal.valueOf(122.25), 543809230));
        quotes.add(new Quote(LocalDate.parse("2020-12-07"), BigDecimal.valueOf(122.31), BigDecimal.valueOf(125.95), BigDecimal.valueOf(120.15), BigDecimal.valueOf(122.41), 452278650));
        quotes.add(new Quote(LocalDate.parse("2020-12-14"), BigDecimal.valueOf(122.6), BigDecimal.valueOf(129.58), BigDecimal.valueOf(121.54), BigDecimal.valueOf(126.655), 621758150));
        quotes.add(new Quote(LocalDate.parse("2020-12-21"), BigDecimal.valueOf(125.02), BigDecimal.valueOf(134.405), BigDecimal.valueOf(123.449), BigDecimal.valueOf(131.97), 433757140));
        quotes.add(new Quote(LocalDate.parse("2020-12-28"), BigDecimal.valueOf(133.99), BigDecimal.valueOf(138.789), BigDecimal.valueOf(131.72), BigDecimal.valueOf(132.69), 441102270));
        quotes.add(new Quote(LocalDate.parse("2021-01-04"), BigDecimal.valueOf(133.52), BigDecimal.valueOf(133.6116), BigDecimal.valueOf(126.382), BigDecimal.valueOf(132.05), 610791160));
        quotes.add(new Quote(LocalDate.parse("2021-01-11"), BigDecimal.valueOf(129.19), BigDecimal.valueOf(131.45), BigDecimal.valueOf(126.86), BigDecimal.valueOf(127.14), 483029140));
        quotes.add(new Quote(LocalDate.parse("2021-01-18"), BigDecimal.valueOf(127.78), BigDecimal.valueOf(139.85), BigDecimal.valueOf(126.938), BigDecimal.valueOf(139.07), 430065720));
        quotes.add(new Quote(LocalDate.parse("2021-01-25"), BigDecimal.valueOf(143.07), BigDecimal.valueOf(145.09), BigDecimal.valueOf(130.21), BigDecimal.valueOf(131.96), 716990970));
        quotes.add(new Quote(LocalDate.parse("2021-02-01"), BigDecimal.valueOf(133.75), BigDecimal.valueOf(137.42), BigDecimal.valueOf(130.93), BigDecimal.valueOf(136.76), 439303020));
        quotes.add(new Quote(LocalDate.parse("2021-02-08"), BigDecimal.valueOf(136.03), BigDecimal.valueOf(137.877), BigDecimal.valueOf(133.6921), BigDecimal.valueOf(135.37), 345543150));
        quotes.add(new Quote(LocalDate.parse("2021-02-15"), BigDecimal.valueOf(135.49), BigDecimal.valueOf(136.01), BigDecimal.valueOf(127.41), BigDecimal.valueOf(129.87), 363187150));
        quotes.add(new Quote(LocalDate.parse("2021-02-22"), BigDecimal.valueOf(128.01), BigDecimal.valueOf(129.72), BigDecimal.valueOf(118.39), BigDecimal.valueOf(121.26), 685989270));
        quotes.add(new Quote(LocalDate.parse("2021-03-01"), BigDecimal.valueOf(123.75), BigDecimal.valueOf(128.72), BigDecimal.valueOf(117.57), BigDecimal.valueOf(121.42), 663456760));
        quotes.add(new Quote(LocalDate.parse("2021-03-08"), BigDecimal.valueOf(120.93), BigDecimal.valueOf(123.21), BigDecimal.valueOf(116.21), BigDecimal.valueOf(121.03), 586977280));
        quotes.add(new Quote(LocalDate.parse("2021-03-15"), BigDecimal.valueOf(121.41), BigDecimal.valueOf(127.22), BigDecimal.valueOf(119.675), BigDecimal.valueOf(119.99), 626770400));
        quotes.add(new Quote(LocalDate.parse("2021-03-22"), BigDecimal.valueOf(120.33), BigDecimal.valueOf(124.24), BigDecimal.valueOf(118.92), BigDecimal.valueOf(121.21), 488825830));
        quotes.add(new Quote(LocalDate.parse("2021-03-29"), BigDecimal.valueOf(121.65), BigDecimal.valueOf(124.18), BigDecimal.valueOf(118.86), BigDecimal.valueOf(123.0), 359904080));
        quotes.add(new Quote(LocalDate.parse("2021-04-05"), BigDecimal.valueOf(123.87), BigDecimal.valueOf(133.04), BigDecimal.valueOf(123.07), BigDecimal.valueOf(132.995), 447820440));
        quotes.add(new Quote(LocalDate.parse("2021-04-12"), BigDecimal.valueOf(132.52), BigDecimal.valueOf(135.0), BigDecimal.valueOf(130.63), BigDecimal.valueOf(134.16), 444178800));
        quotes.add(new Quote(LocalDate.parse("2021-04-19"), BigDecimal.valueOf(133.51), BigDecimal.valueOf(135.53), BigDecimal.valueOf(131.3001), BigDecimal.valueOf(134.32), 421246940));
        quotes.add(new Quote(LocalDate.parse("2021-04-26"), BigDecimal.valueOf(134.83), BigDecimal.valueOf(137.07), BigDecimal.valueOf(131.065), BigDecimal.valueOf(131.46), 501621390));
        quotes.add(new Quote(LocalDate.parse("2021-05-03"), BigDecimal.valueOf(132.04), BigDecimal.valueOf(134.07), BigDecimal.valueOf(126.7), BigDecimal.valueOf(130.21), 453802330));
        quotes.add(new Quote(LocalDate.parse("2021-05-10"), BigDecimal.valueOf(129.41), BigDecimal.valueOf(129.54), BigDecimal.valueOf(122.25), BigDecimal.valueOf(127.45), 514165630));
        quotes.add(new Quote(LocalDate.parse("2021-05-17"), BigDecimal.valueOf(126.82), BigDecimal.valueOf(128.0), BigDecimal.valueOf(122.86), BigDecimal.valueOf(125.43), 386352100));
        quotes.add(new Quote(LocalDate.parse("2021-05-24"), BigDecimal.valueOf(126.01), BigDecimal.valueOf(128.32), BigDecimal.valueOf(124.55), BigDecimal.valueOf(124.61), 357615060));
        quotes.add(new Quote(LocalDate.parse("2021-05-31"), BigDecimal.valueOf(125.08), BigDecimal.valueOf(126.16), BigDecimal.valueOf(123.13), BigDecimal.valueOf(125.89), 278314490));
        quotes.add(new Quote(LocalDate.parse("2021-06-07"), BigDecimal.valueOf(126.17), BigDecimal.valueOf(128.46), BigDecimal.valueOf(124.8321), BigDecimal.valueOf(127.35), 327048060));
        quotes.add(new Quote(LocalDate.parse("2021-06-14"), BigDecimal.valueOf(127.82), BigDecimal.valueOf(132.55), BigDecimal.valueOf(127.07), BigDecimal.valueOf(130.46), 457142830));
        quotes.add(new Quote(LocalDate.parse("2021-06-21"), BigDecimal.valueOf(130.3), BigDecimal.valueOf(134.64), BigDecimal.valueOf(129.21), BigDecimal.valueOf(133.11), 354155880));

        return quotes;
    }

}
