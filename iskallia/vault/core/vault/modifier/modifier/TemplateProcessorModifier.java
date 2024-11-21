package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class TemplateProcessorModifier extends VaultModifier<TemplateProcessorModifier.Properties> {
   public TemplateProcessorModifier(ResourceLocation id, TemplateProcessorModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getProbability() * s * 100.0F)));
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.TEMPLATE_GENERATION
         .at(TemplateGenerationEvent.Phase.PRE)
         .in(world)
         .register(this, data -> data.getTemplate().getSettings().addProcessor(TileProcessor.of((tile, ctx) -> {
            if (this.properties.whitelist != null && !this.properties.whitelist.test(tile)) {
               return tile;
            } else if (this.properties.blacklist != null && this.properties.blacklist.test(tile)) {
               return tile;
            } else if (ctx.getRandom(tile.getPos()).nextFloat() >= this.properties.probability) {
               return tile;
            } else {
               BlockState state = tile.getState().asWhole().orElse(null);
               if (state == null) {
                  return tile;
               } else {
                  List<TileProcessor> palette;
                  if (state.isCollisionShapeFullBlock(data.getWorld(), tile.getPos())) {
                     palette = this.properties.fullBlock;
                  } else {
                     palette = this.properties.partialBlock;
                  }

                  for (TileProcessor processor : palette) {
                     tile = processor.process(tile, ctx);
                  }

                  return tile;
               }
            }
         })));
   }

   public static class Properties {
      @Expose
      private final float probability;
      @Expose
      private final TilePredicate blacklist;
      @Expose
      private final TilePredicate whitelist;
      @Expose
      private final List<TileProcessor> fullBlock;
      @Expose
      private final List<TileProcessor> partialBlock;

      public Properties(float probability, TilePredicate blacklist, TilePredicate whitelist) {
         this.probability = probability;
         this.blacklist = blacklist;
         this.whitelist = whitelist;
         this.fullBlock = new ArrayList<>();
         this.partialBlock = new ArrayList<>();
      }

      public float getProbability() {
         return this.probability;
      }
   }
}
