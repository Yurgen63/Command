package org.skypro.star.rules;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.repository.RecommendationsRepository;
import org.skypro.star.utils.Constants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRuleSet {
    private final RecommendationsRepository repository;

    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!\n" +
            "\n" +
            " Воспользуйтесь налоговыми льготами и начните инвестировать с умом.\n" +
            "\n" +
            "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде.\n" +
            "\n" +
            "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями.\n" +
            "\n" +
            "Откройте ИИС сегодня и станьте ближе к финансовой независимости!\n";

    public Invest500Rule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {

        boolean hasDebit = repository.hasProductType(userId, Constants.PRODUCT_TYPE_DEBIT);

        boolean hasInvest = repository.hasProductType(userId, Constants.PRODUCT_TYPE_INVEST);

        BigDecimal savingDeposits = repository.getSumAmount(userId, Constants.PRODUCT_TYPE_SAVING, Constants.TRANSACTION_TYPE_DEPOSIT);
        boolean savingEnough = savingDeposits.compareTo(new BigDecimal("1000")) > 0;

        if (hasDebit && !hasInvest && savingEnough) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }

        return Optional.empty();
    }
}

