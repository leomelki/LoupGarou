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

import org.bukkit.WorldType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;

public class WrapperPlayServerRespawn extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.RESPAWN;

	public WrapperPlayServerRespawn() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerRespawn(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Dimension.
	 * <p>
	 * Notes: -1: The Nether, 0: The Overworld, 1: The End
	 * 
	 * @return The current Dimension
	 */
	public int getDimension() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Dimension.
	 * 
	 * @param value - new value.
	 */
	public void setDimension(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Difficulty.
	 * <p>
	 * Notes: 0 thru 3 for Peaceful, Easy, Normal, Hard.
	 * 
	 * @return The current Difficulty
	 */
	public Difficulty getDifficulty() {
		return handle.getDifficulties().read(0);
	}

	/**
	 * Set Difficulty.
	 * 
	 * @param value - new value.
	 */
	public void setDifficulty(Difficulty value) {
		handle.getDifficulties().write(0, value);
	}

	/**
	 * Retrieve Gamemode.
	 * <p>
	 * Notes: 0: survival, 1: creative, 2: adventure. The hardcore flag is not
	 * included
	 * 
	 * @return The current Gamemode
	 */
	public NativeGameMode getGamemode() {
		return handle.getGameModes().read(0);
	}

	/**
	 * Set Gamemode.
	 * 
	 * @param value - new value.
	 */
	public void setGamemode(NativeGameMode value) {
		handle.getGameModes().write(0, value);
	}

	/**
	 * Retrieve Level Type.
	 * <p>
	 * Notes: same as Join Game
	 * 
	 * @return The current Level Type
	 */
	public WorldType getLevelType() {
		return handle.getWorldTypeModifier().read(0);
	}

	/**
	 * Set Level Type.
	 * 
	 * @param value - new value.
	 */
	public void setLevelType(WorldType value) {
		handle.getWorldTypeModifier().write(0, value);
	}

}
