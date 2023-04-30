package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.objective.CrakePedestalObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class CakeCrystalObjective extends CrystalObjective {
   protected IntRoll target;
   private float objectiveProbability;

   public CakeCrystalObjective() {
   }

   public CakeCrystalObjective(IntRoll target) {
      this.target = target;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            if (this.target == null) {
               objectives.add(CakeObjective.of(VaultMod.id("cake_adds")))
                  .add(
                     CrakePedestalObjective.of(this.objectiveProbability)
                        .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.CAKE, "cake", level, true))
                        .add(VictoryObjective.of(300))
                  );
            } else {
               objectives.add(
                  CakeObjective.of(this.target.get(random), VaultMod.id("cake_adds"))
                     .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.CAKE, "cake", level, true))
                     .add(VictoryObjective.of(300))
               );
            }

            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("Cake Hunt").withStyle(Style.EMPTY.withColor(this.getColor().orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.of(16220086);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT_ROLL.writeNbt(this.target).ifPresent(target -> nbt.put("target", target));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.objectiveProbability)).ifPresent(tag -> nbt.put("objective_probability", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.target = Adapters.INT_ROLL.readNbt(nbt.getCompound("target")).orElse(null);
      this.objectiveProbability = Adapters.FLOAT.readNbt(nbt.get("objective_probability")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT_ROLL.writeJson(this.target).ifPresent(target -> json.add("target", target));
      Adapters.FLOAT.writeJson(Float.valueOf(this.objectiveProbability)).ifPresent(tag -> json.add("objective_probability", tag));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.target = Adapters.INT_ROLL.readJson(json.getAsJsonObject("target")).orElse(null);
      this.objectiveProbability = Adapters.FLOAT.readJson(json.get("objective_probability")).orElse(0.0F);
   }
}
