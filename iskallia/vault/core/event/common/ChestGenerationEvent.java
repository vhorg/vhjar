package iskallia.vault.core.event.common;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.util.VaultRarity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ChestGenerationEvent extends Event<ChestGenerationEvent, ChestGenerationEvent.Data> {
   public ChestGenerationEvent() {
   }

   protected ChestGenerationEvent(ChestGenerationEvent parent) {
      super(parent);
   }

   public ChestGenerationEvent createChild() {
      return new ChestGenerationEvent(this);
   }

   public ChestGenerationEvent.Data invoke(
      ServerPlayer player,
      BlockState state,
      BlockPos pos,
      ResourceLocation lootTable,
      VaultChestTileEntity tileEntity,
      List<ItemStack> loot,
      VaultRarity rarity,
      Version version,
      RandomSource random,
      ChestGenerationEvent.Phase phase
   ) {
      return this.invoke(new ChestGenerationEvent.Data(player, state, pos, lootTable, tileEntity, loot, rarity, version, random, phase));
   }

   public ChestGenerationEvent pre() {
      return this.filter(data -> data.phase == ChestGenerationEvent.Phase.PRE);
   }

   public ChestGenerationEvent post() {
      return this.filter(data -> data.phase == ChestGenerationEvent.Phase.POST);
   }

   public static class Data {
      private final ServerPlayer player;
      private final BlockState state;
      private final BlockPos pos;
      private ResourceLocation lootTable;
      private final List<ItemStack> loot;
      private final VaultRarity rarity;
      private Version version;
      private RandomSource random;
      private final ChestGenerationEvent.Phase phase;
      private VaultChestTileEntity tileEntity;

      public Data(
         ServerPlayer player,
         BlockState state,
         BlockPos pos,
         ResourceLocation lootTable,
         VaultChestTileEntity tileEntity,
         List<ItemStack> loot,
         VaultRarity rarity,
         Version version,
         RandomSource random,
         ChestGenerationEvent.Phase phase
      ) {
         this.player = player;
         this.state = state;
         this.pos = pos;
         this.lootTable = lootTable;
         this.loot = loot;
         this.rarity = rarity;
         this.version = version;
         this.random = random;
         this.phase = phase;
         this.tileEntity = tileEntity;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }

      public BlockState getState() {
         return this.state;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public ResourceLocation getLootTable() {
         return this.lootTable;
      }

      public List<ItemStack> getLoot() {
         return this.loot;
      }

      @Nullable
      public VaultRarity getRarity() {
         return this.rarity;
      }

      public Version getVersion() {
         return this.version;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public ChestGenerationEvent.Phase getPhase() {
         return this.phase;
      }

      public VaultChestTileEntity getTileEntity() {
         return this.tileEntity;
      }

      public void setLootTable(ResourceLocation lootTable) {
         this.lootTable = lootTable;
      }

      public void setVersion(Version version) {
         this.version = version;
      }

      public void setRandom(RandomSource random) {
         this.random = random;
      }
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
