package org.skypro.star.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StarRecommendationBot extends TelegramLongPollingBot {

    private final BotRecommendationService botService;
    private final String username;

    public StarRecommendationBot(BotRecommendationService botService,
                                 @Value("${telegram.bot.token}") String token,
                                 @Value("${telegram.bot.username}") String username) {
        super(token);
        this.botService = botService;
        this.username = username;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        String response;
        if (text.equals("/start")) {
            response = """
                    Привет! Я бот рекомендаций банка.
                    Доступная команда:
                    /recommend <username> — получить рекомендации по пользователю
                    """;
        } else if (text.startsWith("/recommend")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length < 2 || parts[1].isBlank()) {
                response = "Использование: /recommend <username>";
            } else {
                response = botService.buildRecommendationMessage(parts[1].trim());
            }
        } else {
            response = "Неизвестная команда. Введите /start для справки.";
        }

        SendMessage msg = new SendMessage(chatId.toString(), response);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}