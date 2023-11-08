package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemScavengeTaskEvent extends Event<ItemScavengeTaskEvent, ItemScavengeTaskEvent.Data> {
   public ItemScavengeTaskEvent() {
   }

   protected ItemScavengeTaskEvent(ItemScavengeTaskEvent parent) {
      super(parent);
   }

   public ItemScavengeTaskEvent createChild() {
      return new ItemScavengeTaskEvent(this);
   }

   public ItemScavengeTaskEvent.Data invoke(Vault vault, Level world, BlockPos pos, List<ItemStack> items) {
      return this.invoke(new ItemScavengeTaskEvent.Data(vault, world, pos, items));
   }

   public static class Data {
      private final Level world;
      private final Vault vault;
      private final BlockPos pos;
      private final List<ItemStack> items;

      public Data(Vault vault, Level world, BlockPos pos, List<ItemStack> items) {
         this.world = world;
         this.vault = vault;
         this.pos = pos;
         this.items = items;
      }

      public Level getWorld() {
         return this.world;
      }

      public Vault getVault() {
         return this.vault;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public List<ItemStack> getItems() {
         return this.items;
      }
   }
}
