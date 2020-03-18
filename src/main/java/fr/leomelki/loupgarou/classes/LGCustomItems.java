package fr.leomelki.loupgarou.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fr.leomelki.loupgarou.events.LGCustomItemChangeEvent;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class LGCustomItems {
	static HashMap<Class<? extends Role>, HashMap<String, Material>> mappings = new HashMap<Class<? extends Role>, HashMap<String,Material>>();
	static {
		JSONParser parser = new JSONParser();
		try {
			JSONObject mappings = (JSONObject)parser.parse("{\"PetiteFille\":{\"\":\"SCUTE\",\"infecte\":\"ORANGE_CONCRETE\",\"infecte_mort\":\"WOODEN_SHOVEL\",\"infecte_maire\":\"SOUL_SAND\",\"maire\":\"EXPERIENCE_BOTTLE\",\"mort\":\"STRING\",\"maire_mort\":\"DARK_OAK_SIGN\",\"infecte_maire_mort\":\"PURPLE_SHULKER_BOX\"},\"LoupGarou\":{\"\":\"OAK_PLANKS\",\"infecte\":\"DRAGON_BREATH\",\"infecte_mort\":\"GOLDEN_PICKAXE\",\"infecte_maire\":\"WITHER_SKELETON_SKULL\",\"maire\":\"TRAPPED_CHEST\",\"maire_mort\":\"BRICK_WALL\",\"mort\":\"MELON\",\"infecte_maire_mort\":\"DARK_OAK_LEAVES\"},\"Corbeau\":{\"infecte_mort\":\"RED_TULIP\",\"infecte\":\"PURPLE_STAINED_GLASS\",\"\":\"HEAVY_WEIGHTED_PRESSURE_PLATE\",\"infecte_maire\":\"PUFFERFISH_BUCKET\",\"maire\":\"DIAMOND_CHESTPLATE\",\"maire_mort\":\"SPRUCE_PLANKS\",\"mort\":\"DARK_OAK_BOAT\",\"infecte_maire_mort\":\"WOODEN_PICKAXE\"},\"LoupGarouBlanc\":{\"infecte_mort\":\"DEAD_TUBE_CORAL_WALL_FAN\",\"infecte\":\"CORNFLOWER\",\"\":\"ANDESITE\",\"infecte_maire\":\"GRINDSTONE\",\"maire\":\"TUBE_CORAL\",\"mort\":\"STONE_PICKAXE\",\"maire_mort\":\"PURPLE_CONCRETE\",\"infecte_maire_mort\":\"CYAN_DYE\"},\"Assassin\":{\"infecte\":\"WITHER_ROSE\",\"\":\"GRAY_CONCRETE_POWDER\",\"infecte_mort\":\"ACACIA_DOOR\",\"infecte_maire\":\"ENCHANTING_TABLE\",\"maire\":\"DEAD_HORN_CORAL_FAN\",\"maire_mort\":\"WOODEN_AXE\",\"mort\":\"HOPPER\",\"infecte_maire_mort\":\"RED_STAINED_GLASS\"},\"Voyante\":{\"infecte_mort\":\"OAK_DOOR\",\"\":\"RED_CONCRETE_POWDER\",\"infecte\":\"END_STONE_BRICKS\",\"infecte_maire\":\"FARMLAND\",\"maire\":\"POPPY\",\"maire_mort\":\"PINK_CONCRETE_POWDER\",\"mort\":\"MUSHROOM_STEM\",\"infecte_maire_mort\":\"WHITE_BANNER\"},\"Dictateur\":{\"\":\"PRISMARINE_SHARD\",\"infecte_mort\":\"CYAN_TERRACOTTA\",\"infecte\":\"CYAN_GLAZED_TERRACOTTA\",\"infecte_maire\":\"REPEATER\",\"maire\":\"GREEN_BED\",\"maire_mort\":\"SPRUCE_TRAPDOOR\",\"mort\":\"DRIED_KELP_BLOCK\",\"infecte_maire_mort\":\"LIME_STAINED_GLASS_PANE\"},\"LoupGarouNoir\":{\"infecte_mort\":\"COBBLESTONE_WALL\",\"\":\"SLIME_BLOCK\",\"infecte\":\"TORCH\",\"infecte_maire\":\"CHORUS_FLOWER\",\"maire\":\"LANTERN\",\"maire_mort\":\"BRAIN_CORAL_WALL_FAN\",\"mort\":\"YELLOW_CARPET\",\"infecte_maire_mort\":\"MOJANG_BANNER_PATTERN\"},\"ChaperonRouge\":{\"\":\"GRASS\",\"infecte\":\"LIME_BANNER\",\"infecte_mort\":\"ORANGE_SHULKER_BOX\",\"infecte_maire\":\"STONE_SLAB\",\"maire\":\"BLUE_GLAZED_TERRACOTTA\",\"mort\":\"PURPLE_TERRACOTTA\",\"maire_mort\":\"YELLOW_DYE\",\"infecte_maire_mort\":\"EMERALD_ORE\"},\"EnfantSauvage\":{\"infecte\":\"GREEN_CARPET\",\"\":\"BLACK_STAINED_GLASS\",\"infecte_mort\":\"CHEST_MINECART\",\"infecte_maire\":\"MAGENTA_STAINED_GLASS\",\"maire\":\"ACACIA_SIGN\",\"mort\":\"GRAY_BANNER\",\"maire_mort\":\"POLISHED_DIORITE_SLAB\",\"infecte_maire_mort\":\"WHITE_STAINED_GLASS_PANE\"},\"Faucheur\":{\"infecte_mort\":\"RED_STAINED_GLASS_PANE\",\"infecte\":\"CYAN_WOOL\",\"\":\"MOSSY_COBBLESTONE_SLAB\",\"infecte_maire\":\"PISTON\",\"maire\":\"GOLDEN_HELMET\",\"maire_mort\":\"BIRCH_FENCE\",\"mort\":\"SKELETON_SKULL\",\"infecte_maire_mort\":\"LIGHT_BLUE_TERRACOTTA\"},\"Cupidon\":{\"\":\"CHORUS_PLANT\",\"infecte_mort\":\"SPRUCE_SIGN\",\"infecte\":\"TRIDENT\",\"infecte_maire\":\"GLOBE_BANNER_PATTERN\",\"maire\":\"YELLOW_STAINED_GLASS_PANE\",\"maire_mort\":\"BROWN_STAINED_GLASS_PANE\",\"mort\":\"GOLDEN_BOOTS\",\"infecte_maire_mort\":\"SEA_LANTERN\"},\"EnfantSauvageLG\":{\"\":\"HORN_CORAL_FAN\",\"infecte_mort\":\"JUNGLE_LOG\",\"infecte\":\"CARTOGRAPHY_TABLE\",\"infecte_maire\":\"BLACK_STAINED_GLASS_PANE\",\"maire\":\"YELLOW_GLAZED_TERRACOTTA\",\"maire_mort\":\"OXEYE_DAISY\",\"mort\":\"STICK\",\"infecte_maire_mort\":\"BROWN_MUSHROOM_BLOCK\"},\"Chasseur\":{\"infecte_mort\":\"COAL\",\"infecte\":\"PRISMARINE_SLAB\",\"\":\"DIORITE\",\"infecte_maire\":\"BLUE_CONCRETE\",\"maire\":\"DEAD_TUBE_CORAL_BLOCK\",\"maire_mort\":\"MAGENTA_TERRACOTTA\",\"mort\":\"CAKE\",\"infecte_maire_mort\":\"YELLOW_WOOL\"},\"Bouffon\":{\"infecte\":\"BIRCH_PLANKS\",\"\":\"LECTERN\",\"infecte_mort\":\"GREEN_CONCRETE_POWDER\",\"infecte_maire\":\"RABBIT_FOOT\",\"maire\":\"FIRE_CORAL_BLOCK\",\"maire_mort\":\"STRIPPED_OAK_WOOD\",\"mort\":\"DEAD_HORN_CORAL_WALL_FAN\",\"infecte_maire_mort\":\"LAPIS_BLOCK\"},\"Detective\":{\"\":\"GLASS_PANE\",\"infecte_mort\":\"RED_BED\",\"infecte\":\"MAGENTA_WOOL\",\"infecte_maire\":\"CHEST\",\"maire\":\"FEATHER\",\"mort\":\"REDSTONE_TORCH\",\"maire_mort\":\"DARK_OAK_BUTTON\",\"infecte_maire_mort\":\"GREEN_WOOL\"},\"GrandMechantLoup\":{\"infecte_mort\":\"LIGHT_BLUE_WOOL\",\"\":\"CHISELED_RED_SANDSTONE\",\"infecte\":\"LIGHT_GRAY_BED\",\"infecte_maire\":\"RABBIT_HIDE\",\"maire\":\"COCOA_BEANS\",\"maire_mort\":\"MILK_BUCKET\",\"mort\":\"PURPLE_GLAZED_TERRACOTTA\",\"infecte_maire_mort\":\"YELLOW_CONCRETE\"},\"Survivant\":{\"\":\"SLIME_BALL\",\"infecte_mort\":\"POPPED_CHORUS_FRUIT\",\"infecte\":\"INFESTED_COBBLESTONE\",\"infecte_maire\":\"GRAY_TERRACOTTA\",\"maire\":\"CHARCOAL\",\"mort\":\"PINK_STAINED_GLASS_PANE\",\"maire_mort\":\"MAGENTA_DYE\",\"infecte_maire_mort\":\"NETHER_BRICK_FENCE\"},\"ChienLoupLG\":{\"\":\"LEATHER_HORSE_ARMOR\",\"infecte\":\"BIRCH_BUTTON\",\"infecte_mort\":\"BLAST_FURNACE\",\"infecte_maire\":\"GLASS_BOTTLE\",\"maire\":\"PODZOL\",\"mort\":\"END_ROD\",\"maire_mort\":\"SPRUCE_LOG\",\"infecte_maire_mort\":\"MAGENTA_CARPET\"},\"Garde\":{\"\":\"BRICK_SLAB\",\"infecte\":\"GRAY_STAINED_GLASS\",\"infecte_mort\":\"DARK_OAK_PLANKS\",\"infecte_maire\":\"PURPUR_SLAB\",\"maire\":\"BLACK_TERRACOTTA\",\"maire_mort\":\"IRON_HELMET\",\"mort\":\"DIRT\",\"infecte_maire_mort\":\"ACACIA_WOOD\"},\"Villageois\":{\"\":\"DEAD_BUSH\",\"infecte_mort\":\"DIAMOND_AXE\",\"infecte\":\"CYAN_BED\",\"infecte_maire\":\"PINK_CONCRETE\",\"maire\":\"LEAD\",\"maire_mort\":\"LIME_CONCRETE\",\"mort\":\"DEAD_BRAIN_CORAL\",\"infecte_maire_mort\":\"SUNFLOWER\"},\"Ange\":{\"\":\"POLISHED_GRANITE_SLAB\",\"infecte\":\"BROWN_BED\",\"infecte_mort\":\"MOSSY_STONE_BRICK_WALL\",\"infecte_maire\":\"DEAD_FIRE_CORAL_WALL_FAN\",\"maire\":\"SANDSTONE\",\"maire_mort\":\"POLISHED_DIORITE\",\"mort\":\"NETHER_BRICK_SLAB\",\"infecte_maire_mort\":\"BONE_MEAL\"},\"ChienLoup\":{\"\":\"GRANITE_SLAB\",\"infecte_mort\":\"JUNGLE_PLANKS\",\"infecte\":\"PAINTING\",\"infecte_maire\":\"MOSSY_COBBLESTONE\",\"maire\":\"SUGAR_CANE\",\"mort\":\"MOSSY_STONE_BRICK_SLAB\",\"maire_mort\":\"RED_BANNER\",\"infecte_maire_mort\":\"END_CRYSTAL\"},\"Medium\":{\"infecte_mort\":\"GRAY_SHULKER_BOX\",\"infecte\":\"LIGHT_BLUE_BANNER\",\"\":\"OAK_TRAPDOOR\",\"infecte_maire\":\"LIGHT_BLUE_STAINED_GLASS_PANE\",\"maire\":\"FERN\",\"maire_mort\":\"WHITE_TERRACOTTA\",\"mort\":\"STICKY_PISTON\",\"infecte_maire_mort\":\"CYAN_SHULKER_BOX\"},\"Pyromane\":{\"\":\"BLUE_ICE\",\"infecte_mort\":\"PURPLE_WOOL\",\"infecte\":\"BEDROCK\",\"infecte_maire\":\"STONE_BRICK_WALL\",\"maire\":\"DEAD_BUBBLE_CORAL_BLOCK\",\"mort\":\"ORANGE_TULIP\",\"maire_mort\":\"PINK_STAINED_GLASS\",\"infecte_maire_mort\":\"GREEN_TERRACOTTA\"},\"Pirate\":{\"infecte\":\"BLUE_STAINED_GLASS\",\"\":\"RED_MUSHROOM_BLOCK\",\"infecte_mort\":\"SPAWNER\",\"infecte_maire\":\"STRIPPED_DARK_OAK_LOG\",\"maire\":\"DROPPER\",\"maire_mort\":\"COBBLESTONE_SLAB\",\"mort\":\"ENDER_CHEST\",\"infecte_maire_mort\":\"IRON_INGOT\"},\"Pretre\":{\"\":\"SNOWBALL\",\"infecte_mort\":\"PINK_BANNER\",\"infecte\":\"ORANGE_BED\",\"infecte_maire\":\"SPRUCE_LEAVES\",\"maire\":\"PRISMARINE_WALL\",\"maire_mort\":\"DEAD_BUBBLE_CORAL\",\"mort\":\"WHITE_SHULKER_BOX\",\"infecte_maire_mort\":\"FLINT\"},\"Sorciere\":{\"\":\"NETHERRACK\",\"infecte_mort\":\"OAK_SIGN\",\"infecte\":\"CONDUIT\",\"infecte_maire\":\"IRON_HORSE_ARMOR\",\"maire\":\"STRIPPED_OAK_LOG\",\"mort\":\"HORN_CORAL_BLOCK\",\"maire_mort\":\"COMPOSTER\",\"infecte_maire_mort\":\"BIRCH_FENCE_GATE\"}}");
			for(Object key : mappings.keySet()) {
				HashMap<String, Material> map = new HashMap<String, Material>();
				JSONObject array = (JSONObject) mappings.get(key);
				for(Object key2 : array.keySet())
					map.put((String)key2, Material.valueOf((String)array.get(key2)));
				try {
					LGCustomItems.mappings.put((Class<? extends Role>)Class.forName("fr.leomelki.loupgarou.roles.R"+key), map);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Material getItem(Role role) {
		return mappings.get(role.getClass()).get("");
	}
	public static Material getItem(LGPlayer player, ArrayList<String> constraints) {
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(player.getGame(), player, constraints));
		
		Collections.sort(constraints);
		HashMap<String, Material> mapps = mappings.get(player.getRole().getClass());
		if(mapps == null)
			return Material.AIR;//Lors du développement de rôles.
		StringJoiner sj = new StringJoiner("_");
		for(String s : constraints)
			sj.add(s);
		return mapps.get(sj.toString());
	}
	public static Material getItem(LGPlayer player) {
		return getItem(player, new ArrayList<String>());
	}
	
	public static void updateItem(LGPlayer lgp) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp)));
		lgp.getPlayer().updateInventory();
	}

	public static void updateItem(LGPlayer lgp, ArrayList<String> constraints) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp, constraints)));
		lgp.getPlayer().updateInventory();
	}
	
	@RequiredArgsConstructor
	public static enum LGCustomItemsConstraints{
		INFECTED("infecte"),
		MAYOR("maire"),
		DEAD("mort");
		@Getter private final String name;
	}
	
}
