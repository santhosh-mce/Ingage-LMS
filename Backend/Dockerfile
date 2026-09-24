FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8000

CMD ["sh", "-c", "java -jar target/*.jar"]