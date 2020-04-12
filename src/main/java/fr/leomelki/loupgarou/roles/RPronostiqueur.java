package fr.leomelki.loupgarou.roles;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;

public class RPronostiqueur extends Role{
	public RPronostiqueur(LGGame game) {
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
		return "§a§lPronostiqueur";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux espionner un joueur et découvrir s'il est gentil ou non. Cependant, dans certaines parties, vos pronostiques ne sont pas exacts...";
	}
	@Override
	public String getTask() {
		return "Choisis un joueur sur lequel pronostiquer.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 s'apprête à pronostiquer...";
	}
	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null && choosen != player) {
					//player.sendTitle("§6Vous avez regardé un rôle", "§e§l"+choosen.getName()+"§6§l est §e§l"+choosen.getRole().getName(), 5*20);
					String gentilMechant = choosen.getRoleWinType() == RoleWinType.VILLAGE || choosen.getRoleWinType() == RoleWinType.NONE ? "§a§lgentil" : "§c§lméchant";
					player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 est "+gentilMechant);
					player.sendMessage("§6Votre instinct vous dit que §7§l"+choosen.getName()+"§6 est "+gentilMechant+"§6.");
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
	}
}
