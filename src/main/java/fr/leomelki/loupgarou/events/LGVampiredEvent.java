package fr.leomelki.loupgarou.events;

import org.bukkit.event.Cancellable;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.Setter;

public class LGVampiredEvent extends LGEvent{
	public LGVampiredEvent(LGGame game, LGPlayer player) {
		super(game);
		this.player = player;
	}
	
	@Getter @Setter private boolean immuned, protect;
	@Getter @Setter private LGPlayer player;
}