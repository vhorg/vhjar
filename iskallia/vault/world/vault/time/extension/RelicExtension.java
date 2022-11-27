package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModRelics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class RelicExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("relic_set");
   protected ModRelics.RelicRecipe relicRecipe;

   public RelicExtension() {
   }

   public RelicExtension(ModRelics.RelicRecipe relicRecipe, long extraTime) {
      this(ID, relicRecipe, extraTime);
   }

   public RelicExtension(ResourceLocation id, ModRelics.RelicRecipe relicRecipe, long extraTime) {
      super(id, extraTime);
      this.relicRecipe = relicRecipe;
   }

   public ModRelics.RelicRecipe getRelicRecipe() {
      return this.relicRecipe;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      if (this.relicRecipe != null) {
         nbt.putString("RelicSet", this.getRelicRecipe().getResultingRelic().toString());
      }

      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.relicRecipe = ModRelics.RECIPE_REGISTRY.get(new ResourceLocation(nbt.getString("RelicSet")));
      if (this.relicRecipe == null) {
         VaultMod.LOGGER.error("Relic set <" + nbt.getString("RelicSet") + "> is not defined.");
      }
   }
}
