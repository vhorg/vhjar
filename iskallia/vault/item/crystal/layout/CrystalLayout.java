package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalProperty;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import net.minecraft.nbt.CompoundTag;

public abstract class CrystalLayout extends CrystalProperty implements ISerializable<CompoundTag, JsonObject> {
}
