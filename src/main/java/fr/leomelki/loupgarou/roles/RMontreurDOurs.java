package fr.leomelki.loupgarou.roles;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGDayStartEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RMontreurDOurs extends Role{
	public RMontreurDOurs(LGGame game) {
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
		return "§a§lMontreur d'Ours";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque matin, ton Ours va renifler tes voisins et grognera si l'un d'eux est hostile aux Villageois.";
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
	
	private static Random random = new Random();
	private int lastNight = -1;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDay(LGDayStartEvent e) {
		if (e.getGame() == getGame() && getPlayers().size() > 0) {
			if(lastNight == getGame().getNight())
				return;
			lastNight = getGame().getNight();
			List<?> original = MainLg.getInstance().getConfig().getList("spawns");
			for(LGPlayer target : getPlayers()) {
				if(!target.isRoleActive())
					continue;
				int size = original.size();
				int killedPlace = target.getPlace();

				for (int i = killedPlace + 1;; i++) {
					if (i == size)
						i = 0;
					LGPlayer lgp = getGame().getPlacements().get(i);
					if (lgp != null && !lgp.isDead()) {
						if(lgp.getRoleWinType() == RoleWinType.VILLAGE || lgp.getRoleWinType() == RoleWinType.NONE)
							break;
						else{
							getGame().broadcastMessage("§6La bête du "+getName()+"§6 grogne...");
							return;
						}
					}
					if (lgp == target)// Fait un tour complet
						break;
				}
				for (int i = killedPlace - 1;; i--) {
					if (i == -1)
						i = size - 1;
					LGPlayer lgp = getGame().getPlacements().get(i);
					if (lgp != null && !lgp.isDead()) {
						if(lgp.getRoleWinType() == RoleWinType.VILLAGE || lgp.getRoleWinType() == RoleWinType.NONE)
							break;
						else{
							getGame().broadcastMessage("§6La bête du "+getName()+"§6 grogne...");
							return;
						}
					}
					if (lgp == target)// Fait un tour complet
						break;
				}
			}
		}
	}
}
