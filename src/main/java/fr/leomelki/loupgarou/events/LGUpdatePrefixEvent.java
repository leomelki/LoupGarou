package fr.leomelki.loupgarou.events;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.Setter;

public class LGUpdatePrefixEvent extends LGEvent {
	@Getter @Setter private String prefix;
	@Getter private final LGPlayer player, to;
	public LGUpdatePrefixEvent(LGGame game, LGPlayer player, LGPlayer to, String prefix) {
		super(game);
		this.player = player;
		this.prefix = prefix;
		this.to = to;
	}

}
