package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.Version;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.resources.ResourceLocation;

public class ReferenceEntityProcessor extends EntityProcessor {
   private final ResourceLocation id;

   public ReferenceEntityProcessor(ResourceLocation id) {
      this.id = id;
   }

   public PartialEntity process(PartialEntity value, ProcessorContext context) {
      Version version = context.vault == null ? Version.latest() : context.vault.get(Vault.VERSION);
      Palette palette = VaultRegistry.PALETTE.getKey(this.id).get(version);

      for (EntityProcessor child : palette.getEntityProcessors()) {
         value = child.process(value, context);
         if (value == null) {
            break;
         }
      }

      return value;
   }
}
