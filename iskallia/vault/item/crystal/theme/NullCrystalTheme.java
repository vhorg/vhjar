package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
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

public class NullCrystalTheme extends CrystalTheme {
   public static final NullCrystalTheme INSTANCE = new NullCrystalTheme();

   private NullCrystalTheme() {
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ModConfigs.VAULT_CRYSTAL.getRandomTheme(VaultMod.id("default"), vault.get(Vault.LEVEL).get(), random).ifPresent(id -> {
         ValueCrystalTheme child = new ValueCrystalTheme(id);
         child.configure(vault, random);
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Theme: ???").withStyle(ChatFormatting.GRAY));
   }

   @Override
   public Optional<Integer> getColor() {
      return Optional.empty();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.empty();
   }

   public void readNbt(CompoundTag compound) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.empty();
   }

   public void readJson(JsonObject object) {
   }
}
