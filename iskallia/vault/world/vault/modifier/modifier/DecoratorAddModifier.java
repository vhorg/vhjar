package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.EmptyTemplate;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratorAddModifier extends VaultModifier<DecoratorAddModifier.Properties> {
   private static final int DEFAULT_GENERATION_ATTEMPTS = 48;

   public DecoratorAddModifier(ResourceLocation id, DecoratorAddModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.attemptsPerChunk * s, p.attemptsPerChunk * s > 1 ? "s" : ""));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.TEMPLATE_GENERATION
         .at(TemplateGenerationEvent.Phase.POST)
         .register(
            context.getUUID(),
            data -> {
               if (data.getWorld() == world || data.getWorld() instanceof WorldGenRegion genRegion && genRegion.getLevel() == world) {
                  if (!(data.getTemplate().getParent() instanceof EmptyTemplate)) {
                     ProcessorContext processorContext = new ProcessorContext(vault, data.getRandom());

                     for (int i = 0; i < this.properties.attemptsPerChunk; i++) {
                        int x = data.getRandom().nextInt(16) + data.getChunkPos().x * 16;
                        int z = data.getRandom().nextInt(16) + data.getChunkPos().z * 16;
                        int y = data.getRandom().nextInt(64);
                        BlockPos pos = new BlockPos(x, y, z);
                        ServerLevelAccessor serverLevelAccessor = data.getWorld();
                        BlockState state = serverLevelAccessor.getBlockState(pos);
                        if (state.getBlock() == Blocks.AIR
                           && (
                              !this.properties.requireConditions
                                 || serverLevelAccessor.getBlockState(pos.above()).isAir()
                                    && serverLevelAccessor.getBlockState(pos.below()).isFaceSturdy(serverLevelAccessor, pos, Direction.UP)
                           )) {
                           PartialTile tile = this.properties.output.copy().setPos(pos);
                           PlacementSettings settings = data.getTemplate().getSettings().copy();
                           if (data.getTemplate().getParent() instanceof JigsawTemplate jigsaw) {
                              jigsaw.getConfigurator().accept(settings);
                           }

                           for (TileProcessor processor : settings.getTileProcessors()) {
                              tile = processor.process(tile, processorContext);
                              if (tile == null) {
                                 break;
                              }
                           }

                           if (tile != null) {
                              serverLevelAccessor.setBlock(pos, tile.getState().asBlockState(), 3);
                              if (tile.getNbt() != null) {
                                 BlockEntity blockEntity = serverLevelAccessor.getBlockEntity(pos);
                                 if (blockEntity != null) {
                                    blockEntity.load(tile.getNbt());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   public static class Properties {
      @Expose
      private final PartialTile output;
      @Expose
      private final int attemptsPerChunk;
      @Expose
      private final boolean requireConditions;

      public Properties(PartialTile output) {
         this(output, 48, true);
      }

      public Properties(PartialTile output, int attemptsPerChunk, boolean requireConditions) {
         this.output = output;
         this.attemptsPerChunk = attemptsPerChunk;
         this.requireConditions = requireConditions;
      }

      public PartialTile getOutput() {
         return this.output;
      }

      public int getAttemptsPerChunk() {
         return this.attemptsPerChunk;
      }

      public boolean requireConditions() {
         return this.requireConditions;
      }
   }
}
