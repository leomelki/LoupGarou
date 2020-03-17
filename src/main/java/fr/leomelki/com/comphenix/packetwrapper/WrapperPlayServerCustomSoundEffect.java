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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;
import com.comphenix.protocol.wrappers.MinecraftKey;

public class WrapperPlayServerCustomSoundEffect extends AbstractPacket {

	public static final PacketType TYPE =
			PacketType.Play.Server.CUSTOM_SOUND_EFFECT;

	public WrapperPlayServerCustomSoundEffect() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerCustomSoundEffect(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Sound Name.
	 * <p>
	 * Notes: all known sound effect names can be seen here.
	 * 
	 * @return The current Sound Name
	 */
	public MinecraftKey getSoundName() {
		return handle.getMinecraftKeys().read(0);
	}

	/**
	 * Set Sound Name.
	 * 
	 * @param value - new value.
	 */
	public void setSoundName(MinecraftKey value) {
		handle.getMinecraftKeys().write(0, value);
	}

	/**
	 * Retrieve Sound Category.
	 * <p>
	 * Notes: the category that this sound will be played from (current
	 * categories)
	 * 
	 * @return The current Sound Category
	 */
	public SoundCategory getSoundCategory() {
		return handle.getSoundCategories().read(0);
	}

	/**
	 * Set Sound Category.
	 * 
	 * @param value - new value.
	 */
	public void setSoundCategory(SoundCategory value) {
		handle.getSoundCategories().write(0, value);
	}

	/**
	 * Retrieve Effect Position X.
	 * <p>
	 * Notes: effect X multiplied by 8 (fixed-point number with only 3 bits
	 * dedicated to the fractional part)
	 * 
	 * @return The current Effect Position X
	 */
	public int getX() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Effect Position X.
	 * 
	 * @param value - new value.
	 */
	public void setX(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Effect Position Y.
	 * <p>
	 * Notes: effect Y multiplied by 8 (fixed-point number with only 3 bits
	 * dedicated to the fractional part)
	 * 
	 * @return The current Effect Position Y
	 */
	public int getY() {
		return handle.getIntegers().read(1);
	}

	/**
	 * Set Effect Position Y.
	 * 
	 * @param value - new value.
	 */
	public void setY(int value) {
		handle.getIntegers().write(1, value);
	}

	/**
	 * Retrieve Effect Position Z.
	 * <p>
	 * Notes: effect Z multiplied by 8 (fixed-point number with only 3 bits
	 * dedicated to the fractional part)
	 * 
	 * @return The current Effect Position Z
	 */
	public int getZ() {
		return handle.getIntegers().read(2);
	}

	/**
	 * Set Effect Position Z.
	 * 
	 * @param value - new value.
	 */
	public void setZ(int value) {
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
