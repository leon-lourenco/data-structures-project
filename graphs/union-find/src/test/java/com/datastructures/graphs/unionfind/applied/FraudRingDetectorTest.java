package com.datastructures.graphs.unionfind.applied;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FraudRingDetectorTest {

    @Test
    void entitiesLinkedDirectlyOrTransitivelyLandInTheSameCluster() {
        FraudRingDetector detector = new FraudRingDetector(10);
        detector.linkObservedTogether("account-1", "device-A");
        detector.linkObservedTogether("device-A", "account-2");
        // account-2 was already assigned an index above: reuses it (existing-entity branch).
        detector.linkObservedTogether("account-2", "phone-555");

        assertThat(detector.sameFraudCluster("account-1", "account-2")).isTrue();
        assertThat(detector.sameFraudCluster("account-1", "phone-555")).isTrue();
    }

    @Test
    void entitiesInDifferentClustersAreNotTreatedAsLinked() {
        FraudRingDetector detector = new FraudRingDetector(10);
        detector.linkObservedTogether("account-1", "device-A");
        detector.linkObservedTogether("account-2", "device-B");

        assertThat(detector.sameFraudCluster("account-1", "account-2")).isFalse();
    }

    @Test
    void anUnknownIdentifierOnEitherSideCanNeverShareACluster() {
        FraudRingDetector detector = new FraudRingDetector(10);
        detector.linkObservedTogether("account-1", "device-A");

        assertThat(detector.sameFraudCluster("ghost", "account-1")).isFalse();
        assertThat(detector.sameFraudCluster("account-1", "ghost")).isFalse();
    }

    @Test
    void exceedingTheConfiguredEntityCapacityThrows() {
        FraudRingDetector detector = new FraudRingDetector(2);
        detector.linkObservedTogether("account-1", "device-A"); // uses both available slots

        assertThatThrownBy(() -> detector.linkObservedTogether("account-2", "device-B"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("capacity");
    }
}
