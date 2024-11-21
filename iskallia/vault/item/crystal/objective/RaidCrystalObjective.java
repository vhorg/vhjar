package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class RaidCrystalObjective extends CrystalObjective {
   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = vault.get(Vault.LEVEL).get();
      vault.ifPresent(Vault.OBJECTIVES, objectives -> {
         objectives.add(BailObjective.create(true, ClassicPortalLogic.EXIT));
         objectives.add(DeathObjective.create(true));
         objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
      });
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("Raid").withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor(float time) {
      return Optional.of(13369344);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
   }
}
