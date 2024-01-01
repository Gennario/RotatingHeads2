package cz.gennario.newrotatingheads.utils.items;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.alexmc.api.THeadsAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemSystem {

    public static ItemStack itemFromConfig(YamlDocument configManager, String path) {
        YamlDocument configuration = configManager;
        Section section = configuration.getSection(path);

        Material material = XMaterial.matchXMaterial(section.getString("material")).get().parseMaterial();
        int amount = 1;
        if (section.getString("amount") != null) amount = section.getInt("amount");
        byte data = 0;
        if (section.getString("data") != null) data = section.getByte("data");
        String name = null;
        if (section.getString("name") != null) name = Utils.colorize(null, section.getString("name"));
        List<String> lore = new ArrayList<>();
        if (section.getString("lore") != null) {
            for (String line : section.getStringList("lore")) {
                lore.add(Utils.colorize(null, line.replace('&', '§')));
            }
        }
        List<String> enchants = new ArrayList<>();
        if (section.getString("enchants") != null) {
            enchants.addAll(section.getStringList("enchants"));
        }
        List<String> itemFlags = new ArrayList<>();
        if (section.getString("itemflags") != null) {
            itemFlags.addAll(section.getStringList("itemflags"));
        }
        int customModelData = 0;
        if (section.getString("custommodeldata") != null) {
            customModelData = section.getInt("custommodeldata");
        }
        boolean unbreakable = false;
        if (section.getString("unbreakable") != null) {
            unbreakable = section.getBoolean("unbreakable");
        }

        // ITEM GENERATE

        ItemStack itemStack = null;
        if (section.getString("base64") != null) {
            itemStack = HeadManager.convert(HeadManager.HeadType.BASE64, section.getString("base64"));
        } else if (section.getString("skin") != null) {
            itemStack = HeadManager.convert(HeadManager.HeadType.PLAYER_HEAD, section.getString("skin"));
        } else {
            itemStack = new ItemStack(material, amount, data);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        if (name != null) itemMeta.setDisplayName(name);
        if (!lore.isEmpty()) itemMeta.setLore(lore);
        if (!enchants.isEmpty()) {
            for (String e : enchants) {
                itemMeta.addEnchant(Objects.requireNonNull(XEnchantment.matchXEnchantment(e.split(";")[0]).get().getEnchant()), Integer.parseInt(e.split(";")[1]), true);
            }
        }
        if (!unbreakable) itemMeta.setUnbreakable(unbreakable);
        if (!itemFlags.isEmpty()) itemFlags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        if (customModelData != 0) itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        if (itemMeta instanceof LeatherArmorMeta) {
            if (section.contains("leather")) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                String[] split = section.getString("leather").split(";");
                leatherArmorMeta.setColor(Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                itemStack.setItemMeta(leatherArmorMeta);
            }
        }

        return itemStack;
    }

    public static ItemStack itemFromConfig(Section section) {

        Material material = XMaterial.matchXMaterial(section.getString("material")).get().parseMaterial();
        int amount = 1;
        if (section.getString("amount") != null) amount = section.getInt("amount");
        byte data = 0;
        if (section.getString("data") != null) data = section.getByte("data");
        String name = null;
        if (section.getString("name") != null) name = Utils.colorize(null, section.getString("name"));
        List<String> lore = new ArrayList<>();
        if (section.getString("lore") != null) {
            for (String line : section.getStringList("lore")) {
                lore.add(Utils.colorize(null, line.replace('&', '§')));
            }
        }
        List<String> enchants = new ArrayList<>();
        if (section.getString("enchants") != null) {
            enchants.addAll(section.getStringList("enchants"));
        }
        List<String> itemFlags = new ArrayList<>();
        if (section.getString("itemflags") != null) {
            itemFlags.addAll(section.getStringList("itemflags"));
        }
        int customModelData = 0;
        if (section.getString("custommodeldata") != null) {
            customModelData = section.getInt("custommodeldata");
        }
        boolean unbreakable = false;
        if (section.getString("unbreakable") != null) {
            unbreakable = section.getBoolean("unbreakable");
        }

        // ITEM GENERATE

        ItemStack itemStack = null;
        if (section.getString("base64") != null) {
            itemStack = HeadManager.convert(HeadManager.HeadType.BASE64, section.getString("base64"));
        } else if (section.getString("player") != null) {
            String toReplace;
            if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                toReplace = PlaceholderAPI.setPlaceholders(null, section.getString("player"));
            } else {
                toReplace = section.getString("player");
            }
            itemStack = HeadManager.convert(HeadManager.HeadType.PLAYER_HEAD, toReplace);
        } else {
            itemStack = new ItemStack(material, amount, data);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        if (name != null) itemMeta.setDisplayName(name);
        if (!lore.isEmpty()) itemMeta.setLore(lore);
        if (!enchants.isEmpty()) {
            for (String e : enchants) {
                itemMeta.addEnchant(Objects.requireNonNull(XEnchantment.matchXEnchantment(e.split(";")[0]).get().getEnchant()), Integer.parseInt(e.split(";")[1]), true);
            }
        }
        if (!unbreakable) itemMeta.setUnbreakable(unbreakable);
        if (!itemFlags.isEmpty()) itemFlags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        if (customModelData != 0) itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        if (itemMeta instanceof LeatherArmorMeta) {
            if (section.contains("leather")) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                String[] split = section.getString("leather").split(";");
                leatherArmorMeta.setColor(Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                itemStack.setItemMeta(leatherArmorMeta);
            }
        }

        return itemStack;
    }

    public static ItemStack itemFromConfig(Section section, Player player, Replacement replacement) {

        Material material = XMaterial.matchXMaterial(section.getString("material")).get().parseMaterial();
        int amount = 1;
        if (section.getString("amount") != null) amount = section.getInt("amount");
        byte data = 0;
        if (section.getString("data") != null) data = section.getByte("data");
        String name = null;
        if (section.getString("name") != null)
            name = Utils.colorize(player, replacement.replace(player, section.getString("name")));
        List<String> lore = new ArrayList<>();
        if (section.getString("lore") != null) {
            for (String line : section.getStringList("lore")) {
                lore.add(Utils.colorize(null, replacement.replace(player, line.replace('&', '§'))));
            }
        }
        List<String> enchants = new ArrayList<>();
        if (section.getString("enchants") != null) {
            enchants.addAll(section.getStringList("enchants"));
        }
        List<String> itemFlags = new ArrayList<>();
        if (section.getString("itemflags") != null) {
            itemFlags.addAll(section.getStringList("itemflags"));
        }
        int customModelData = 0;
        if (section.getString("custommodeldata") != null) {
            customModelData = section.getInt("custommodeldata");
        }
        boolean unbreakable = false;
        if (section.getString("unbreakable") != null) {
            unbreakable = section.getBoolean("unbreakable");
        }

        // ITEM GENERATE

        ItemStack itemStack = null;
        if (section.contains("base64")) {
            itemStack = HeadManager.convert(HeadManager.HeadType.BASE64, replacement.replace(player, section.getString("base64")));
        } else if (section.contains("player")) {
            String toReplace;
            if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                toReplace = PlaceholderAPI.setPlaceholders(player, section.getString("player"));
            } else {
                toReplace = section.getString("player");
            }
            itemStack = HeadManager.convert(HeadManager.HeadType.PLAYER_HEAD, replacement.replace(player, toReplace));
        } else if (section.contains("transparent-head")) {
            Section section1 = section.getSection("transparent-head");
            String location = section1.getString("location");
            Boolean aBoolean = section1.getBoolean("include-default-url");
            itemStack = THeadsAPI.getInstance().getHeadItem(location, aBoolean);
        } else {
            itemStack = new ItemStack(material, amount, data);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        if (name != null) itemMeta.setDisplayName(name);
        if (!lore.isEmpty()) itemMeta.setLore(lore);
        if (!enchants.isEmpty()) {
            for (String e : enchants) {
                itemMeta.addEnchant(Objects.requireNonNull(XEnchantment.matchXEnchantment(e.split(";")[0]).get().getEnchant()), Integer.parseInt(e.split(";")[1]), true);
            }
        }
        if (!unbreakable) itemMeta.setUnbreakable(unbreakable);
        if (!itemFlags.isEmpty()) itemFlags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        if (customModelData != 0) itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        if (itemMeta instanceof LeatherArmorMeta) {
            if (section.contains("leather")) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                String[] split = section.getString("leather").split(";");
                leatherArmorMeta.setColor(Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                itemStack.setItemMeta(leatherArmorMeta);
            }
        }

        return itemStack;
    }

}
