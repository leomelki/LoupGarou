package fr.leomelki.loupgarou.scoreboard;

import java.util.Arrays;
import java.util.List;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.utils.RandomString;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomScoreboard {
	@Getter private final String name = RandomString.generate(16);
	@Getter private final String displayName;
	private final List<CustomScoreboardEntry> entries = Arrays.asList(new CustomScoreboardEntry(15, this), new CustomScoreboardEntry(14, this), new CustomScoreboardEntry(13, this),
																			  new CustomScoreboardEntry(12, this), new CustomScoreboardEntry(11, this), new CustomScoreboardEntry(10, this),
																			  new CustomScoreboardEntry(9, this), new CustomScoreboardEntry(8, this), new CustomScoreboardEntry(7, this),
																			  new CustomScoreboardEntry(6, this), new CustomScoreboardEntry(5, this), new CustomScoreboardEntry(4, this),
																			  new CustomScoreboardEntry(3, this), new CustomScoreboardEntry(2, this), new CustomScoreboardEntry(1, this),
																			  new CustomScoreboardEntry(0, this));
	@Getter private final LGPlayer player;
	@Getter private boolean shown;
	
	public CustomScoreboardEntry getLine(int index) {
		return entries.get(index);
	}
	
	public void show() {
		WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective();
		objective.setMode(0);
		objective.setName(name);
		objective.setDisplayName(WrappedChatComponent.fromText(displayName));
		objective.sendPacket(player.getPlayer());
		WrapperPlayServerScoreboardDisplayObjective display = new WrapperPlayServerScoreboardDisplayObjective();
		display.setPosition(1);
		display.setScoreName(name);
		display.sendPacket(player.getPlayer());
		shown = true;
		
		for(CustomScoreboardEntry entry : entries)
			entry.show();
	}
	
	public void hide() {
		WrapperPlayServerScoreboardObjective remove = new WrapperPlayServerScoreboardObjective();
		remove.setMode(1);
		remove.setName(name);
		remove.sendPacket(player.getPlayer());
		
		for(CustomScoreboardEntry entry : entries)
			entry.hide();
		
		shown = false;
	}
}
