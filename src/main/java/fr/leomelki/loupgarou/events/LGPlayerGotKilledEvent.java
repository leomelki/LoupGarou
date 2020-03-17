package fr.leomelki.loupgarou.events;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;
import lombok.Getter;

public class LGPlayerGotKilledEvent extends LGEvent {
	public LGPlayerGotKilledEvent(LGGame game, LGPlayer killed, Reason reason, boolean endGame) {
		super(game);
		this.killed = killed;
		this.reason = reason;
		this.endGame = endGame;
	}
	
	@Getter private final boolean endGame;
    @Getter private final LGPlayer killed;
    @Getter private Reason reason;
	
}
