package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DungeonDoorTileEntity extends TreasureDoorTileEntity {
   private DungeonDoorTileEntity.Difficulty difficulty;

   public DungeonDoorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.DUNGEON_DOOR_TILE_ENTITY, pos, state);
   }

   public DungeonDoorTileEntity.Difficulty getDifficulty() {
      return this.difficulty;
   }

   @Override
   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("Difficulty", 10)) {
         this.difficulty = DungeonDoorTileEntity.Difficulty.fromNBT(nbt.getCompound("Difficulty"));
      }
   }

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.difficulty != null) {
         nbt.put("Difficulty", this.difficulty.toNBT());
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, DungeonDoorTileEntity tile) {
      TreasureDoorTileEntity.tick(level, pos, state, tile);
   }

   public static class Difficulty {
      public String name;
      public int color;

      public Difficulty(String name, int color) {
         this.name = name;
         this.color = color;
      }

      public String getName() {
         return this.name;
      }

      public int getColor() {
         return this.color;
      }

      public CompoundTag toNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("Name", this.name);
         nbt.putInt("Color", this.color);
         return nbt;
      }

      public static DungeonDoorTileEntity.Difficulty fromNBT(CompoundTag nbt) {
         return new DungeonDoorTileEntity.Difficulty(nbt.getString("Name"), nbt.getInt("Color"));
      }

      public Component getDisplay() {
         return new TextComponent("Difficulty: %s".formatted(this.getName())).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.getColor())));
      }
   }
}
