package fr.leomelki.loupgarou.utils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class VariousUtils {
	public static double distanceSquaredXZ(Location from, Location to) {
		return Math.pow(from.getX()-to.getX(), 2)+Math.pow(from.getZ()-to.getZ(), 2);
	}
	public static void setWarning(Player p, boolean warning) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
		WorldBorder wb = p.getWorld().getWorldBorder();

		container.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE);

		container.getIntegers().write(0, 29999984);

		container.getDoubles().write(0, p.getLocation().getX());
		container.getDoubles().write(1, p.getLocation().getZ());

		container.getDoubles().write(3, wb.getSize());
		container.getDoubles().write(2, wb.getSize());

		container.getIntegers().write(2, (int) (warning ? wb.getSize() : wb.getWarningDistance()));
		container.getIntegers().write(1, 0);
		
		container.getLongs().write(0, (long) 0);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private static char[] hex = "0123456789abcdef".toCharArray();
	public static char toHex(int i) {
		return hex[i];
	}

}
