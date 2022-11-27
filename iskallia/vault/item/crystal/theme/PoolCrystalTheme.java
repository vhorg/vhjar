package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public class PoolCrystalTheme extends CrystalTheme {
   private ResourceLocation id;

   protected PoolCrystalTheme() {
   }

   public PoolCrystalTheme(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      Optional<ResourceLocation> theme = ModConfigs.VAULT_CRYSTAL.getRandomTheme(this.id, vault.get(Vault.LEVEL).get(), random);
      theme.ifPresent(id -> {
         ValueCrystalTheme child = new ValueCrystalTheme(id);
         child.configure(vault, random);
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "pool");
      nbt.putString("id", this.id.toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("id"));
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "pool");
      object.addProperty("id", this.id.toString());
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.id = new ResourceLocation(json.get("id").getAsString());
   }
}
