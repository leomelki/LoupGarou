FROM itzg/minecraft-server:adopt11

ENV TYPE=SPIGOT
ENV VERSION=1.15.2

WORKDIR /plugins
COPY target/LoupGarou.jar .
ADD --chown=minecraft:minecraft https://github.com/dmulloy2/ProtocolLib/releases/download/4.5.0/ProtocolLib.jar .

WORKDIR /data
