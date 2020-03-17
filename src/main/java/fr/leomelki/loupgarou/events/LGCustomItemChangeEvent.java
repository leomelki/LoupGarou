package fr.leomelki.loupgarou.events;

import java.util.List;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;

public class LGCustomItemChangeEvent extends LGEvent {
	@Getter private final LGPlayer player;
	@Getter private final List<String> constraints;
	
	public LGCustomItemChangeEvent(LGGame game, LGPlayer player, List<String> constraints) {
		super(game);
		this.player = player;
		this.constraints = constraints;
	}
}
