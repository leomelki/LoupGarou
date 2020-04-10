# Build Loup-Garou project
FROM maven:3.3-jdk-8 as LOUP_GAROU_BUILDER
WORKDIR /app
COPY . .
RUN mvn clean install
RUN mvn package

# Start the minecraft server
FROM itzg/minecraft-server:latest

ENV TYPE=SPIGOT
ENV VERSION=1.15.1

WORKDIR /plugins
COPY --from=LOUP_GAROU_BUILDER /app/target/LoupGarou.jar .
ADD https://github.com/dmulloy2/ProtocolLib/releases/download/4.5.0/ProtocolLib.jar .
RUN chmod 777 ProtocolLib.jar
EXPOSE 25565
