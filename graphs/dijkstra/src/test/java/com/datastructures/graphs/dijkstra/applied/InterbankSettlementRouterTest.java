package com.datastructures.graphs.dijkstra.applied;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InterbankSettlementRouterTest {

    private final BankAccount originAccount = new BankAccount("BANK-A", "0001");
    private final BankAccount correspondentAccount = new BankAccount("BANK-B", "0002");
    private final BankAccount destinationAccount = new BankAccount("BANK-C", "0003");
    private final BankAccount unreachableAccount = new BankAccount("BANK-Z", "9999");

    @Test
    void findsTheCheapestFeeAcrossACorrespondentBankHopEvenWhenADirectRailIsMoreExpensive() {
        InterbankSettlementRouter router = new InterbankSettlementRouter();
        router.registerRail(originAccount, destinationAccount, 900); // expensive direct PIX rail
        router.registerRail(originAccount, correspondentAccount, 100); // TED hop
        router.registerRail(correspondentAccount, destinationAccount, 150); // Boleto hop

        Optional<Long> cheapestFee = router.cheapestSettlementFee(originAccount, destinationAccount);

        assertThat(cheapestFee).contains(250L);
    }

    @Test
    void noKnownChainOfRailsReturnsAnEmptyResult() {
        InterbankSettlementRouter router = new InterbankSettlementRouter();
        router.registerRail(originAccount, correspondentAccount, 100);

        Optional<Long> cheapestFee = router.cheapestSettlementFee(originAccount, unreachableAccount);

        assertThat(cheapestFee).isEmpty();
    }
}
