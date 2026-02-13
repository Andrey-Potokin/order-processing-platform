.PHONY: build dev-up dev-down clean health

# Собрать все сервисы через Maven
build:
	mvn clean package -DskipTests

# Собрать и запустить всё через docker-compose
dev-up: build
	docker-compose up --build -d
	@echo "✅ Все сервисы запущены. Проверьте http://localhost:8080/swagger-ui.html"

# Остановить и удалить контейнеры
dev-down:
	docker-compose down
	@echo "⏹️ Все сервисы остановлены"

# Проверить health всех сервисов
health:
	@echo "🔍 Проверка состояния сервисов..."
	@for port in 8080 8081 8082 8083 8084 8085 8086; do \
		echo "➡️  Проверка http://localhost:$$port/actuator/health"; \
		curl -s http://localhost:$$port/actuator/health | grep -q "UP" && \
		echo "✅ Сервис на порту $$port доступен" || \
		echo "❌ Сервис на порту $$port НЕДОСТУПЕН"; \
	done

# Очистка (JAR, Docker-образы)
clean:
	mvn clean
	docker-compose down --rmi all --volumes
	@echo "🧹 Кеш и образы очищены"