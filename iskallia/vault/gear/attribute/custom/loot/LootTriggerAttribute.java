package iskallia.vault.gear.attribute.custom.loot;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.init.ModConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class LootTriggerAttribute {
   private final ResourceLocation tileGroupId;
   private final String displayName;

   protected LootTriggerAttribute(ResourceLocation tileGroupId, String displayName) {
      this.tileGroupId = tileGroupId;
      this.displayName = displayName;
   }

   public ResourceLocation getTileGroupId() {
      return this.tileGroupId;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public boolean shouldTrigger(BlockEntity tile) {
      return ModConfigs.TILE_GROUPS.isInGroup(this.getTileGroupId(), PartialTile.of(tile));
   }

   public abstract void trigger(BlockEntity var1, RandomSource var2, ServerPlayer var3);

   public abstract static class Config {
      @Expose
      private final ResourceLocation tileEntityGroupId;
      @Expose
      private final String displayName;

      public Config(ResourceLocation tileEntityGroupId, String displayName) {
         this.tileEntityGroupId = tileEntityGroupId;
         this.displayName = displayName;
      }

      public ResourceLocation getTileEntityGroupId() {
         return this.tileEntityGroupId;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
