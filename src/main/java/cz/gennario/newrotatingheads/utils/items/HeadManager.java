package cz.gennario.newrotatingheads.utils.items;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import cz.gennario.newrotatingheads.Main;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

/**
 * HeadSystem [1.1]
 * <p>
 * Util class that solves a bug with gaining a player's head on versions 1.16 and higher.
 * Created by Gennario with <3
 */
public final class HeadManager {

    public enum CacheType {
        CONFIG,
        MEMORY
    }
    public static final CacheType cacheType = CacheType.valueOf(Main.getInstance().getConfigFile().getYamlDocument().getString("skull-cache.type"));
    public static final Map<String, String> memoryCache = new HashMap<>();

    /**
     * Generation head type enum
     */
    public enum HeadType {
        PLAYER_HEAD,
        BASE64
    }

    /**
     * With this method you can get a player's head by nickname or a base64 head by base64 code
     *
     * @param type  Determines whether you want to get the head by name or by base64
     * @param value If you want a player's head, then the player's name. If you want base64, then base64 code.
     * @return Head itemStack
     */
    public static ItemStack convert(HeadType type, String value) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta itemMeta = (SkullMeta) head.getItemMeta();

        if (type.equals(HeadType.PLAYER_HEAD)) {
            assert itemMeta != null;
            try {
                head = getSkullByTexture(getPlayerHeadTexture(value));
            }catch (Exception e) {
                e.printStackTrace();
            }

            return head;
        } else {
            return getSkullByTexture(value);
        }
    }

    private static ItemStack getSkullByTexture(String base64) {
        ItemStack head = getAllVersionStack("SKULL_ITEM", "PLAYER_HEAD", 3);
        if (base64.isEmpty() || base64.equals("none")) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // If the Minecraft version is 1.18.1 or higher, use the new API methods.
        int version = Integer.parseInt(Bukkit.getServer().getClass().getName().split("\\.")[3].split("_")[1]);
        if (version >= 18) {
            String skinJson = new String(Base64.getDecoder().decode(base64));
            JsonObject skinObject = new JsonParser().parse(skinJson).getAsJsonObject();
            String url = skinObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            URL urlObject;
            try {
                urlObject = new URL(url);
            } catch (MalformedURLException exception) {
                throw new RuntimeException("Invalid URL", exception);
            }
            textures.setSkin(urlObject);
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);

        } else {
            try {
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", base64));
                Field profileField;
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);

            } catch (NoSuchFieldException | IllegalAccessException e) {
               Bukkit.getLogger().log(Level.SEVERE, "Unexpected error!");
            }
        }
        head.setItemMeta(meta);
        return head;
    }

    public static String getPlayerHeadTexture(String username) {
        try {
            String UUIDJson = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + username), StandardCharsets.UTF_8);
            JsonObject uuidObject = new JsonParser().parse(UUIDJson).getAsJsonObject();
            String dashlessUuid = uuidObject.get("id").getAsString();

            String profileJson = IOUtils.toString(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid), StandardCharsets.UTF_8);
            JsonObject profileObject = new JsonParser().parse(profileJson).getAsJsonObject();
            return profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();

        } catch (IOException e) {
            Bukkit.getLogger().log(java.util.logging.Level.WARNING, "The player name " + username + " does not exist!");
            return "none";
        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    private static ItemStack getAllVersionStack(String oldName, String newName, int data) {
        Material material = null;
        try {
            material = Material.valueOf(oldName);
        } catch (Exception exception) {
            material = Material.valueOf(newName);
            data = 0;
        }
        return new ItemStack(material, 1, (byte) data);
    }

    public static String getFromCache(String username) {
        switch (cacheType) {
            case MEMORY:
                return memoryCache.getOrDefault(username, null);
            case CONFIG:
                if(Main.getInstance().getHeadCache().getYamlDocument().getSection("values").contains(username)) {
                    return Main.getInstance().getHeadCache().getYamlDocument().getString("values."+username);
                }

                return null;
        }
        return null;
    }

    public static void saveToCache(String username, String value) {
        switch (cacheType) {
            case MEMORY:
                memoryCache.put(username, value);
                return;
            case CONFIG:
                Main.getInstance().getHeadCache().getYamlDocument().set("values."+username, value);
                try {
                    Main.getInstance().getHeadCache().getYamlDocument().save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

        }
    }

}