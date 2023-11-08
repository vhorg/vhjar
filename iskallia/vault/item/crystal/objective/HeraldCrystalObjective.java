package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.HeraldObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.time.TickStopwatch;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;

public class HeraldCrystalObjective extends CrystalObjective {
   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.set(Vault.SEED, Long.valueOf(0L));
      vault.set(Vault.CLOCK, new TickStopwatch());
      if (vault.get(Vault.LISTENERS).get(Listeners.LOGIC) instanceof ClassicListenersLogic classic) {
         classic.set(ClassicListenersLogic.GAME_MODE, GameType.ADVENTURE);
         classic.set(ClassicListenersLogic.MIN_LEVEL, Integer.valueOf(vault.get(Vault.LEVEL).get()));
         classic.set(ClassicListenersLogic.ADDED_BONUS_TIME);
      }

      vault.get(Vault.WORLD).set(WorldManager.FACING, Direction.EAST);
      vault.ifPresent(Vault.OBJECTIVES, objectives -> {
         objectives.add(HeraldObjective.of().add(FindExitObjective.create(ClassicPortalLogic.EXIT)).add(VictoryObjective.empty()));
         objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
         objectives.add(DeathObjective.create(true));
         objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(
         new TextComponent("Objective: ").append(new TextComponent("Defeat the Herald").withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow())))
      );
   }

   @Override
   public Optional<Integer> getColor(float time) {
      return Optional.of(9404279);
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
