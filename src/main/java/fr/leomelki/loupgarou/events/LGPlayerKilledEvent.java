package fr.leomelki.loupgarou.events;

import org.bukkit.event.Cancellable;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class LGPlayerKilledEvent extends LGEvent implements Cancellable{
	public LGPlayerKilledEvent(LGGame game, LGPlayer killed, Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}

	@Getter @Setter boolean cancelled;
    
    @Getter @Setter private LGPlayer killed;
    @Getter @Setter private Reason reason;
	
    @RequiredArgsConstructor
	public static enum Reason{
		LOUP_GAROU("§7§l%s§4 est mort pendant la nuit"),
		GM_LOUP_GAROU("§7§l%s§4 est mort pendant la nuit"),
		LOUP_BLANC(LOUP_GAROU.getMessage()),
		SORCIERE(LOUP_GAROU.getMessage()),
		VOTE("§7§l%s§4 a été victime du vote"),
		CHASSEUR("§7§l%s§4 est mort sur le coup"),
		DICTATOR("§7§l%s§4 a été désigné"),
		DICTATOR_SUICIDE("§7§l%s§4 s'est suicidé par culpabilité"),
		DISCONNECTED("§7§l%s§4 est mort d'une déconnexion"),
		LOVE("§7§l%s§4 s'est suicidé par amour"),
		BOUFFON("§7§l%s§4 est mort de peur"),
		ASSASSIN("§7§l%s§4 s'est fait poignarder"),
		PYROMANE("§7§l%s§4 est parti en fumée"),
		PIRATE("§7§l%s§4 était l'otage"),
		FAUCHEUR("§7§l%s§4 a égaré son âme"),
		
		DONT_DIE("§7§l%s§4 est mort pour rien");
		
		@Getter private final String message;
	}
	
}
