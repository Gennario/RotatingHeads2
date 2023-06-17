package cz.gennario.newrotatingheads.developer;

import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.system.RotatingHead;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class RotatingHeadsAPI {

    private final Main instance;
    private ActionDevTools actionDevTools;
    private AnimationDevTools animationDevTools;

    public RotatingHeadsAPI() {
        this.instance = Main.getInstance();
        this.actionDevTools = new ActionDevTools();
        this.animationDevTools = new AnimationDevTools();
    }

    /**
     * Return a list of all the heads in the instance.
     *
     * @return A list of the keys in the map.
     */
    public List<String> getHeadsStringList() {
        return new ArrayList<>(instance.getHeads().keySet());
    }

    /**
     * Convert the set of keys in the map to an array of strings.
     *
     * @return The keys of the map in the instance of the class.
     */
    public String[] getHeadsStringArray() {
        return instance.getHeads().keySet().toArray(new String[0]);
    }

    /**
     * Return a collection of strings that are the keys of the map returned by the getHeads() function.
     *
     * @return A collection of strings.
     */
    public Collection<String> getHeadsStringCollection() {
        return instance.getHeads().keySet();
    }

    /**
     * This function returns a list of all the heads in the plugin.
     *
     * @return A list of all the heads in the map.
     */
    public List<RotatingHead> getHeadsList() {
        return new ArrayList<>(instance.getHeads().values());
    }

    /**
     * Get an array of all the heads in the plugin.
     *
     * @return An array of RotatingHead objects.
     */
    public RotatingHead[] getHeadsArray() {
        return instance.getHeads().values().toArray(new RotatingHead[0]);
    }

    /**
     * Returns a collection of all the heads in the game.
     *
     * @return A collection of all the heads in the instance.
     */
    public Collection<RotatingHead> getHeadsCollection() {
        return instance.getHeads().values();
    }

    /**
     * This function returns a RotatingHead object with the name specified by the parameter.
     *
     * @param name The name of the head.
     * @return The head with the name that is passed in.
     */
    public RotatingHead getHeadByName(String name) {
        return instance.getHeadByName(name);
    }

    /**
     * "Get the head at the specified location, if it exists."
     *
     * The first thing we do is get the world of the location we're looking for. Then we loop through all the heads in the
     * plugin. If the world of the head matches the world of the location we're looking for, we check if the block
     * parameter is true. If it is, we clone the head's location and check if the block location of the clone matches the
     * location we're looking for. If it does, we return the head. If the block parameter is false, we return the head. If
     * the world of the head doesn't match the world of the location we're looking for, we continue the loop. If we get to
     * the end of the loop, we return null
     *
     * @param location The location of the block you want to get the head from.
     * @param block If true, the method will check if the block at the location is the same as the block at the head's
     * location. If false, it will check if the location is the same as the head's location.
     * @return A RotatingHead object
     */
    public @Nullable RotatingHead getHeadByLocation(Location location, boolean block) {
        World world = location.getWorld();

        for (RotatingHead head : getHeadsArray()) {
            World headWorld = head.getLocation().getWorld();

            if(headWorld.equals(world)) {
                if(block) {
                    Location clone = head.getLocation().clone();
                    if(clone.getBlock().getLocation().equals(location)) {
                        return head;
                    }
                }else {
                    if(head.getLocation().equals(location)) {
                        return head;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a new RotatingHead object with the given name and location.
     *
     * @param name The name of the head.
     * @param location The location of the head.
     * @return A RotatingHead object.
     */
    public RotatingHead createRotatingHead(String name, @Nullable Location location) {
        RotatingHead rotatingHead = new RotatingHead(location, name, false);
        instance.getHeads().put(name, rotatingHead);
        return rotatingHead;
    }

    /**
     * It creates a new RotatingHead object
     *
     * @param name The name of the head. This is used to save the head to the config.
     * @param location The location of the head.
     * @param useConfig If true, the head will use the config file to determine the rotation speed. If false, the head will
     * use the default rotation speed.
     * @return A new RotatingHead object.
     */
    public RotatingHead createRotatingHead(String name, @Nullable Location location, boolean useConfig) {
        RotatingHead rotatingHead = new RotatingHead(location, name, useConfig);
        instance.getHeads().put(name, rotatingHead);
        return rotatingHead;
    }

    /**
     * It creates a new RotatingHead object, sets the head type to STAND, and returns the RotatingHead object
     *
     * @param name The name of the head. This is used to identify the head.
     * @param location The location of the head.
     * @param useConfig If true, the head will use the config file to determine the head's rotation speed.
     * @return A RotatingHead object.
     */
    public RotatingHead createStandRotatingHead(String name, @Nullable Location location, boolean useConfig) {
        RotatingHead rotatingHead = new RotatingHead(location, name, useConfig);
        instance.getHeads().put(name, rotatingHead);
        rotatingHead.setHeadType(RotatingHead.HeadType.STAND);
        return rotatingHead;
    }

    /**
     * Create a new rotating head with the name `name`, at the location `location`, with the entity type `entityType`, and
     * use the config if `useConfig` is true
     *
     * @param name The name of the head. This is used to identify the head.
     * @param location The location of the head.
     * @param entityType The entity type of the head.
     * @param useConfig Whether or not to use the config file for the head.
     * @return A RotatingHead object.
     */
    public RotatingHead createEntityRotatingHead(String name, @Nullable Location location, EntityType entityType, boolean useConfig) {
        RotatingHead rotatingHead = new RotatingHead(location, name, useConfig);
        rotatingHead.setHeadType(RotatingHead.HeadType.ENTITY)
                .setEntityType(entityType);
        instance.getHeads().put(name, rotatingHead);
        return rotatingHead;
    }

}
