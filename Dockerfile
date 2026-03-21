# --- Stage 1: Сборка фронтенда ---
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# --- Stage 2: Сборка бэкенда ---
FROM eclipse-temurin:21-jdk-alpine AS backend
WORKDIR /app
COPY gradlew ./
COPY gradle/ gradle/
RUN chmod +x gradlew
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./
COPY src/ src/
COPY --from=frontend /app/frontend/dist/ src/main/resources/static/
RUN ./gradlew bootJar -x test -x buildFrontend -x copyFrontend --no-daemon

# --- Stage 3: Запуск (Debian для совместимости с Playwright) ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /app/build/libs/hh-*-SNAPSHOT.jar app.jar
# Playwright требует системные зависимости для Chromium
RUN apt-get update && apt-get install -y --no-install-recommends \
    libnss3 libatk1.0-0t64 libatk-bridge2.0-0t64 libcups2t64 libdrm2 \
    libxkbcommon0 libxcomposite1 libxdamage1 libxrandr2 libgbm1 \
    libpango-1.0-0 libasound2t64 libxshmfence1 libxfixes3 libcairo2 \
    fonts-liberation \
    && rm -rf /var/lib/apt/lists/*
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
