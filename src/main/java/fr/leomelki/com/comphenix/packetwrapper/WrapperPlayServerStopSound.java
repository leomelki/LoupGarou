/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.leomelki.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.MinecraftKey;

public class WrapperPlayServerStopSound extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.STOP_SOUND;
    
    public WrapperPlayServerStopSound() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerStopSound(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    public MinecraftKey getSoundEffect() {
        return handle.getMinecraftKeys().readSafely(0);
    }

    public void setSoundEffect(MinecraftKey value) {
        handle.getMinecraftKeys().writeSafely(0, value);
    }

    public EnumWrappers.SoundCategory getCategory() {
    	return handle.getSoundCategories().readSafely(0);
    }

    public void setCategory(EnumWrappers.SoundCategory value) {
    	handle.getSoundCategories().writeSafely(0, value);
    }
}
