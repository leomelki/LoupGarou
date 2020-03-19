package fr.leomelki.loupgarou.roles;

import java.util.Comparator;

import org.bukkit.potion.PotionEffectType;

import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class REnfantSauvageLG extends Role{
	public REnfantSauvageLG(LGGame game) {
		super(game);
	}
	@Override
	public String getName() {
		for(LGPlayer lgp : getPlayers())
			if(lgp.getPlayer() != null && lgp.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
				return "§c§lEnfant-Sauvage";
		return (getPlayers().size() > 0 ? "§a" : "§c")+"§lEnfant-Sauvage";
	}

	@Override
	public String getFriendlyName() {
		return "de l'"+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Au début de la première nuit, tu dois choisir un joueur comme modèle. S'il meurt au cours de la partie, tu deviendras un §c§lLoup-Garou§f.";
	}

	@Override
	public String getTask() {
		return "Qui veux-tu prendre comme modèle ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "L'"+getName()+"§9 cherche ses marques...";
	}
	@Override
	public RoleType getType() {
		return RoleType.LOUP_GAROU;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.LOUP_GAROU;
	}

	@Override
	public int getTimeout() {
		return -1;
	}
	
	@Override
	public void join(LGPlayer player, boolean sendMessage) {
		super.join(player, sendMessage);
		player.setRole(this);
		LGCustomItems.updateItem(player);
		RLoupGarou lgRole = null;
		for(Role role : getGame().getRoles())
			if(role instanceof RLoupGarou)
				lgRole = (RLoupGarou)role;
		
		if(lgRole == null) {
			getGame().getRoles().add(lgRole = new RLoupGarou(getGame()));

			getGame().getRoles().sort(new Comparator<Role>() {
				@Override
				public int compare(Role role1, Role role2) {
					return role1.getTurnOrder()-role2.getTurnOrder();
				}
			});
		}
		
		lgRole.join(player, false);
		for(LGPlayer lgp : lgRole.getPlayers())
			if(lgp != player)
				lgp.sendMessage("§7§l"+player.getName()+"§6 a rejoint les §c§lLoups-Garous§6.");
	}
}
