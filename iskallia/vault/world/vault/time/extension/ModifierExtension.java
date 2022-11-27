package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.modifier.modifier.VaultTimeModifier;
import net.minecraft.resources.ResourceLocation;

public class ModifierExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("modifier");

   public ModifierExtension() {
   }

   public ModifierExtension(int addedTime) {
      super(ID, addedTime);
   }

   public ModifierExtension(VaultTimeModifier modifier) {
      this(modifier.properties().getTimeAddedTicks());
   }

   public ModifierExtension(ResourceLocation id, VaultTimeModifier modifier) {
      super(id, modifier.properties().getTimeAddedTicks());
   }
}
