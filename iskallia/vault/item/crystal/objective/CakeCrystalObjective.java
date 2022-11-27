package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.world.loot.LootRoll;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class CakeCrystalObjective extends CrystalObjective {
   protected LootRoll target;

   public CakeCrystalObjective() {
   }

   public CakeCrystalObjective(LootRoll target) {
      this.target = target;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            objectives.add(
               CakeObjective.of(this.target.get(random), VaultMod.id("cake_adds"))
                  .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.CAKE, "cake", level, true))
                  .add(VictoryObjective.of(300))
            );
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
         }
      );
   }

   @Override
   public Component getName() {
      return new TextComponent("Cake Hunt").withStyle(ChatFormatting.DARK_PURPLE);
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "cake");
      object.add("target", this.target.serializeJson());
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.target = LootRoll.fromJson(json.get("target").getAsJsonObject());
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "cake");
      nbt.put("target", this.target.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.target = LootRoll.fromNBT(nbt.getCompound("target"));
   }
}
