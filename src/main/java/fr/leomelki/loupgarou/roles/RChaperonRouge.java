package fr.leomelki.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGNightEndEvent;
import fr.leomelki.loupgarou.events.LGNightPlayerPreKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;
import fr.leomelki.loupgarou.events.LGRoleTurnEndEvent;

public class RChaperonRouge extends Role{
	public RChaperonRouge(LGGame game) {
		super(game);
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
	public String getName() {
		return "§a§lChaperon Rouge";
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
		return "Tu gagnes avec le §a§lVillage§f. Tant que le §a§lChasseur§f est en vie, tu ne peux pas te faire tuer par les §c§lLoups§f pendant la nuit.";
	}
	@Override
	public String getTask() {
		return "";
	}
	@Override
	public String getBroadcastedTask() {
		return "";
	}
	@Override
	public int getTimeout() {
		return -1;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onKill(LGNightPlayerPreKilledEvent e) {
		if(e.getKilled().getRole() == this && e.getReason() == Reason.LOUP_GAROU || e.getReason() == Reason.GM_LOUP_GAROU) {
			for(Role role : getGame().getRoles())
				if(role instanceof RChasseur)
					if(role.getPlayers().size() > 0){
						e.getKilled().getCache().set("chaperon_kill", true);
						e.setReason(Reason.DONT_DIE);
						break;
					}
		}
	}
	@EventHandler
	public void onTour(LGRoleTurnEndEvent e) {
		if(e.getGame() == getGame()) {
			if(e.getPreviousRole() instanceof RLoupGarou) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean("chaperon_kill")) {
						for(LGPlayer l : getGame().getInGame())
							if(l.getRoleType() == RoleType.LOUP_GAROU)
								l.sendMessage("§cVotre cible est immunisée.");
					}
			}else if(e.getPreviousRole() instanceof RGrandMechantLoup) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean("chaperon_kill")) {
						for(LGPlayer l : e.getPreviousRole().getPlayers())
							l.sendMessage("§cVotre cible est immunisée.");
					}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDayStart(LGNightEndEvent e) {
		if(e.getGame() == getGame()) {
			for(LGPlayer lgp : getPlayers())
				if(lgp.getCache().getBoolean("chaperon_kill")) {
					lgp.getCache().remove("chaperon_kill");
					lgp.sendMessage("§9§oTu as été attaqué cette nuit.");
				}
		}
	}
}
