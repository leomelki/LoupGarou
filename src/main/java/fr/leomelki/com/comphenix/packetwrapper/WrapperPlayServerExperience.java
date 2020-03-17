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

public class WrapperPlayServerExperience extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.EXPERIENCE;

	public WrapperPlayServerExperience() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerExperience(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Experience bar.
	 * <p>
	 * Notes: between 0 and 1
	 * 
	 * @return The current Experience bar
	 */
	public float getExperienceBar() {
		return handle.getFloat().read(0);
	}

	/**
	 * Set Experience bar.
	 * 
	 * @param value - new value.
	 */
	public void setExperienceBar(float value) {
		handle.getFloat().write(0, value);
	}

	/**
	 * Retrieve Level.
	 * 
	 * @return The current Level
	 */
	public int getLevel() {
		return handle.getIntegers().read(1);
	}

	/**
	 * Set Level.
	 * 
	 * @param value - new value.
	 */
	public void setLevel(int value) {
		handle.getIntegers().write(1, value);
	}

	/**
	 * Retrieve Total Experience.
	 * 
	 * @return The current Total Experience
	 */
	public int getTotalExperience() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Total Experience.
	 * 
	 * @param value - new value.
	 */
	public void setTotalExperience(int value) {
		handle.getIntegers().write(0, value);
	}

}
