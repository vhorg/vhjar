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
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.GridGatewayObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class BingoCrystalObjective extends CrystalObjective {
   protected float objectiveProbability;

   public BingoCrystalObjective() {
   }

   public BingoCrystalObjective(float objectiveProbability) {
      this.objectiveProbability = objectiveProbability;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            ModConfigs.BINGO
               .generate(VaultMod.id("default"), level)
               .ifPresent(
                  task -> objectives.add(
                     BingoObjective.of(task)
                        .add(
                           GridGatewayObjective.of(this.objectiveProbability)
                              .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.BINGO, "bingo", level, true))
                              .add(VictoryObjective.of(300))
                        )
                  )
               );
            objectives.add(BailObjective.create(true, ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("Bingo").withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor(float time) {
      return Optional.of(4821183);
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