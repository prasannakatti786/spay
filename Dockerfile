FROM --platform=linux/amd64 openjdk:17-oracle
LABEL authors="spondias"
ADD  target/wallet_server.jar wallet_server.jar
ENTRYPOINT ["java","-jar", "wallet_server.jar"]