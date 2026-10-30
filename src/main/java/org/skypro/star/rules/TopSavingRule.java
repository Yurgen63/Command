package org.skypro.star.rules;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.utils.Constants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели.\n" +
            "\n" +
            " Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
            "\n" +
            "Преимущества «Копилки»:\n" +
            "\n" +
            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
            "\n" +
            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
            "\n" +
            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
            "\n" +
            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!";

    public TopSavingRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {

        boolean hasDebit = repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT);
        if (!hasDebit) {
            return Optional.empty();
        }

        var stats = repository.getTopSavingStats(userId);

        BigDecimal threshold = new BigDecimal("50000");
        boolean isEnoughDeposits = stats.debitDeposits().compareTo(threshold) >= 0
                || stats.savingDeposits().compareTo(threshold) >= 0;

        boolean isDepositsMoreThanWithdrawals = stats.debitDeposits().compareTo(stats.debitWithdrawals()) > 0;

        if (isEnoughDeposits && isDepositsMoreThanWithdrawals) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}