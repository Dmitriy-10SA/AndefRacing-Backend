# Сборка
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Создаем финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем пользователя для запуска приложения
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем собранный jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
