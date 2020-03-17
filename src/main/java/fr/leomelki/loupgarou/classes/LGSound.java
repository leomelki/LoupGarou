package fr.leomelki.loupgarou.classes;

import org.bukkit.Sound;

import lombok.Getter;

public enum LGSound {
	KILL(Sound.ENTITY_BLAZE_DEATH),
	START_NIGHT(Sound.ENTITY_SKELETON_DEATH),
	START_DAY(Sound.ENTITY_ZOMBIE_DEATH),
	AMBIANT_NIGHT(Sound.MUSIC_DISC_MALL),
	AMBIANT_DAY(Sound.MUSIC_DISC_MELLOHI);
	
	@Getter Sound sound;
	LGSound(Sound sound){
		this.sound = sound;
	}
}
