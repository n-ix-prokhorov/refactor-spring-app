package com.example.refactoringtask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


public class Service {

    public Collection<CourierRewards> calculateDeliveryPayImpactFor(Courier courier,
                                                                    List<Route> orderDeliveryRoutes,
                                                                    List<OrderDeliveryData> orderDeliveryData,
                                                                    List<CustomerDeliveryFeedback> deliveryFeedbacks,
                                                                    List<DeliveryCompletenessRewardEnrollmentRule> deliveryCompletenessRewardEnrollmentRules) {
        var allReceivedRewards = new ArrayList<CourierRewards>();

        for (var deliveryFeedback : deliveryFeedbacks) {
            switch (deliveryFeedback.type) {
                case FREE_STANDARD_DELIVERY:
                    if (courier.isEligibleForReward()) {
                        var rewardEnrollmentRules = new ArrayList<DeliveryCompletenessRewardEnrollmentRule>();
                        for (var orderDeliveryRoute : orderDeliveryRoutes) {
                            if (orderDeliveryRoute.getOrderId() == deliveryFeedback.getOrderId()) {
                                if (orderDeliveryRoute.isSaturatedByTrafficJams()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.TrafficProblemBypassingReward.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                                if (orderDeliveryRoute.hasLargeDistance()) {
                                    if (orderDeliveryRoute.isGasolinePayed()) {
                                        rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                                .filter(rule -> RuleType.DistanceProblemBypassingReward.equals(rule.getType()))
                                                .collect(Collectors.toUnmodifiableList()));
                                    }
                                }
                                if (deliveryFeedback.wasDeliveredInTime()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.EarlyDelivery.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                            }
                        }

                        for (var rewardEnrollmentRule : rewardEnrollmentRules) {
                            allReceivedRewards.addAll(rewardEnrollmentRule.process(courier, deliveryFeedback));
                        }
                    }
                case FREE_PREMIUM_DELIVERY:
                    if (courier.isEligibleForReward()) {
                        var rewardEnrollmentRules = new ArrayList<DeliveryCompletenessRewardEnrollmentRule>();

                        for (var orderDeliveryRoute : orderDeliveryRoutes) {
                            if (orderDeliveryRoute.getOrderId() == deliveryFeedback.getOrderId()) {
                                if (deliveryFeedback.wasDeliveredOnWeekendsOrNationalHoliday()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.WeekendsOrHolidayReward.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                                if (!deliveryFeedback.wasDamaged()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.PremiumNotDamagedReward.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                            }
                        }
                        for (var rewardEnrollmentRule : rewardEnrollmentRules) {
                            allReceivedRewards.addAll(rewardEnrollmentRule.process(courier, deliveryFeedback));
                        }
                    }
                case PRIORITY_GLOBAL_DELIVERY:
                    if (courier.isEligibleForReward()) {
                        var rewardEnrollmentRules = new ArrayList<DeliveryCompletenessRewardEnrollmentRule>();

                        for (var orderDeliveryRoute : orderDeliveryRoutes) {
                            if (orderDeliveryRoute.getOrderId() == deliveryFeedback.getOrderId()) {
                                if (orderDeliveryRoute.hadCustomsDeclarationReturns()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.CustomsProblemBypassingReward.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                                var deliveryRepresentation = orderDeliveryData.stream()
                                        .filter(currentOrderDeliveryData -> Objects.equals(currentOrderDeliveryData.getOrderId(), deliveryFeedback.getOrderId()))
                                        .findFirst();
                                if (deliveryRepresentation.isPresent() && deliveryRepresentation.get().isOrderProperlyPacked() && !deliveryFeedback.wasDamaged()) {
                                    rewardEnrollmentRules.addAll(deliveryCompletenessRewardEnrollmentRules.stream()
                                            .filter(rule -> RuleType.GlobalNotDamagedReward.equals(rule.getType()))
                                            .collect(Collectors.toUnmodifiableList()));
                                }
                            }
                        }
                        for (var rewardEnrollmentRule : rewardEnrollmentRules) {
                            allReceivedRewards.addAll(rewardEnrollmentRule.process(courier, deliveryFeedback));
                        }
                    }
                case PRIORITY_LOCAL_DELIVERY:
                    // new feature
                case PAID_STANDARD_DELIVERY:
                    // new feature
                case PAID_PREMIUM_DELIVERY:
                    // new feature
                default:

            }
        }
        return allReceivedRewards;
    }

    enum DeliveryType {
        FREE_STANDARD_DELIVERY, PAID_STANDARD_DELIVERY, FREE_PREMIUM_DELIVERY, PAID_PREMIUM_DELIVERY, PRIORITY_GLOBAL_DELIVERY, PRIORITY_LOCAL_DELIVERY,
    }

    class Courier {

        public boolean isEligibleForReward() {
            return new Random().nextBoolean();
        }
    }

    class Route {


        boolean isSaturatedByTrafficJams() {
            return new Random().nextBoolean();
        }

        public boolean hasLargeDistance() {
            return new Random().nextBoolean();
        }

        public boolean isGasolinePayed() {
            return new Random().nextBoolean();
        }

        public boolean hadCustomsDeclarationReturns() {
            return new Random().nextBoolean();
        }

        public long getOrderId() {
            return new Random().nextLong();
        }
    }

    class CustomerDeliveryFeedback {
        DeliveryType type;

        public boolean wasDeliveredOnWeekendsOrNationalHoliday() {
            return new Random().nextBoolean();
        }

        public boolean wasDeliveredInTime() {
            return new Random().nextBoolean();
        }

        public boolean wasDamaged() {
            return new Random().nextBoolean();
        }

        public long getOrderId() {
            return new Random().nextLong();
        }

    }

    class OrderDeliveryData {

        public boolean isOrderProperlyPacked() {
            return new Random().nextBoolean();
        }

        public long getOrderId() {
            return new Random().nextLong();
        }
    }

    abstract class EnrollmentRule {
        RuleType type;

        public abstract Collection<? extends CourierRewards> process(Courier courier, CustomerDeliveryFeedback deliveryFeedback);

        public RuleType getType() {
            return null;
        }
    }

    class DeliveryCompletenessRewardEnrollmentRule extends EnrollmentRule {

        @Override
        public Collection<? extends CourierRewards> process(Courier courier, CustomerDeliveryFeedback deliveryFeedback) {
            return null;
        }
    }

    enum RuleType {
        TrafficProblemBypassingReward, DistanceProblemBypassingReward, EarlyDelivery, WeekendsOrHolidayReward, PremiumNotDamagedReward, CustomsProblemBypassingReward, GlobalNotDamagedReward
    }

    abstract class CourierRewards {
        abstract BigDecimal getBonus();
    }


}
