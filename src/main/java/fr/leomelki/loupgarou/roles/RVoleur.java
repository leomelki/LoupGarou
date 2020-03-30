package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGDayEndEvent;
import fr.leomelki.loupgarou.events.LGDayStartEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import org.bukkit.event.EventHandler;

public class RVoleur extends fr.leomelki.loupgarou.roles.Role {

    public RVoleur(LGGame game) {
        super(game);
    }

    @Override
    public String getName() {
        return "§a§lVoleur";
    }

    @Override
    public String getFriendlyName() {
        return "du "+getName();
    }

    @Override
    public String getShortDescription() {
        return "Tu gagnes avec le §a§lVillage";
    }

    @Override
    public String getDescription() {
        return "Ton objectif sera celui de ta nouvelle carte. Chaque nuit, tu pourras choisir un joueur avec lequel tu échangeras ta carte. Il deviendra "+getName()+"§f à son tour. Mais si le village décide d'éliminer un §a§lVillageois§f, tu auras trop peur pour voler la nuit suivante.";
    }

    @Override
    public String getTask() {
        return canPlay ? "Avec qui veux-tu échanger ta carte ?" : "";
    }

    @Override
    public String getBroadcastedTask() {
        return canPlay ? "Le "+getName()+"§9 cherche une cible..." : "";
    }

    @Override
    public RoleType getType() {
        return RoleType.VILLAGER;
    }

    @Override
    public RoleWinType getWinType() {
        return RoleWinType.VILLAGE;
    }

    @Override
    public int getTimeout() {
        return canPlay ? 15 : 2;
    }

    @Override
    public void onNightTurn(LGPlayer player, Runnable callback) {
        if(!canPlay) {
            player.sendMessage("§6Vous avez encore peur des résultats du dernier vote. Vous ne pouvez pas voler cette nuit.");
            player.sendTitle("§6Tu as trop peur !", "§7Tu as trop peur pour voler cette nuit.", 100);
            callback.run();
            return;
        }
        player.showView();
        player.sendMessage("§6Choisissez votre cible.");
        player.choose(choosen -> {
            if(choosen != null) {
                player.stopChoosing();
                player.sendMessage("§6Tu as volé la carte de §4§l"+choosen.getName()+"§6. Tu es maintenant "+choosen.getRole().getName()+"§6.");
                player.hideView();

                choosen.sendMessage("§cTu t'es fais volé ta carte pendant la nuit. Tu es maintenant "+getName()+"§6.");
                choosen.sendTitle("§cVolé !", "§6Ta carte a été volée.", 100);

                Role choosenRole = choosen.getRole();
                if(choosenRole != null) {
                    // On regarde si la personne choisie fait partie des Loups-Garous
                    for(Role role : getGame().getRoles())
                        if(role instanceof RLoupGarou)
                            if(role.getPlayers().contains(choosen)) {
                                role.getPlayers().remove(choosen);
                            }

                    choosenRole.getPlayers().remove(choosen);
                    this.getPlayers().add(choosen);
                    choosen.setRole(this);
                    LGCustomItems.updateItem(choosen);
                    choosen.updateOwnSkin();
                    choosen.updatePrefix();
                    choosen.hideView(); // Sinon il regagne la vue

                    getPlayers().remove(player);
                    choosenRole.join(player, false);
                    player.setRole(choosenRole);
                    LGCustomItems.updateItem(player);
                    player.updateOwnSkin();
                    choosen.updatePrefix();
                    player.hideView();

                    getGame().updateRoleScoreboard(); // Même si rien ne change...
                } else
                    throw new NullPointerException("La personne visée par le voleur n'a pas de role.");

                callback.run();
            }
        }, player);
    }

    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
        player.sendMessage("§6Tu as choisi de ne voler personne. Tu restes "+getName()+"§6.");
        player.sendActionBarMessage("§6Tu as décidé de ne voler personne.");
    }

    private boolean canPlay = true;

    @EventHandler
    public void onPlayerKill(LGPlayerKilledEvent e) {
            canPlay = e.getKilled().getRoleWinType() != RoleWinType.VILLAGE;
    }

    @EventHandler
    public void onDayStart(LGDayStartEvent e) {
        if(e.getGame() != getGame()) return;

        canPlay = true;
    }
}
