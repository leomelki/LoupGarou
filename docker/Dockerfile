ARG SPIGOT_VERSION=1.15.1

# Build Loup-Garou project
FROM maven:3.3-jdk-8 as LOUP_GAROU_BUILDER
WORKDIR /app
COPY . .
RUN mvn clean install
RUN mvn package

# Build Spigot project
FROM openjdk:8 AS SPIGOT_BUILDER
ARG SPIGOT_VERSION
WORKDIR /spigot
RUN apt-get update
ADD https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar buildtools.jar
RUN git config --global --unset core.autocrlf || true
RUN java -Xmx1024M -jar buildtools.jar --rev ${SPIGOT_VERSION}

# Final server image
FROM openjdk:8
ARG SPIGOT_VERSION
WORKDIR /_minecraft
COPY --from=LOUP_GAROU_BUILDER /app/target/LoupGarou.jar ./plugins/
ADD https://github.com/dmulloy2/ProtocolLib/releases/download/4.5.0/ProtocolLib.jar ./plugins
COPY --from=SPIGOT_BUILDER /spigot/spigot-${SPIGOT_VERSION}.jar ./spigot.jar

VOLUME /minecraft
WORKDIR /minecraft
EXPOSE 25565
CMD cp -rn /_minecraft/* . && tail -f /dev/null
