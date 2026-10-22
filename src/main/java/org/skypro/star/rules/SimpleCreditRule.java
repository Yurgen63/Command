package org.skypro.star.rules;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.utils.Constants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT = "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную сумму? \n" +
            "\n" +
            " Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
            "\n" +
            "Почему выбирают нас:\n" +
            "\n" +
            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
            "\n" +
            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
            "\n" +
            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
            "\n" +
            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    public SimpleCreditRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {

        boolean hasCredit = repository.hasProductType(userId, Constants.PRODUCT_TYPE_CREDIT);
        if (hasCredit) {
            return Optional.empty();
        }

        var stats = repository.getTopSavingStats(userId);

        boolean isDepositsMoreThanWithdrawals = stats.debitDeposits().compareTo(stats.debitWithdrawals()) > 0;

        boolean isWithdrawalsEnough = stats.debitWithdrawals().compareTo(new BigDecimal("100000")) > 0;

        if (isDepositsMoreThanWithdrawals && isWithdrawalsEnough) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}