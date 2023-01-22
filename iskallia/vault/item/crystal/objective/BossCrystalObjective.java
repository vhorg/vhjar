package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.ObeliskObjective;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.world.loot.LootRoll;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class BossCrystalObjective extends CrystalObjective {
   protected LootRoll target;
   protected LootRoll wave;
   protected float objectiveProbability;

   public BossCrystalObjective() {
   }

   public BossCrystalObjective(LootRoll target, LootRoll wave, float objectiveProbability) {
      this.target = target;
      this.wave = wave;
      this.objectiveProbability = objectiveProbability;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            objectives.add(
               ObeliskObjective.of(this.target.get(random), () -> this.wave.get(random), this.objectiveProbability)
                  .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.BOSS, "boss", level, true))
                  .add(VictoryObjective.of(300))
            );
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
         }
      );
   }

   @Override
   public Component getName() {
      return new TextComponent("Hunt the Guardians").withStyle(ChatFormatting.GOLD);
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "boss");
      object.add("target", this.target.serializeJson());
      object.add("wave", this.wave.serializeJson());
      object.addProperty("objective_probability", this.objectiveProbability);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.target = LootRoll.fromJson(json.get("target").getAsJsonObject());
      this.wave = LootRoll.fromJson(json.get("wave").getAsJsonObject());
      this.objectiveProbability = json.get("objective_probability").getAsFloat();
      if (this.wave == null) {
         this.wave = LootRoll.ofConstant(3);
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "boss");
      nbt.put("target", this.target.serializeNBT());
      nbt.put("wave", this.wave.serializeNBT());
      nbt.putFloat("objective_probability", this.objectiveProbability);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.target = LootRoll.fromNBT(nbt.getCompound("target"));
      this.wave = LootRoll.fromNBT(nbt.getCompound("wave"));
      this.objectiveProbability = nbt.getFloat("objective_probability");
      if (this.wave == null) {
         this.wave = LootRoll.ofConstant(3);
      }
   }
}
