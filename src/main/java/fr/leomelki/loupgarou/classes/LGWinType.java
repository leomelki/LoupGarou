package fr.leomelki.loupgarou.classes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LGWinType {
	VILLAGEOIS("§6§l§oLa partie a été gagnée par le §2§lVillage§6§l§o !"),
	LOUPGAROU("§6§l§oLa partie a été gagnée par les §c§lLoups-Garous§6§l§o !"),
	LOUPGAROUBLANC("§6§l§oLa partie a été gagnée par le §c§lLoup-Garou Blanc§6§l§o !"),
	COUPLE("§6§l§oLa partie a été gagnée par le §d§lcouple§6§l§o !"),
	ANGE("§6§l§oLa partie a été gagnée par l'§d§lAnge§6§l§o !"),
	EQUAL("§7§l§oÉgalité§6§l§o, personne n'a gagné la partie !"),
	SOLO("§6§l§oUn joueur solitaire a gagné la partie!"),//bug si ça s'affiche
	ASSASSIN("§6§l§oLa partie a été gagnée par l'§1§lAssassin§6§l§o !"),
	PYROMANE("§6§l§oLa partie a été gagnée par le §6§lPyromane§6§l§o !"),
	NONE("§4Erreur: §cpersonne n'a gagné la partie.");
	
	@Getter private final String message;
}
