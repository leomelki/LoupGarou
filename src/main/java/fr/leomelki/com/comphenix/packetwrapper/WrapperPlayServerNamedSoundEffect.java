/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.leomelki.com.comphenix.packetwrapper;

import org.bukkit.Sound;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;

public class WrapperPlayServerNamedSoundEffect extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.NAMED_SOUND_EFFECT;

	public WrapperPlayServerNamedSoundEffect() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerNamedSoundEffect(PacketContainer packet) {
		super(packet, TYPE);
	}

	public Sound getSoundEffect() {
		return handle.getSoundEffects().read(0);
	}

	public void setSoundEffect(Sound value) {
		handle.getSoundEffects().write(0, value);
	}

	public SoundCategory getSoundCategory() {
		return handle.getSoundCategories().read(0);
	}

	public void setSoundCategory(SoundCategory value) {
		handle.getSoundCategories().write(0, value);
	}

	/**
	 * Retrieve Effect position X.
	 * <p>
	 * Notes: effect X multiplied by 8
	 * 
	 * @return The current Effect position X
	 */
	public int getEffectPositionX() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Effect position X.
	 * 
	 * @param value - new value.
	 */
	public void setEffectPositionX(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Effect position Y.
	 * <p>
	 * Notes: effect Y multiplied by 8
	 * 
	 * @return The current Effect position Y
	 */
	public int getEffectPositionY() {
		return handle.getIntegers().read(1);
	}

	/**
	 * Set Effect position Y.
	 * 
	 * @param value - new value.
	 */
	public void setEffectPositionY(int value) {
		handle.getIntegers().write(1, value);
	}

	/**
	 * Retrieve Effect position Z.
	 * <p>
	 * Notes: effect Z multiplied by 8
	 * 
	 * @return The current Effect position Z
	 */
	public int getEffectPositionZ() {
		return handle.getIntegers().read(2);
	}

	/**
	 * Set Effect position Z.
	 * 
	 * @param value - new value.
	 */
	public void setEffectPositionZ(int value) {
		handle.getIntegers().write(2, value);
	}

	/**
	 * Retrieve Volume.
	 * <p>
	 * Notes: 1 is 100%, can be more
	 * 
	 * @return The current Volume
	 */
	public float getVolume() {
		return handle.getFloat().read(0);
	}

	/**
	 * Set Volume.
	 * 
	 * @param value - new value.
	 */
	public void setVolume(float value) {
		handle.getFloat().write(0, value);
	}

	/**
	 * Retrieve Pitch.
	 * <p>
	 * Notes: 63 is 100%, can be more
	 * 
	 * @return The current Pitch
	 */
	public float getPitch() {
		return handle.getFloat().read(1);
	}

	/**
	 * Set Pitch.
	 * 
	 * @param value - new value.
	 */
	public void setPitch(float value) {
		handle.getFloat().write(1, value);
	}

}
