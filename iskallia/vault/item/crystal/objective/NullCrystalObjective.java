package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class NullCrystalObjective extends CrystalObjective {
   public static final NullCrystalObjective INSTANCE = new NullCrystalObjective();

   protected NullCrystalObjective() {
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ModConfigs.VAULT_CRYSTAL.getRandomObjective(vault.get(Vault.LEVEL).get(), random).ifPresent(objective -> objective.configure(vault, random));
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Objective: ???").withStyle(ChatFormatting.GRAY));
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.empty();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.empty();
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.empty();
   }

   public void readJson(JsonObject json) {
   }
}
