package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public class PoolCrystalObjective extends CrystalObjective {
   private ResourceLocation id;

   public PoolCrystalObjective() {
   }

   public PoolCrystalObjective(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ModConfigs.VAULT_CRYSTAL.getRandomObjective(this.id, vault.get(Vault.LEVEL).get(), random).ifPresent(child -> child.configure(vault, random));
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Objective: ???").withStyle(ChatFormatting.GRAY));
   }

   @Override
   public Optional<Integer> getColor(float time) {
      return Optional.empty();
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
