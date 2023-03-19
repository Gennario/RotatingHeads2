package cz.gennario.newrotatingheads.utils.items;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import cz.gennario.newrotatingheads.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material, int amount) {
        this.setItem(material, amount);
    }

    public ItemBuilder(Material material) {
        this.setItem(material, 1);
    }

    public ItemBuilder() {
        this.setItem(Material.DIRT, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.setItem(itemStack);
    }

    public ItemBuilder(XMaterial xMaterial) {
        this.setItem(xMaterial.parseItem());
    }

    public ItemBuilder(Material material, int amount, int data) {
        this.setItem(material, amount, (short) data);
    }

    public ItemBuilder(Material material, int amount, short data) {
        this.setItem(material, amount, data);
    }

    public ItemBuilder setItem(Material material, int amount, short data) {
        this.item = new ItemStack(material, amount, data);
        this.meta = this.item.getItemMeta();
        return this;
    }

    public ItemBuilder setItem(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
        return this;
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(Utils.colorize(null, name));
        this.update();
        return this;
    }

    public ItemBuilder setUnbreakable(Boolean bol) {
        ItemMeta im = this.meta;
        ((ItemBuilder) im).setUnbreakable(bol);
        this.meta = im;
        this.update();
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.meta.setLore(lore);
        this.update();
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.meta.setLore(Utils.colorize(null, lore));
        this.update();
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            final SkullMeta im = (SkullMeta) this.item.getItemMeta();
            im.setOwner(owner);
            this.item.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setSkinBase64(String skinUrl) {
        SkullMeta skullMeta = (SkullMeta) this.meta;
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", skinUrl));
        Field profileField;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var6) {
            var6.printStackTrace();
        }

        this.update();
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        this.update();
        return this;
    }

    public ItemBuilder addFlag(ItemFlag itemFlag) {
        this.meta.addItemFlags(itemFlag);
        this.update();
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        update();
        return this;
    }

    public ItemBuilder setEnchant(Enchantment enchant, int level) {
        if (enchant != null) {
            this.meta.addEnchant(enchant, level, false);
            this.update();
        }
        return this;
    }

    public ItemBuilder update() {
        this.item.setItemMeta(this.meta);
        return this;
    }

    public String getName() {
        return getMeta().getDisplayName();
    }

    public List<String> getLore() {
        return getMeta().getLore() != null ? getMeta().getLore() : new ArrayList<>();
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemBuilder setItem(ItemStack itemStack) {
        this.item = itemStack;
        this.meta = this.item.getItemMeta();
        return this;
    }

    public ItemMeta getMeta() {
        return this.meta;
    }

}
