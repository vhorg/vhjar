package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.KillBossObjective;
import iskallia.vault.core.vault.objective.LegacyObeliskObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.TrackSpeedrunObjective;
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

public class SpeedrunCrystalObjective extends CrystalObjective {
   protected IntRoll target;
   protected float objectiveProbability;

   public SpeedrunCrystalObjective() {
   }

   public SpeedrunCrystalObjective(IntRoll target, float objectiveProbability) {
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
               LegacyObeliskObjective.of(this.target.get(random), this.objectiveProbability)
                  .add(KillBossObjective.ofStandardConfig(level, random).add(FindExitObjective.create(ClassicPortalLogic.EXIT)))
            );
            objectives.add(DeathObjective.create(false));
            objectives.add(TrackSpeedrunObjective.create());
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("Speedrun").withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor(float time) {
      return Optional.ofNullable(ChatFormatting.AQUA.getColor());
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
