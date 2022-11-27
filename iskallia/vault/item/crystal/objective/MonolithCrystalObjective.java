package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.MonolithObjective;
import iskallia.vault.core.world.loot.LootRoll;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class MonolithCrystalObjective extends CrystalObjective {
   protected LootRoll target;
   protected float objectiveProbability;

   public MonolithCrystalObjective() {
   }

   public MonolithCrystalObjective(LootRoll target, float objectiveProbability) {
      this.target = target;
      this.objectiveProbability = objectiveProbability;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            objectives.add(
               MonolithObjective.of(this.target.get(random), this.objectiveProbability)
                  .add(
                     FindExitObjective.create(ClassicPortalLogic.EXIT)
                        .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.MONOLITH, "monolith", level, true))
                  )
            );
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
         }
      );
   }

   @Override
   public Component getName() {
      return new TextComponent("Light the Monoliths").withStyle(ChatFormatting.RED);
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "monolith");
      object.add("target", this.target.serializeJson());
      object.addProperty("objective_probability", this.objectiveProbability);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.target = LootRoll.fromJson(json.get("target").getAsJsonObject());
      this.objectiveProbability = json.get("objective_probability").getAsFloat();
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "monolith");
      nbt.put("target", this.target.serializeNBT());
      nbt.putFloat("objective_probability", this.objectiveProbability);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.target = LootRoll.fromNBT(nbt.getCompound("target"));
      this.objectiveProbability = nbt.getFloat("objective_probability");
   }
}
