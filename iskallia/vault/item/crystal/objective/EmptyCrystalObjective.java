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

public class EmptyCrystalObjective extends CrystalObjective {
   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> {
         objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
         objectives.add(DeathObjective.create(true));
         objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Objective: ").append(new TextComponent("None").withStyle(Style.EMPTY.withColor(this.getColor().orElseThrow()))));
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.of(3841972);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag());
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject());
   }

   public void readJson(JsonObject json) {
   }
}
