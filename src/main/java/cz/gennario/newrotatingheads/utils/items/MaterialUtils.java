package cz.gennario.newrotatingheads.utils.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class MaterialUtils {

    public static boolean checkMaterial(String name) {
        return getMaterial(name) != null;
    }

    public static Material getMaterial(String name) {
        return Material.matchMaterial(name);
    }

    public static Material getAllVersionMaterial(String oldName, String newName) {
        Material material = null;
        try {
            material = Material.valueOf(oldName);
        } catch (Exception exception) {
            material = Material.valueOf(newName);
        }
        return material;
    }

    public static ItemStack getAllVersionStack(String oldName, String newName, int data) {
        Material material = null;
        try {
            material = Material.valueOf(oldName);
        } catch (Exception exception) {
            material = Material.valueOf(newName);
            data = 0;
        }
        return new ItemStack(material, 1, (byte) data);
    }

}
