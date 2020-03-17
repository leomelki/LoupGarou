## Table des matières

- [À propos](#à-propos)
- [Installation](#installation)
  - [Dépendances requises](#dépendances-requises)
- [Commandes](#commandes)
- [Crédits](#crédits)
- [Aide](#aide)
  - [Questions fréquentes](#questions-fréquentes)

## À propos

Le mode Loup-Garou est un mode inspiré du jeu de société [Les Loups-Garous de Thiercelieux](https://fr.wikipedia.org/wiki/Les_Loups-garous_de_Thiercelieux) reprenant son fonctionnement ainsi que sa manière d'être joué, à la seule différence qu'aucun maître du jeu n'est requis, le déroulement de chaque partie étant entièrement automatisé :

- Déroulement de la partie automatisé
- Rôles du jeu de base, et nouveaux rôles
- Utilisable sur n'importe quelle map

## Installation

**Minecraft 1.15.1 est requis.**  
Déplacez simplement le plugin compilé [LoupGarou.jar](https://github.com/leomelki/LoupGarou/releases) dans le dossier `plugins` de votre serveur avant de le redémarrer.

### Dépendances requises

- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

## Commandes

`/lg roles` : Retourne la liste des rôles dans la partie  
`/lg roles set <ID> <MONTANT>` : Définit le nombre de joueurs pour un certain rôle  
`/lg addSpawn` : Ajoute un point de spawn (emplacement de joueur)  
`/lg start <PSEUDO>` : Lance la partie  
`/lg end <PSEUDO>` : Arrête une partie  
`/lg reloadConfig` : Recharge la configuration  
`/lg joinAll` : À utiliser après avoir changé les rôles  

## Crédits

- Chef de Projet : [Shytoos](https://twitter.com/shytoos_)
- Développement : [Leomelki](https://twitter.com/leomelki)
- Mapping : [Cosii](https://www.youtube.com/channel/UCwyOcA41QSk590fl9L0ys8A)

## Aide

Par soucis de temps, nous ne pouvons pas faire de support au cas par cas, mais vous pouvez rejoindre notre serveur [Discord](https://discord.gg/Squeezie) pour trouver de l'aide auprès de la communauté.

### Questions fréquentes

- Que faire en cas de problème d'affichage (votes bloqués au dessus des têtes, etc...) ?  

Cela arrive après avoir `reload` au cours d'une partie, tous les joueurs qui ont ce problème doivent se déconnecter et se reconnecter.

- Pourquoi la partie ne se lance pas ?  

Il faut taper la commande `/lg start <PSEUDO>` en mettant le pseudo d'un des joueurs qui sera présent dans la partie. Si cela ne fonctionne toujours pas, c'est parce qu'il n'y a pas suffisamment de rôles pour le nombre de joueurs, il doit y avoir le même nombre de rôles qu'il y aura de joueurs dans la partie. N'oubliez pas de taper `/lg joinAll` après avoir modifié la liste des rôles.

- J'ai mal placés mes spawns ou je veux utiliser une nouvelle map, comment faire ?  

Il suffit d'ouvrir le fichier `plugins\LoupGarou\config.yml` et de supprimer les points de spawn.