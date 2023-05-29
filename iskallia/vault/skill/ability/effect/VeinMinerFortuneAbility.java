package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VeinMinerFortuneAbility extends VeinMinerAbility {
   private int additionalFortuneLevel;

   public VeinMinerFortuneAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int blockLimit, int additionalFortuneLevel) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, blockLimit);
      this.additionalFortuneLevel = additionalFortuneLevel;
   }

   public VeinMinerFortuneAbility() {
   }

   public int getAdditionalFortuneLevel() {
      return this.additionalFortuneLevel;
   }

   @Override
   protected ItemStack getVeinMiningItemProxy(Player player) {
      ItemStack stack = super.getVeinMiningItemProxy(player).copy();
      return OverlevelEnchantHelper.increaseFortuneBy(stack, this.getAdditionalFortuneLevel());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.additionalFortuneLevel), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalFortuneLevel = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalFortuneLevel)).ifPresent(tag -> nbt.put("additionalFortuneLevel", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalFortuneLevel = Adapters.INT.readNbt(nbt.get("additionalFortuneLevel")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalFortuneLevel)).ifPresent(element -> json.add("additionalFortuneLevel", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalFortuneLevel = Adapters.INT.readJson(json.get("additionalFortuneLevel")).orElse(0);
   }
}
