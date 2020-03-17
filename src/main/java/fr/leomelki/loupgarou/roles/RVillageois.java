package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;

public class RVillageois extends Role{
	public RVillageois(LGGame game) {
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
		return "§a§lVillageois";
	}
	@Override
	public String getFriendlyName() {
		return "des "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Tu ne disposes d'aucun pouvoir particulier, uniquement ta perspicacité et ta force de persuasion.";
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
}
