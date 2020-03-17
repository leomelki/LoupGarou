package fr.leomelki.loupgarou.events;

import org.bukkit.event.Cancellable;

import fr.leomelki.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.Setter;

public class LGNightStart extends LGEvent implements Cancellable{

	public LGNightStart(LGGame game) {
		super(game);
	}

	@Getter @Setter boolean cancelled;

}
