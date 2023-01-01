FROM maven:3.8.1-openjdk-16
WORKDIR /tests
COPY . .
CMD mvn clean -Dtest=tests/* test
