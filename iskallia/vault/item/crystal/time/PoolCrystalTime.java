package iskallia.vault.item.crystal.time;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public class PoolCrystalTime extends CrystalTime {
   private ResourceLocation id;

   public PoolCrystalTime() {
   }

   public PoolCrystalTime(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ModConfigs.VAULT_CRYSTAL.getRandomTime(this.id, vault.get(Vault.LEVEL).get(), random).ifPresent(time -> time.configure(vault, random));
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.IDENTIFIER.writeNbt(this.id).ifPresent(id -> nbt.put("id", id));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag compound) {
      this.id = Adapters.IDENTIFIER.readNbt(compound.get("id")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.IDENTIFIER.writeJson(this.id).ifPresent(id -> json.add("id", id));
      return Optional.of(json);
   }

   public void readJson(JsonObject object) {
      this.id = Adapters.IDENTIFIER.readJson(object.get("id")).orElse(null);
   }
}
