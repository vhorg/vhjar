package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.KillBossObjective;
import iskallia.vault.core.vault.objective.ObeliskObjective;
import iskallia.vault.core.vault.objective.TrackSpeedrunObjective;
import iskallia.vault.core.world.loot.LootRoll;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class SpeedrunCrystalObjective extends CrystalObjective {
   protected LootRoll target;
   protected float objectiveProbability;

   public SpeedrunCrystalObjective() {
   }

   public SpeedrunCrystalObjective(LootRoll target, float objectiveProbability) {
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
               ObeliskObjective.of(this.target.get(random), this.objectiveProbability)
                  .add(KillBossObjective.ofStandardConfig(level, random).add(FindExitObjective.create(ClassicPortalLogic.EXIT)))
            );
            objectives.add(DeathObjective.create(false));
            objectives.add(TrackSpeedrunObjective.create());
         }
      );
   }

   @Override
   public Component getName() {
      return new TextComponent("Speedrun").withStyle(ChatFormatting.AQUA);
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "speedrun");
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
      nbt.putString("type", "speedrun");
      nbt.put("target", this.target.serializeNBT());
      nbt.putFloat("objective_probability", this.objectiveProbability);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.target = LootRoll.fromNBT(nbt.getCompound("target"));
      this.objectiveProbability = nbt.getFloat("objective_probability");
   }
}
