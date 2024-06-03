package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalEntry;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class CrystalTheme extends CrystalEntry implements ISerializable<CompoundTag, JsonObject> {
   public abstract Optional<Integer> getColor();
}
