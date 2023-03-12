package iskallia.vault.item.crystal.time;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public class NullCrystalTime extends CrystalTime {
   public static final NullCrystalTime INSTANCE = new NullCrystalTime();

   private NullCrystalTime() {
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ModConfigs.VAULT_CRYSTAL.getRandomTime(VaultMod.id("default"), vault.get(Vault.LEVEL).get(), random).ifPresent(time -> time.configure(vault, random));
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
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
