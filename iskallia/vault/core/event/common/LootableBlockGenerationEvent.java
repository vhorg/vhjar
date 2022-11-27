package iskallia.vault.core.event.common;

import iskallia.vault.core.Version;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LootableBlockGenerationEvent extends Event<LootableBlockGenerationEvent, LootableBlockGenerationEvent.Data> {
   public LootableBlockGenerationEvent() {
   }

   protected LootableBlockGenerationEvent(LootableBlockGenerationEvent parent) {
      super(parent);
   }

   public LootableBlockGenerationEvent createChild() {
      return new LootableBlockGenerationEvent(this);
   }

   public LootableBlockGenerationEvent.Data invoke(
      ServerPlayer player,
      BlockState state,
      BlockPos pos,
      ResourceLocation lootTable,
      BlockEntity tileEntity,
      List<ItemStack> loot,
      Version version,
      RandomSource random,
      LootableBlockGenerationEvent.Phase phase
   ) {
      return this.invoke(new LootableBlockGenerationEvent.Data(player, state, pos, lootTable, tileEntity, loot, version, random, phase));
   }

   public LootableBlockGenerationEvent pre() {
      return this.filter(data -> data.phase == LootableBlockGenerationEvent.Phase.PRE);
   }

   public LootableBlockGenerationEvent post() {
      return this.filter(data -> data.phase == LootableBlockGenerationEvent.Phase.POST);
   }

   public static class Data {
      private final ServerPlayer player;
      private final BlockState state;
      private final BlockPos pos;
      private ResourceLocation lootTable;
      private final List<ItemStack> loot;
      private Version version;
      private RandomSource random;
      private final LootableBlockGenerationEvent.Phase phase;
      private final BlockEntity tileEntity;

      public Data(
         ServerPlayer player,
         BlockState state,
         BlockPos pos,
         ResourceLocation lootTable,
         BlockEntity tileEntity,
         List<ItemStack> loot,
         Version version,
         RandomSource random,
         LootableBlockGenerationEvent.Phase phase
      ) {
         this.player = player;
         this.state = state;
         this.pos = pos;
         this.lootTable = lootTable;
         this.loot = loot;
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

      public Version getVersion() {
         return this.version;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public LootableBlockGenerationEvent.Phase getPhase() {
         return this.phase;
      }

      public BlockEntity getTileEntity() {
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
