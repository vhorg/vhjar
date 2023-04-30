package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VeinMinerDurabilityAbility extends VeinMinerAbility {
   private int additionalUnbreakingLevel;

   public VeinMinerDurabilityAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int blockLimit, int additionalUnbreakingLevel) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, blockLimit);
      this.additionalUnbreakingLevel = additionalUnbreakingLevel;
   }

   public VeinMinerDurabilityAbility() {
   }

   public int getAdditionalUnbreakingLevel() {
      return this.additionalUnbreakingLevel;
   }

   @Override
   protected ItemStack getVeinMiningItemProxy(Player player) {
      ItemStack itemStackCopy = super.getVeinMiningItemProxy(player).copy();
      return OverlevelEnchantHelper.increaseUnbreakingBy(itemStackCopy, this.getAdditionalUnbreakingLevel());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.additionalUnbreakingLevel), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalUnbreakingLevel = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalUnbreakingLevel)).ifPresent(tag -> nbt.put("additionalUnbreakingLevel", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalUnbreakingLevel = Adapters.INT.readNbt(nbt.get("additionalUnbreakingLevel")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalUnbreakingLevel)).ifPresent(element -> json.add("additionalUnbreakingLevel", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalUnbreakingLevel = Adapters.INT.readJson(json.get("additionalUnbreakingLevel")).orElse(0);
   }
}
