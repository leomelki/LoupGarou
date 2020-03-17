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

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class WrapperPlayServerNamedEntitySpawn extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.NAMED_ENTITY_SPAWN;

	public WrapperPlayServerNamedEntitySpawn() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerNamedEntitySpawn(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Entity ID.
	 * <p>
	 * Notes: entity's ID
	 * 
	 * @return The current Entity ID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Entity ID.
	 * 
	 * @param value - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve the entity of the painting that will be spawned.
	 * 
	 * @param world - the current world of the entity.
	 * @return The spawned entity.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Retrieve the entity of the painting that will be spawned.
	 * 
	 * @param event - the packet event.
	 * @return The spawned entity.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	/**
	 * Retrieve Player UUID.
	 * <p>
	 * Notes: player's UUID
	 * 
	 * @return The current Player UUID
	 */
	public UUID getPlayerUUID() {
		return handle.getUUIDs().read(0);
	}

	/**
	 * Set Player UUID.
	 * 
	 * @param value - new value.
	 */
	public void setPlayerUUID(UUID value) {
		handle.getUUIDs().write(0, value);
	}

	/**
	 * Retrieve the position of the spawned entity as a vector.
	 * 
	 * @return The position as a vector.
	 */
	public Vector getPosition() {
		return new Vector(getX(), getY(), getZ());
	}

	/**
	 * Set the position of the spawned entity using a vector.
	 * 
	 * @param position - the new position.
	 */
	public void setPosition(Vector position) {
		setX(position.getX());
		setY(position.getY());
		setZ(position.getZ());
	}

	public double getX() {
		return handle.getDoubles().read(0);
	}

	public void setX(double value) {
		handle.getDoubles().write(0, value);
	}

	public double getY() {
		return handle.getDoubles().read(1);
	}

	public void setY(double value) {
		handle.getDoubles().write(1, value);
	}

	public double getZ() {
		return handle.getDoubles().read(2);
	}

	public void setZ(double value) {
		handle.getDoubles().write(2, value);
	}

	/**
	 * Retrieve the yaw of the spawned entity.
	 * 
	 * @return The current Yaw
	 */
	public float getYaw() {
		return (handle.getBytes().read(0) * 360.F) / 256.0F;
	}

	/**
	 * Set the yaw of the spawned entity.
	 * 
	 * @param value - new yaw.
	 */
	public void setYaw(float value) {
		handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve the pitch of the spawned entity.
	 * 
	 * @return The current pitch
	 */
	public float getPitch() {
		return (handle.getBytes().read(1) * 360.F) / 256.0F;
	}

	/**
	 * Set the pitch of the spawned entity.
	 * 
	 * @param value - new pitch.
	 */
	public void setPitch(float value) {
		handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
	}

	/**
	 * Retrieve Metadata.
	 * <p>
	 * Notes: the client will crash if no metadata is sent
	 * 
	 * @return The current Metadata
	 */
	public WrappedDataWatcher getMetadata() {
		return handle.getDataWatcherModifier().read(0);
	}

	/**
	 * Set Metadata.
	 * 
	 * @param value - new value.
	 */
	public void setMetadata(WrappedDataWatcher value) {
		handle.getDataWatcherModifier().write(0, value);
	}
}
