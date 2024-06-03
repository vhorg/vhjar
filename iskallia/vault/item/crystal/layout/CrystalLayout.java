package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalEntry;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import net.minecraft.nbt.CompoundTag;

public abstract class CrystalLayout extends CrystalEntry implements ISerializable<CompoundTag, JsonObject> {
}
