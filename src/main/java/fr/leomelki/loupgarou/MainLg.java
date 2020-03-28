package fr.leomelki.loupgarou;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerNamedSoundEffect;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerUpdateHealth;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerUpdateTime;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.events.LGSkinLoadEvent;
import fr.leomelki.loupgarou.events.LGUpdatePrefixEvent;
import fr.leomelki.loupgarou.listeners.CancelListener;
import fr.leomelki.loupgarou.listeners.ChatListener;
import fr.leomelki.loupgarou.listeners.JoinListener;
import fr.leomelki.loupgarou.listeners.LoupGarouListener;
import fr.leomelki.loupgarou.listeners.VoteListener;
import fr.leomelki.loupgarou.roles.RAnge;
import fr.leomelki.loupgarou.roles.RAssassin;
import fr.leomelki.loupgarou.roles.RBouffon;
import fr.leomelki.loupgarou.roles.RChaperonRouge;
import fr.leomelki.loupgarou.roles.RChasseur;
import fr.leomelki.loupgarou.roles.RChienLoup;
import fr.leomelki.loupgarou.roles.RCorbeau;
import fr.leomelki.loupgarou.roles.RCupidon;
import fr.leomelki.loupgarou.roles.RDetective;
import fr.leomelki.loupgarou.roles.RDictateur;
import fr.leomelki.loupgarou.roles.REnfantSauvage;
import fr.leomelki.loupgarou.roles.RFaucheur;
import fr.leomelki.loupgarou.roles.RGarde;
import fr.leomelki.loupgarou.roles.RGrandMechantLoup;
import fr.leomelki.loupgarou.roles.RLoupGarou;
import fr.leomelki.loupgarou.roles.RLoupGarouBlanc;
import fr.leomelki.loupgarou.roles.RLoupGarouNoir;
import fr.leomelki.loupgarou.roles.RMedium;
import fr.leomelki.loupgarou.roles.RPetiteFille;
import fr.leomelki.loupgarou.roles.RPirate;
import fr.leomelki.loupgarou.roles.RPretre;
import fr.leomelki.loupgarou.roles.RPyromane;
import fr.leomelki.loupgarou.roles.RSorciere;
import fr.leomelki.loupgarou.roles.RSurvivant;
import fr.leomelki.loupgarou.roles.RVillageois;
import fr.leomelki.loupgarou.roles.RVoyante;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.Setter;

public class MainLg extends JavaPlugin{
	private static MainLg instance;
	@Getter private HashMap<String, Constructor<? extends Role>> roles = new HashMap<String, Constructor<? extends Role>>();
	@Getter private static String prefix = ""/*"§7[§9Loup-Garou§7] "*/;
	
	@Getter @Setter private LGGame currentGame;//Because for now, only one game will be playable on one server (flemme)

	private UtilsConfig EloConfig = new UtilsConfig(this, new File(this.getDataFolder(), "/elo.yml"), "elo.yml");
	private UtilsConfig EloPointsConfig = new UtilsConfig(this, new File(this.getDataFolder(), "/eloconfig.yml"), "eloconfig.yml");
	
	@Override
	public void onEnable() {
		instance = this;
		loadRoles();
		if(!new File(getDataFolder(), "config.yml").exists()) {//Créer la config
			FileConfiguration config = getConfig();
			config.set("spawns", new ArrayList<List<Double>>());
			for(String role : roles.keySet())//Nombre de participant pour chaque rôle
				config.set("role."+role, 1);
			saveConfig();
		}

		try {
			EloConfig.init();
			EloConfig.save();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (!new File(getDataFolder(), "eloconfig.yml").exists()) {//Créer la config
				EloPointsConfig.init();
				EloPointsConfig.getConfig().set("ranked", false);
				EloPointsConfig.getConfig().set("elo.bronze.3", "§7[§8Bronze III§7]");
				EloPointsConfig.getConfig().set("elo.bronze.2", "§7[§8Bronze II§7]");
				EloPointsConfig.getConfig().set("elo.bronze.1", "§7[§8Bronze I§7]");
				EloPointsConfig.getConfig().set("elo.silver.3", "§7[§7Argent III§7]");
				EloPointsConfig.getConfig().set("elo.silver.2", "§7[§7Argent II§7]");
				EloPointsConfig.getConfig().set("elo.silver.1", "§7[§7Argent I§7]");
				EloPointsConfig.getConfig().set("elo.gold.3", "§7[§6Or III§7]");
				EloPointsConfig.getConfig().set("elo.gold.2", "§7[§6Or II§7]");
				EloPointsConfig.getConfig().set("elo.gold.1", "§7[§6Or I§7]");
				EloPointsConfig.getConfig().set("elo.plat.3", "§7[§bPlatine III§7]");
				EloPointsConfig.getConfig().set("elo.plat.2", "§7[§bPlatine II§7]");
				EloPointsConfig.getConfig().set("elo.plat.1", "§7[§bPlatine I§7]");
				EloPointsConfig.getConfig().set("elo.diamond.3", "§7[§dDiamand III§7]");
				EloPointsConfig.getConfig().set("elo.diamond.2", "§7[§dDiamand II§7]");
				EloPointsConfig.getConfig().set("elo.diamond.1", "§7[§dDiamand I§7]");
				EloPointsConfig.getConfig().set("elo.master.3", "§7[§cMaître II§7]");
				EloPointsConfig.getConfig().set("elo.master.2", "§7[§cMaître I§7]");
				EloPointsConfig.getConfig().set("elo.master.1", "§7[§cMaître§7]");

				EloPointsConfig.getConfig().set("points.win.egalite", 0);
				EloPointsConfig.getConfig().set("points.win.villageois", 5);
				EloPointsConfig.getConfig().set("points.win.bouffon", 5);
				EloPointsConfig.getConfig().set("points.win.survivant", 7);
				EloPointsConfig.getConfig().set("points.win.loupgarou", 8);
				EloPointsConfig.getConfig().set("points.win.loupgaroublanc", 10);
				EloPointsConfig.getConfig().set("points.win.couple", 15);
				EloPointsConfig.getConfig().set("points.win.ange", 20);
				EloPointsConfig.getConfig().set("points.win.assassin", 23);
				EloPointsConfig.getConfig().set("points.win.pyromane", 23);

				EloPointsConfig.getConfig().set("points.loose.egalite", 0);
				EloPointsConfig.getConfig().set("points.loose.bouffon", -5);
				EloPointsConfig.getConfig().set("points.loose.survivant", -5);
				EloPointsConfig.getConfig().set("points.loose.villageois", -4);
				EloPointsConfig.getConfig().set("points.loose.loupgarou", -5);
				EloPointsConfig.getConfig().set("points.loose.loupgaroublanc", -7);
				EloPointsConfig.getConfig().set("points.loose.couple", -6);
				EloPointsConfig.getConfig().set("points.loose.assassin", -8);
				EloPointsConfig.getConfig().set("points.loose.pyromane", -8);

				EloPointsConfig.getConfig().set("points.ranked.bronze.3", 0);
				EloPointsConfig.getConfig().set("points.ranked.bronze.2", 50);
				EloPointsConfig.getConfig().set("points.ranked.bronze.1", 100);
				EloPointsConfig.getConfig().set("points.ranked.silver.3", 200);
				EloPointsConfig.getConfig().set("points.ranked.silver.2", 300);
				EloPointsConfig.getConfig().set("points.ranked.silver.1", 400);
				EloPointsConfig.getConfig().set("points.ranked.gold.3", 525);
				EloPointsConfig.getConfig().set("points.ranked.gold.2", 650);
				EloPointsConfig.getConfig().set("points.ranked.gold.1", 775);
				EloPointsConfig.getConfig().set("points.ranked.plat.3", 925);
				EloPointsConfig.getConfig().set("points.ranked.plat.2", 1075);
				EloPointsConfig.getConfig().set("points.ranked.plat.1", 1225);
				EloPointsConfig.getConfig().set("points.ranked.diamond.3", 1425);
				EloPointsConfig.getConfig().set("points.ranked.diamond.2", 1625);
				EloPointsConfig.getConfig().set("points.ranked.diamond.1", 1825);
				EloPointsConfig.getConfig().set("points.ranked.master.3", 2075);
				EloPointsConfig.getConfig().set("points.ranked.master.2", 2225);
				EloPointsConfig.getConfig().set("points.ranked.master.1", 2500);

			} else {
				EloPointsConfig.init();
			}
			EloPointsConfig.save();
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadConfig();
		Bukkit.getConsoleSender().sendMessage("/");
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new CancelListener(), this);
		Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new LoupGarouListener(), this);
		
		for(Player player : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "is connected"));
		
	    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_TIME) {
				@Override
				public void onPacketSending(PacketEvent event) {
					WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime(event.getPacket());
					LGPlayer lgp = LGPlayer.thePlayer(event.getPlayer());
					if(lgp.getGame() != null && lgp.getGame().getTime() != time.getTimeOfDay())
						event.setCancelled(true);
				}
			}
		);
		//Éviter que les gens s'entendent quand ils se sélectionnent et qu'ils sont trop proche
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
					WrapperPlayServerNamedSoundEffect sound = new WrapperPlayServerNamedSoundEffect(event.getPacket());
					if(sound.getSoundEffect() == Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
						event.setCancelled(true);
			}
		}
	);
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event.getPacket());
				ArrayList<PlayerInfoData> datas = new ArrayList<PlayerInfoData>();
				for(PlayerInfoData data : info.getData()) {
					LGPlayer lgp = LGPlayer.thePlayer(Bukkit.getPlayer(data.getProfile().getUUID()));
					if(player.getGame() != null && player.getGame() == lgp.getGame()) {
						LGUpdatePrefixEvent evt2 = new LGUpdatePrefixEvent(player.getGame(), lgp, player, "");
						WrappedChatComponent displayName = data.getDisplayName();
						Bukkit.getPluginManager().callEvent(evt2);
						if(evt2.getPrefix().length() > 0) {
								try {
								if(displayName != null) {
									JSONObject obj = (JSONObject) new JSONParser().parse(displayName.getJson());
									displayName = WrappedChatComponent.fromText(evt2.getPrefix()+obj.get("text"));
								} else
									displayName = WrappedChatComponent.fromText(evt2.getPrefix()+data.getProfile().getName());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						LGSkinLoadEvent evt = new LGSkinLoadEvent(lgp.getGame(), lgp, player, data.getProfile());
						Bukkit.getPluginManager().callEvent(evt);
						datas.add(new PlayerInfoData(evt.getProfile(), data.getLatency(), data.getGameMode(), displayName));
					}else
						datas.add(data);
				}
				info.setData(datas);
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_HEALTH) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				if(player.getGame() != null && player.getGame().isStarted()) {
					WrapperPlayServerUpdateHealth health = new WrapperPlayServerUpdateHealth(event.getPacket());
					health.setFood(6);
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam(event.getPacket());
				team.setColor(ChatColor.WHITE);
				Player other = Bukkit.getPlayer(team.getName());
				if(other == null)return;
				LGPlayer lgp = LGPlayer.thePlayer(other);
				if(player.getGame() != null && player.getGame() == lgp.getGame()) {
					LGUpdatePrefixEvent evt2 = new LGUpdatePrefixEvent(player.getGame(), lgp, player, "");
					Bukkit.getPluginManager().callEvent(evt2);
					if(evt2.getPrefix().length() > 0)
						team.setPrefix(WrappedChatComponent.fromText(evt2.getPrefix()));
					else
						team.setPrefix(WrappedChatComponent.fromText("§f"));
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				if(player.getGame() != null) {
					WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment(event.getPacket());
					if(equip.getSlot() == ItemSlot.OFFHAND && equip.getEntityID() != player.getPlayer().getEntityId())
						equip.setItem(new ItemStack(Material.AIR));
				}
			}
		});
	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("lg")) {
			if(!sender.hasPermission("loupgarou.admin")) {
				sender.sendMessage(prefix+"§4Erreur: Vous n'avez pas la permission...");
				return true;
			}
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("addspawn")) {
					Player player = (Player)sender;
					Location loc = player.getLocation();
					List<Object> list = (List<Object>) getConfig().getList("spawns");
					list.add(Arrays.asList((double)loc.getBlockX(), loc.getY(), (double)loc.getBlockZ(), (double)loc.getYaw(), (double)loc.getPitch()));
					saveConfig();
					loadConfig();
					sender.sendMessage(prefix+"§aLa position a bien été ajoutée !");
					return true;
				}else if(args[0].equalsIgnoreCase("end")) {
					if(args.length != 2) {
						sender.sendMessage("§4Utilisation : §c/lg end <pseudo>");
						return true;
					}
					Player selected = Bukkit.getPlayer(args[1]);
					if(selected == null) {
						sender.sendMessage("§4Erreur : §cLe joueur §4"+args[1]+"§c n'est pas connecté.");
						return true;
					}
					LGGame game = LGPlayer.thePlayer(selected).getGame();
					if(game == null) {
						sender.sendMessage("§4Erreur : §cLe joueur §4"+selected.getName()+"§c n'est pas dans une partie.");
						return true;
					}
					game.cancelWait();
					game.endGame(LGWinType.EQUAL);
					game.broadcastMessage("§cLa partie a été arrêtée de force !");
					return true;
				}else if(args[0].equalsIgnoreCase("start")) {
					if(args.length < 2) {
						sender.sendMessage("§4Utilisation : §c/lg start <pseudo>");
						return true;
					}
					Player player = Bukkit.getPlayer(args[1]);
					if(player == null) {
						sender.sendMessage("§4Erreur : §cLe joueur §4"+args[1]+"§c n'existe pas !");
						return true;
					}
					LGPlayer lgp = LGPlayer.thePlayer(player);
					if(lgp.getGame() == null) {
						sender.sendMessage("§4Erreur : §cLe joueur §4"+lgp.getName()+"§c n'est pas dans une partie.");
						return true;
					}
					if(MainLg.getInstance().getConfig().getList("spawns").size() < lgp.getGame().getMaxPlayers()) {
						sender.sendMessage("§4Erreur : §cIl n'y a pas assez de points de spawn !");
						sender.sendMessage("§8§oPour les définir, merci de faire §7/lg addSpawn");
						return true;
					}
					sender.sendMessage("§aVous avez bien démarré une nouvelle partie !");
					lgp.getGame().updateStart();
					return true;
				}else if(args[0].equalsIgnoreCase("reloadconfig")) {
					sender.sendMessage("§aVous avez bien reload les config !");
					sender.sendMessage("§7§oSi vous avez changé les rôles, écriver §8§o/lg joinall§7§o !");
					loadConfig();
					EloConfig.reload();
					EloPointsConfig.reload();
					return true;
				}else if(args[0].equalsIgnoreCase("joinall")) {
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "joinall"));
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "joinall"));
					return true;
				}else if(args[0].equalsIgnoreCase("reloadPacks")) {
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "reloadPacks"));
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "reloadPacks"));
					return true;
				}else if(args[0].equalsIgnoreCase("nextNight")) {
					sender.sendMessage("§aVous êtes passé à la prochaine nuit");
					if(getCurrentGame() != null) {
						getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine nuit a été forcé !");
						for(LGPlayer lgp : getCurrentGame().getInGame())
							lgp.stopChoosing();
						getCurrentGame().cancelWait();
						getCurrentGame().nextNight();
					}
					return true;
				}else if(args[0].equalsIgnoreCase("nextDay")) {
					sender.sendMessage("§aVous êtes passé à la prochaine journée");
					if(getCurrentGame() != null) {
						getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine journée a été forcé !");
						getCurrentGame().cancelWait();
						for(LGPlayer lgp : getCurrentGame().getInGame())
							lgp.stopChoosing();
						getCurrentGame().endNight();
					}
					return true;
				}else if(args[0].equalsIgnoreCase("roles")) {
					if(args.length == 1 || args[1].equalsIgnoreCase("list")) {
						sender.sendMessage(prefix+"§6Voici la liste des rôles:");
						int index = 0;
						for(String role : getRoles().keySet())
							sender.sendMessage(prefix+"  §e- "+index+++" - §6"+role+" §e> "+MainLg.getInstance().getConfig().getInt("role."+role));
						sender.sendMessage("\n"+prefix+" §7Écrivez §8§o/lg roles set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
					} else {
						if(args[1].equalsIgnoreCase("set") && args.length == 4) {
							String role = null;
							if(args[2].length() <= 2)
								try {
									Integer i = Integer.valueOf(args[2]);
									Object[] array = getRoles().keySet().toArray();
									if(array.length <= i) {
										sender.sendMessage(prefix+"§4Erreur: §cCe rôle n'existe pas.");
										return true;
									}else
										role = (String)array[i];
								}catch(Exception err) {sender.sendMessage(prefix+"§4Erreur: §cCeci n'est pas un nombre");}
							else
								role = args[2];
							
							if(role != null) {
								String real_role = null;
								for(String real : getRoles().keySet())
									if(real.equalsIgnoreCase(role)) {
										real_role = real;
										break;
									}
								
								if(real_role != null) {
									try {
										MainLg.getInstance().getConfig().set("role."+real_role, Integer.valueOf(args[3]));
										sender.sendMessage(prefix+"§6Il y aura §e"+args[3]+" §6"+real_role);
										saveConfig();
										loadConfig();
										sender.sendMessage("§7§oSi vous avez fini de changer les rôles, écriver §8§o/lg joinall§7§o !");
									}catch(Exception err) {
										sender.sendMessage(prefix+"§4Erreur: §c"+args[3]+" n'est pas un nombre");
									}
									return true;
								}
							}
							sender.sendMessage(prefix+"§4Erreur: §cLe rôle que vous avez entré est incorrect");
							
						} else {
							sender.sendMessage(prefix+"§4Erreur: §cCommande incorrecte.");
							sender.sendMessage(prefix+"§4Essayez §c/lg roles set <role_id/role_name> <nombre>§4 ou §c/lg roles list");
						}
					}
					return true;
				}else if (args[0].equalsIgnoreCase("elo")) {
					if (args[1].equalsIgnoreCase("set") && args.length == 4) {
						UUID pUUID = null;
						int number = -1;
						try {
							Player pElo = Bukkit.getPlayer(args[2]);
							number = Integer.parseInt(args[3]);

							if (pElo == null) {
								sender.sendMessage(prefix + "§4Cette personne n'existe pas.");
								return true;
							} else
								pUUID = pElo.getUniqueId();
						} catch (Exception err) {
							sender.sendMessage(prefix + "§4Erreur: §cCeci n'est pas un nombre");
						}

						if (pUUID != null && MainLg.getInstance().getEloConfig().getConfig().getInt(pUUID + ".points") >= 0 && number >= 0) {

							try {
								MainLg.getInstance().getEloConfig().getConfig().set(pUUID + ".points", number);
								sender.sendMessage("§eVous avez mis §l" + Bukkit.getPlayer(pUUID).getDisplayName() + "§l à " + number + " points");
							} catch (Exception err) {
								sender.sendMessage(prefix + "§4Erreur: §c" + args[3] + " n'est pas un nombre");
							}
							return true;
						}

					} else if (args[1].equalsIgnoreCase("get") && args.length == 3) {
						UUID pUUID = null;
						try {
							Player pElo = Bukkit.getPlayer(args[2]);
							if (pElo == null) {
								sender.sendMessage(prefix + "§4Cette personne n'existe pas.");
								return true;
							} else
								pUUID = pElo.getUniqueId();
						} catch (Exception err) {
							sender.sendMessage(prefix + "§4Erreur: §cCeci n'est pas un nombre");
						}
						if (pUUID != null && MainLg.getInstance().getEloConfig().getConfig().getInt(pUUID + ".points") >= 0) {
							sender.sendMessage("§e§l" + Bukkit.getPlayer(pUUID).getDisplayName() + "§r§e à " + MainLg.getInstance().getEloConfig().getConfig().getInt(pUUID + ".points") + " points");
							return true;
						}
					} else {
						sender.sendMessage(prefix + "§4Erreur: §cCommande incorrecte.");
						sender.sendMessage(prefix + "§4Essayez §c/lg elo set <pseudo> <nombre>");
						sender.sendMessage(prefix + "§4Essayez §c/lg elo get <pseudo>");
					}
					return true;
				}else if(args[0].equalsIgnoreCase("ranked")){
					if (args[1].equalsIgnoreCase("get") && args.length == 2) {
						sender.sendMessage("§4Le mode classé est "+(MainLg.getInstance().getEloPointsConfig().getConfig().getBoolean("ranked")?"§a§lactivé":"§c§ldésactivé"));
					} else if(args[1].equalsIgnoreCase("set") && args.length == 3){
						if(Boolean.parseBoolean(args[2]) || !Boolean.parseBoolean(args[2])){
							sender.sendMessage("§4Le mode classé a été "+(Boolean.parseBoolean(args[2])?"§c§ldésactivé":"§a§lactivé"));
							MainLg.getInstance().getEloPointsConfig().getConfig().set("ranked", Boolean.parseBoolean(args[2]));
						} else {
							sender.sendMessage(prefix + "§4Erreur: §cCeci n'est pas true ou false");
						}
					} else {
						sender.sendMessage(prefix + "§4Erreur: §cCommande incorrecte.");
						sender.sendMessage(prefix + "§4Essayez §c/lg ranked get");
						sender.sendMessage(prefix + "§4Essayez §c/lg elo set <true/false>");
					}
					return true;
				}
			}
			sender.sendMessage(prefix+"§4Erreur: §cCommande incorrecte.");
			sender.sendMessage(prefix+"§4Essayez /lg §caddSpawn/end/start/nextNight/nextDay/reloadConfig/roles/reloadPacks/joinAll");
			return true;
		}
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!sender.hasPermission("loupgarou.admin"))
			return new ArrayList<String>(0);
		
		if(args.length > 1) {
			if(args[0].equalsIgnoreCase("roles"))
				if(args.length == 2)
					return getStartingList(args[1], "list", "set");
				else if(args.length == 3 && args[1].equalsIgnoreCase("set"))
					return getStartingList(args[2], getRoles().keySet().toArray(new String[getRoles().size()]));
				else if(args.length == 4)
					return Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
		}else if(args.length == 1)
			return getStartingList(args[0], "addSpawn", "end", "start", "nextNight", "nextDay", "reloadConfig", "roles", "joinAll", "reloadPacks");
		return new ArrayList<String>(0);
	}
	private List<String> getStartingList(String startsWith, String... list){
		startsWith = startsWith.toLowerCase();
		ArrayList<String> returnlist = new ArrayList<String>();
		if(startsWith.length() == 0)
			return Arrays.asList(list);
		for(String s : list)
			if(s.toLowerCase().startsWith(startsWith))
				returnlist.add(s);
		return returnlist;
	}
	public void loadConfig() {
		int players = 0;
		for(String role : roles.keySet())
			players += getConfig().getInt("role."+role);
		currentGame = new LGGame(players);
	}
	@Override
	public void onDisable() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}
	public static MainLg getInstance() {
		return instance;
	}
	private void loadRoles() {
		try {
			roles.put("LoupGarou", RLoupGarou.class.getConstructor(LGGame.class));
			roles.put("LoupGarouNoir", RLoupGarouNoir.class.getConstructor(LGGame.class));
			roles.put("Garde", RGarde.class.getConstructor(LGGame.class));
			roles.put("Sorciere", RSorciere.class.getConstructor(LGGame.class));
			roles.put("Voyante", RVoyante.class.getConstructor(LGGame.class));
			roles.put("Chasseur", RChasseur.class.getConstructor(LGGame.class));
			roles.put("Villageois", RVillageois.class.getConstructor(LGGame.class));
			roles.put("Medium", RMedium.class.getConstructor(LGGame.class));
			roles.put("Dictateur", RDictateur.class.getConstructor(LGGame.class));
			roles.put("Cupidon", RCupidon.class.getConstructor(LGGame.class));
			roles.put("PetiteFille", RPetiteFille.class.getConstructor(LGGame.class));
			roles.put("ChaperonRouge", RChaperonRouge.class.getConstructor(LGGame.class));
			roles.put("LoupGarouBlanc", RLoupGarouBlanc.class.getConstructor(LGGame.class));
			roles.put("Bouffon", RBouffon.class.getConstructor(LGGame.class));
			roles.put("Ange", RAnge.class.getConstructor(LGGame.class));
			roles.put("Survivant", RSurvivant.class.getConstructor(LGGame.class));
			roles.put("Assassin", RAssassin.class.getConstructor(LGGame.class));
			roles.put("GrandMechantLoup", RGrandMechantLoup.class.getConstructor(LGGame.class));
			roles.put("Corbeau", RCorbeau.class.getConstructor(LGGame.class));
			roles.put("Detective", RDetective.class.getConstructor(LGGame.class));
			roles.put("ChienLoup", RChienLoup.class.getConstructor(LGGame.class));
			roles.put("Pirate", RPirate.class.getConstructor(LGGame.class));
			roles.put("Pyromane", RPyromane.class.getConstructor(LGGame.class));
			roles.put("Pretre", RPretre.class.getConstructor(LGGame.class));
			roles.put("Faucheur", RFaucheur.class.getConstructor(LGGame.class));
			roles.put("EnfantSauvage", REnfantSauvage.class.getConstructor(LGGame.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	public fr.leomelki.loupgarou.utils.UtilsConfig getEloConfig() {
		return this.EloConfig;
	}
	public fr.leomelki.loupgarou.utils.UtilsConfig getEloPointsConfig() {
		return this.EloPointsConfig;
	}
	public void setRank(LGPlayer lgp) {
		int points = MainLg.getInstance().getEloConfig().getConfig().getInt(lgp.getPlayer().getUniqueId() + ".points");
		YamlConfiguration rankConfig = MainLg.getInstance().getEloPointsConfig().getConfig();
		if (points == 0) {
			MainLg.getInstance().getEloConfig().getConfig().set(String.valueOf(lgp.getPlayer().getUniqueId() + ".points"), 0);
			lgp.setElo(rankConfig.getString("elo.bronze.3"));
			MainLg.getInstance().getEloConfig().save();
		} else if (points >= rankConfig.getInt("points.ranked.bronze.3") && points < rankConfig.getInt("points.ranked.bronze.2")) {
			lgp.setElo(rankConfig.getString("elo.bronze.3"));
		} else if (points >= rankConfig.getInt("points.ranked.bronze.2") && points < rankConfig.getInt("points.ranked.bronze.1")) {
			lgp.setElo(rankConfig.getString("elo.bronze.2"));
		} else if (points >= rankConfig.getInt("points.ranked.bronze.1") && points < rankConfig.getInt("points.ranked.silver.3")) {
			lgp.setElo(rankConfig.getString("elo.bronze.1"));
		} else if (points >= rankConfig.getInt("points.ranked.silver.3") && points < rankConfig.getInt("points.ranked.silver.2")) {
			lgp.setElo(rankConfig.getString("elo.silver.3"));
		} else if (points >= rankConfig.getInt("points.ranked.silver.2") && points < rankConfig.getInt("points.ranked.silver.1")) {
			lgp.setElo(rankConfig.getString("elo.silver.2"));
		} else if (points >= rankConfig.getInt("points.ranked.silver.1") && points < rankConfig.getInt("points.ranked.gold.3")) {
			lgp.setElo(rankConfig.getString("elo.silver.1"));
		} else if (points >= rankConfig.getInt("points.ranked.gold.3") && points < rankConfig.getInt("points.ranked.gold.2")) {
			lgp.setElo(rankConfig.getString("elo.gold.3"));
		} else if (points >= rankConfig.getInt("points.ranked.gold.2") && points < rankConfig.getInt("points.ranked.gold.1")) {
			lgp.setElo(rankConfig.getString("elo.gold.2"));
		} else if (points >= rankConfig.getInt("points.ranked.gold.1") && points < rankConfig.getInt("points.ranked.plat.3")) {
			lgp.setElo(rankConfig.getString("elo.gold.1"));
		} else if (points >= rankConfig.getInt("points.ranked.plat.3") && points < rankConfig.getInt("points.ranked.plat.2")) {
			lgp.setElo(rankConfig.getString("elo.plat.3"));
		} else if (points >= rankConfig.getInt("points.ranked.plat.2") && points < rankConfig.getInt("points.ranked.plat.1")) {
			lgp.setElo(rankConfig.getString("elo.plat.2"));
		} else if (points >= rankConfig.getInt("points.ranked.plat.1") && points < rankConfig.getInt("points.ranked.diamond.3")) {
			lgp.setElo(rankConfig.getString("elo.plat.1"));
		} else if (points >= rankConfig.getInt("points.ranked.diamond.3") && points < rankConfig.getInt("points.ranked.diamond.2")) {
			lgp.setElo(rankConfig.getString("elo.diamond.3"));
		} else if (points >= rankConfig.getInt("points.ranked.diamond.2") && points < rankConfig.getInt("points.ranked.diamond.1")) {
			lgp.setElo(rankConfig.getString("elo.diamond.2"));
		} else if (points >= rankConfig.getInt("points.ranked.diamond.1") && points < rankConfig.getInt("points.ranked.master.3")) {
			lgp.setElo(rankConfig.getString("elo.diamond.1"));
		} else if (points >= rankConfig.getInt("points.ranked.master.3") && points < rankConfig.getInt("points.ranked.master.2")) {
			lgp.setElo(rankConfig.getString("elo.master.3"));
		} else if (points >= rankConfig.getInt("points.ranked.master.2") && points < rankConfig.getInt("points.ranked.master.1")) {
			lgp.setElo(rankConfig.getString("elo.master.2"));
		} else if (points >= rankConfig.getInt("points.ranked.master.1")) {
			lgp.setElo(rankConfig.getString("elo.master.1"));
		}
	}
}
