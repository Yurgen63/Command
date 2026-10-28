package org.skypro.star.bot;

import org.skypro.star.dto.RecommendationDto;
import org.skypro.star.repository.UserRepository;
import org.skypro.star.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotRecommendationService {

    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    public BotRecommendationService(UserRepository userRepository,
                                    RecommendationService recommendationService) {
        this.userRepository = userRepository;
        this.recommendationService = recommendationService;
    }

    public String buildRecommendationMessage(String username) {
        List<UserRepository.UserRow> users = userRepository.findByUsername(username);

        if (users.size() != 1) {
            return "Пользователь не найден";
        }

        UserRepository.UserRow user = users.get(0);
        List<RecommendationDto> recommendations =
                recommendationService.getRecommendations(user.id());

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте ")
                .append(user.firstName()).append(' ')
                .append(user.lastName()).append("\n");
        sb.append("Новые продукты для вас:\n");

        if (recommendations.isEmpty()) {
            sb.append("- Пока нет новых рекомендаций\n");
        } else {
            for (RecommendationDto dto : recommendations) {
                sb.append("- ").append(dto.name()).append("\n");
            }
        }
        return sb.toString();
    }
}
