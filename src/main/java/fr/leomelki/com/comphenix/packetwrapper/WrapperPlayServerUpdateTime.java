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

public class WrapperPlayServerUpdateTime extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.UPDATE_TIME;

	public WrapperPlayServerUpdateTime() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerUpdateTime(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Age of the world.
	 * <p>
	 * Notes: in ticks; not changed by server commands
	 * 
	 * @return The current Age of the world
	 */
	public long getAgeOfTheWorld() {
		return handle.getLongs().read(0);
	}

	/**
	 * Set Age of the world.
	 * 
	 * @param value - new value.
	 */
	public void setAgeOfTheWorld(long value) {
		handle.getLongs().write(0, value);
	}

	/**
	 * Retrieve Time of day.
	 * <p>
	 * Notes: the world (or region) time, in ticks. If negative the sun will
	 * stop moving at the Math.abs of the time
	 * 
	 * @return The current Time of day
	 */
	public long getTimeOfDay() {
		return handle.getLongs().read(1);
	}

	/**
	 * Set Time of day.
	 * 
	 * @param value - new value.
	 */
	public void setTimeOfDay(long value) {
		handle.getLongs().write(1, value);
	}

}
