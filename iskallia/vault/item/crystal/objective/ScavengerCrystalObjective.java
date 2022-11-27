package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.vault.objective.VictoryObjective;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ScavengerCrystalObjective extends CrystalObjective {
   protected float objectiveProbability;

   public ScavengerCrystalObjective() {
   }

   public ScavengerCrystalObjective(float objectiveProbability) {
      this.objectiveProbability = objectiveProbability;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            objectives.add(
               new ScavengerObjective()
                  .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.SCAVENGER, "scavenger", level, true))
                  .add(VictoryObjective.of(300))
            );
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
         }
      );
   }

   @Override
   public Component getName() {
      return new TextComponent("Scavenger Hunt").withStyle(ChatFormatting.GREEN);
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "scavenger");
      object.addProperty("objective_probability", this.objectiveProbability);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.objectiveProbability = json.get("objective_probability").getAsFloat();
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "scavenger");
      nbt.putFloat("objective_probability", this.objectiveProbability);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.objectiveProbability = nbt.getFloat("objective_probability");
   }
}
