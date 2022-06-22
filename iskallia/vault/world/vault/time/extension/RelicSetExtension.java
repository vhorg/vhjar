package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import iskallia.vault.util.RelicSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RelicSetExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("relic_set");
   protected RelicSet relicSet;

   public RelicSetExtension() {
   }

   public RelicSetExtension(RelicSet relicSet, long extraTime) {
      this(ID, relicSet, extraTime);
   }

   public RelicSetExtension(ResourceLocation id, RelicSet relicSet, long extraTime) {
      super(id, extraTime);
      this.relicSet = relicSet;
   }

   public RelicSet getRelicSet() {
      return this.relicSet;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      if (this.relicSet != null) {
         nbt.func_74778_a("RelicSet", this.getRelicSet().getId().toString());
      }

      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.relicSet = RelicSet.REGISTRY.get(new ResourceLocation(nbt.func_74779_i("RelicSet")));
      if (this.relicSet == null) {
         Vault.LOGGER.error("Relic set <" + nbt.func_74779_i("RelicSet") + "> is not defined.");
      }
   }
}
