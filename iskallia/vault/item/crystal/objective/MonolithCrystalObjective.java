package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.MonolithObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class MonolithCrystalObjective extends CrystalObjective {
   protected IntRoll target;
   protected float objectiveProbability;

   public MonolithCrystalObjective() {
   }

   public MonolithCrystalObjective(IntRoll target, float objectiveProbability) {
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
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         new TextComponent("Objective: ").append(new TextComponent("Light the Monoliths").withStyle(Style.EMPTY.withColor(this.getColor().orElseThrow())))
      );
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.ofNullable(ChatFormatting.RED.getColor());
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
