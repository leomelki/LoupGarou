package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.Random;

public class RLoupFeutrer extends fr.leomelki.loupgarou.roles.Role {
    public RLoupFeutrer(LGGame game) {
        super(game);
    }
    @Override
    public String getName() {
        return "§c§lLoup Feutrer";
    }
    @Override
    public String getFriendlyName() {
        return "des "+getName();
    }
    @Override
    public String getShortDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous";
    }
    @Override
    public String getDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous§f. Au début de la première nuit, tu peux choisir une personne, si la voyante te regarde elle verra le rôle de la personne que tu as choisis.";
    }
    @Override
    public String getTask() {
        return "Pour qui veux-tu te faire passer ?";
    }
    @Override
    public String getBroadcastedTask() {
        return "Le "+getName()+"§9 va faire semblant d'être une personne qu'il n'est pas...";
    }
    @Override
    public fr.leomelki.loupgarou.roles.RoleType getType() {
        return fr.leomelki.loupgarou.roles.RoleType.LOUP_GAROU;
    }
    @Override
    public fr.leomelki.loupgarou.roles.RoleWinType getWinType() {
        return fr.leomelki.loupgarou.roles.RoleWinType.LOUP_GAROU;
    }
    @Override
    public int getTimeout() {
        return 15;
    }

    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback){

        if(!player.getCache().has("loup_ftr_e")){
            player.showView();
            player.sendMessage("§6Choisissez votre exemple.");
            player.choose(new LGPlayer.LGChooseCallback() {

                @Override
                public void callback(LGPlayer choosen) {
                    if(choosen != null) {
                        player.stopChoosing();
                        player.sendMessage("§6Si la voyante te sonde, elle verra que tu as celui de §7§l"+choosen.getName()+"§6.");
                        player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton exemple");
                        player.getCache().set("loup_ftr_e", choosen);
                        getPlayers().remove(player);//Pour éviter qu'il puisse avoir plusieurs modèles
                        player.hideView();
                        callback.run();
                    }
                }
            }, player);
        }
    }

    private static Random random = new Random();
    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        player.stopChoosing();
        player.hideView();
        LGPlayer choosen = null;
        while(choosen == null || choosen == player)
            choosen = getGame().getAlive().get(random.nextInt(getGame().getAlive().size()));
        player.sendMessage("§6Si la voyante te regarde, elle verra que tu as celui de "+choosen.getName()+".");
        player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton exemple");
        player.getCache().set("loup_ftr_e", choosen);
        getPlayers().remove(player);
    }

    @Override
    public void join(LGPlayer player, boolean sendMessage) {
        super.join(player, sendMessage);
        player.setRole(this);
        LGCustomItems.updateItem(player);
        fr.leomelki.loupgarou.roles.RLoupGarou lgRole = null;
        for(fr.leomelki.loupgarou.roles.Role role : getGame().getRoles())
            if(role instanceof fr.leomelki.loupgarou.roles.RLoupGarou)
                lgRole = (fr.leomelki.loupgarou.roles.RLoupGarou)role;

        if(lgRole == null) {
            getGame().getRoles().add(lgRole = new fr.leomelki.loupgarou.roles.RLoupGarou(getGame()));

            getGame().getRoles().sort(new Comparator<fr.leomelki.loupgarou.roles.Role>() {
                @Override
                public int compare(fr.leomelki.loupgarou.roles.Role role1, fr.leomelki.loupgarou.roles.Role role2) {
                    return role1.getTurnOrder()-role2.getTurnOrder();
                }
            });
        }

        lgRole.join(player, false);
    }
}
