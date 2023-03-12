package iskallia.vault.item.crystal.time;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public abstract class CrystalTime implements ISerializable<CompoundTag, JsonObject> {
   public abstract void configure(Vault var1, RandomSource var2);

   public abstract void addText(List<Component> var1, TooltipFlag var2);
}
