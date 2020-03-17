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
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerTitle extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.TITLE;

	public WrapperPlayServerTitle() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerTitle(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Action.
	 * 
	 * @return The current Action
	 */
	public TitleAction getAction() {
		return handle.getTitleActions().read(0);
	}

	/**
	 * Set Action.
	 * 
	 * @param value - new value.
	 */
	public void setAction(TitleAction value) {
		handle.getTitleActions().write(0, value);
	}

	/**
	 * Retrieve 0 (TITLE).
	 * <p>
	 * Notes: chat
	 * 
	 * @return The current 0 (TITLE)
	 */
	public WrappedChatComponent getTitle() {
		return handle.getChatComponents().read(0);
	}

	/**
	 * Set 0 (TITLE).
	 * 
	 * @param value - new value.
	 */
	public void setTitle(WrappedChatComponent value) {
		handle.getChatComponents().write(0, value);
	}

	/**
	 * Retrieve 2 (TIMES).
	 * <p>
	 * Notes: int
	 * 
	 * @return The current 2 (TIMES)
	 */
	public int getFadeIn() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set 2 (TIMES).
	 * 
	 * @param value - new value.
	 */
	public void setFadeIn(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Stay.
	 * 
	 * @return The current Stay
	 */
	public int getStay() {
		return handle.getIntegers().read(1);
	}

	/**
	 * Set Stay.
	 * 
	 * @param value - new value.
	 */
	public void setStay(int value) {
		handle.getIntegers().write(1, value);
	}

	/**
	 * Retrieve Fade Out.
	 * 
	 * @return The current Fade Out
	 */
	public int getFadeOut() {
		return handle.getIntegers().read(2);
	}

	/**
	 * Set Fade Out.
	 * 
	 * @param value - new value.
	 */
	public void setFadeOut(int value) {
		handle.getIntegers().write(2, value);
	}
}
