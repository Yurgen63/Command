# Сервис рекомендаций банка «Стар»

Сервис анализирует транзакции клиента банка и возвращает список релевантных
банковских продуктов. Доступен через REST API и Telegram-бота; поддерживает
как статические правила рекомендаций, так и динамические, управляемые через API.

## 📌 Что умеет

- **REST API** `GET /recommendation/{userId}` — рекомендации по `user_id` клиента.
- **Динамические правила** — создание, просмотр и удаление правил через `/rule` без перекомпиляции приложения; 4 типа запросов (`USER_OF`, `ACTIVE_USER_OF`, `TRANSACTION_SUM_COMPARE`, `TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW`) и флаг `negate`.
- **Статистика срабатываний** правил — `GET /rule/stats`.
- **Telegram-бот** — команда `/recommend <username>` возвращает персонализированное сообщение.
- **Служебные эндпоинты** — сброс кешей (`/management/clear-caches`) и информация о сервисе (`/management/info`).
- **Кеширование** результатов SQL-запросов в Caffeine; сброс кешей без перезапуска.

## 🛠 Стек технологий

| Слой | Технологии |
|------|-----------|
| Язык | Java 17 |
| Фреймворк | Spring Boot 3.3.5, Spring Web, Spring Data JPA |
| Доступ к данным | Spring JDBC (`JdbcTemplate`) для БД транзакций; JPA/Hibernate для БД правил |
| Базы данных | H2 (read-only, `transaction.mv.db`) — транзакции; PostgreSQL — динамические правила |
| Миграции | Liquibase |
| Кеширование | Caffeine |
| Интеграции | Telegram Bots (`telegrambots-spring-boot-starter`) |
| Документация API | Springdoc OpenAPI / Swagger UI |
| Тесты | JUnit 5, Mockito, Spring Boot Test (`@WebMvcTest`) |
| Сборка | Maven (`mvnw`) |

## 🚀 Быстрый старт

```bash
# 1. Собрать JAR (с тестами)
./mvnw clean package

# 2. Запустить приложение
java -jar target/star-0.0.1-SNAPSHOT.jar
```

После запуска:

- API: `http://localhost:8080/recommendation/{userId}`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Требования к окружению и переменные среды (PostgreSQL, H2, Telegram) — см. [Инструкцию по развертыванию](../../wiki/Deployment).


## 📚 Документация

Вся проектная документация — в [Wiki](../../wiki):

- [Главная страница Wiki](../../wiki) — обзор проекта.
- [Требования (User Story + NFR)](../../wiki/Requirements) — что и для кого делаем.
- [Таблица трейсинга](../../wiki/Traceability-Matrix) — требования → Issues → MR.
- [Архитектура](../../wiki/Architecture) — компонентная диаграмма, Activity Diagram, описание взаимодействия.
- [REST API (OpenAPI / Swagger)](../../wiki/REST-API) — эндпоинты, схемы, примеры.
- [Развертывание](../../wiki/Deployment) — сервисы, сборка, запуск, переменные среды.

## 🧪 Тесты

```bash
./mvnw test
```
Покрыты: статические правила (Invest500Rule, TopSavingRule, SimpleCreditRule), движок динамических правил (DynamicRuleChecker, RuleQueryFactory), REST-контракты (RecommendationController, RuleController), сервис рекомендаций. Итого — 36 тестов, все зелёные.

Javadoc генерируется командой ./mvnw javadoc:javadoc → target/site/apidocs/index.html.

## 🗂 Структура репозитория

```text
src/main/java/org/skypro/star/
├── bot/               # Telegram-бот и сервис сообщений
├── configuration/     # конфигурация DataSource, Telegram
├── controller/        # REST-контроллеры (/recommendation, /rule, /management)
├── dto/               # DTO для API
├── entity/            # JPA-сущности динамических правил
├── exception/         # глобальный обработчик исключений
├── mapper/            # мапперы entity ↔ DTO
├── repository/        # JDBC-репозиторий транзакций и JPA-репозитории правил
├── rules/             # статические правила и движок динамических правил
│   └── query/         # реализации типов запросов динамических правил
├── service/           # бизнес-логика
└── utils/             # константы
```

## 🔗 Полезные ссылки

- [Доска задач (GitHub Project)](https://github.com/users/Yurgen63/projects/1)
- [Исходный код репозитория](../../)
