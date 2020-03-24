package fr.leomelki.loupgarou.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;

public class ItemBuilder implements Cloneable {
    private ItemStack is;

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param material The material to create the ItemBuilder with.
     */
    @ConstructorProperties({"material"})
    public ItemBuilder(Material material)
    {
        this(material, 1);
    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is)
    {
        this.is = is;
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount)
    {
        is = new ItemStack(m, amount);
    }

    /**
     * Clone the ItemBuilder into a new one.
     *
     * @return The cloned instance.
     */
    @Override
    public ItemBuilder clone()
    {
        return new ItemBuilder(is);
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     * @return
     */
    public ItemBuilder setName(String name)
    {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Retrieves the itemstack from the ItemBuilder.
     *
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack build()
    {
        return is;
    }
}
