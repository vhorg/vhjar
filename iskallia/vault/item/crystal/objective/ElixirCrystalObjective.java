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
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.vault.objective.LodestoneObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ElixirCrystalObjective extends CrystalObjective {
   protected float objectiveProbability;

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            objectives.add(
               ElixirObjective.create()
                  .add(
                     LodestoneObjective.of(this.objectiveProbability)
                        .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.ELIXIR, "elixir", level, true))
                        .add(VictoryObjective.of(300))
                  )
            );
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getId(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("Elixir Rush").withStyle(Style.EMPTY.withColor(this.getColor().orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.of(12719079);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.FLOAT.writeNbt(Float.valueOf(this.objectiveProbability)).ifPresent(tag -> nbt.put("objective_probability", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.objectiveProbability = Adapters.FLOAT.readNbt(nbt.get("objective_probability")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.FLOAT.writeJson(Float.valueOf(this.objectiveProbability)).ifPresent(tag -> json.add("objective_probability", tag));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.objectiveProbability = Adapters.FLOAT.readJson(json.get("objective_probability")).orElse(0.0F);
   }
}