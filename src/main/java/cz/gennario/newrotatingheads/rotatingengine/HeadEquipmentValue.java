package cz.gennario.newrotatingheads.rotatingengine;

import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.items.ItemSystem;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
@Setter
public class HeadEquipmentValue {

    public enum HeadEquipmentType {
        SYSTEM,
        CONFIG
    }

    private HeadEquipmentType type;
    private Section configData;
    private Replacement replacement;
    private ItemStack systemStack;

    public HeadEquipmentValue(@NotNull HeadEquipmentType type) {
        this.type = type;
        this.replacement = new Replacement(Utils::colorize);
    }

    public @Nullable ItemStack convert(@Nullable Player player) {
        switch (type) {
            case CONFIG:
                return ItemSystem.itemFromConfig(configData, player, replacement);
            case SYSTEM:
                return systemStack;
        }
        return null;
    }

}
