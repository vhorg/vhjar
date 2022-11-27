package iskallia.vault.block.entity;

import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.template.DynamicTemplate;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class TreasureDoorTileEntity extends BlockEntity {
   private TreasureDoorTileEntity.Step step = TreasureDoorTileEntity.Step.PLACED;
   private int tunnelSize;
   private ResourceLocation pool;
   private ResourceLocation target;
   private final List<ResourceLocation> palettes = new ArrayList<>();

   public TreasureDoorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.TREASURE_DOOR_TILE_ENTITY, pos, state);
   }

   public TreasureDoorTileEntity.Step getStep() {
      return this.step;
   }

   public int getTunnelSize() {
      return this.tunnelSize;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public void setStep(TreasureDoorTileEntity.Step step) {
      this.step = step;
      this.setChanged();
   }

   public void setTunnelSize(int tunnelSize) {
      this.tunnelSize = tunnelSize;
      this.setChanged();
   }

   public void setPool(ResourceLocation pool) {
      this.pool = pool;
      this.setChanged();
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("Step", 3)) {
         this.step = TreasureDoorTileEntity.Step.values()[nbt.getInt("Step")];
      }

      this.tunnelSize = nbt.getInt("TunnelSize");
      if (nbt.contains("Pool", 8)) {
         this.pool = new ResourceLocation(nbt.getString("Pool"));
      }

      if (nbt.contains("Target", 8)) {
         this.target = new ResourceLocation(nbt.getString("Target"));
      }

      this.palettes.clear();
      if (nbt.contains("Palettes", 9)) {
         ListTag list = nbt.getList("Palettes", 8);

         for (int i = 0; i < list.size(); i++) {
            this.palettes.add(new ResourceLocation(list.getString(i)));
         }
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putInt("Step", this.step.ordinal());
      nbt.putInt("TunnelSize", this.tunnelSize);
      if (this.pool != null) {
         nbt.putString("Pool", this.pool.toString());
      }

      if (this.target != null) {
         nbt.putString("Target", this.target.toString());
      }

      ListTag list = new ListTag();
      this.palettes.forEach(location -> list.add(StringTag.valueOf(location.toString())));
      nbt.put("Palettes", list);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, TreasureDoorTileEntity tile) {
      if (level instanceof ServerLevel world) {
         if (ServerVaults.isVaultWorld(level)) {
            if (tile.getStep() == TreasureDoorTileEntity.Step.PLACED) {
               fillMissingDoor(world, pos, state);
               if (state.getValue(TreasureDoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                  fillTunnel(world, pos, state, ModBlocks.VAULT_BEDROCK.defaultBlockState(), tile);
               }
            } else if (tile.getStep() == TreasureDoorTileEntity.Step.FILLED
               && (Boolean)state.getValue(TreasureDoorBlock.OPEN)
               && state.getValue(TreasureDoorBlock.HALF) == DoubleBlockHalf.LOWER) {
               carveTunnel(world, pos, state, Blocks.AIR.defaultBlockState(), tile);
               generateRoom(world, pos, state, tile);
            }
         }
      }
   }

   public static void fillMissingDoor(ServerLevel world, BlockPos pos, BlockState state) {
      BlockState current = world.getBlockState(pos);
      BlockPos otherPos = pos.above(current.getValue(TreasureDoorBlock.HALF) == DoubleBlockHalf.LOWER ? 1 : -1);
      BlockState other = world.getBlockState(otherPos);
      if (other.getBlock() != ModBlocks.TREASURE_DOOR) {
         other = (BlockState)current.setValue(
            TreasureDoorBlock.HALF, current.getValue(TreasureDoorBlock.HALF) == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER
         );
         world.setBlock(otherPos, other, 3);
      }
   }

   private static void fillTunnel(ServerLevel world, BlockPos pos, BlockState door, BlockState filler, TreasureDoorTileEntity tile) {
      int offset;
      for (offset = 1; offset <= 48; offset++) {
         BlockPos pos1 = pos.relative(((Direction)door.getValue(TreasureDoorBlock.FACING)).getOpposite(), offset);
         Block block1 = world.getBlockState(pos1).getBlock();
         Block block2 = world.getBlockState(pos1.above()).getBlock();
         if (block1.defaultDestroyTime() < 0.0F && block2.defaultDestroyTime() < 0.0F) {
            break;
         }

         world.setBlock(pos1.below(), filler, 3);
         world.setBlock(pos1, filler, 3);
         world.setBlock(pos1.above(), filler, 3);
         world.setBlock(pos1.above(2), filler, 3);
         Direction side = ((Direction)door.getValue(TreasureDoorBlock.FACING)).getClockWise();
         world.setBlock(pos1.relative(side), filler, 3);
         world.setBlock(pos1.relative(side).above(), filler, 3);
         world.setBlock(pos1.relative(side.getOpposite()), filler, 3);
         world.setBlock(pos1.relative(side.getOpposite()).above(), filler, 3);
      }

      tile.setTunnelSize(offset - 1);
      tile.setStep(TreasureDoorTileEntity.Step.FILLED);
   }

   private static void carveTunnel(ServerLevel world, BlockPos pos, BlockState door, BlockState carver, TreasureDoorTileEntity tile) {
      for (int offset = 0; offset < tile.tunnelSize; offset++) {
         BlockPos pos1 = pos.relative(((Direction)door.getValue(TreasureDoorBlock.FACING)).getOpposite(), offset + 1);
         world.setBlock(pos1, carver, 3);
         world.setBlock(pos1.above(), carver, 3);
      }
   }

   private static void generateRoom(ServerLevel world, BlockPos pos, BlockState state, TreasureDoorTileEntity tile) {
      if (tile.getPool() != null) {
         TemplatePoolKey key = VaultRegistry.TEMPLATE_POOL.getKey(tile.getPool());
         if (key != null) {
            Version version = Version.latest();
            ChunkRandom random = ChunkRandom.any();
            Vault vault = ServerVaults.get(world).orElse(null);
            if (vault != null) {
               version = vault.get(Vault.VERSION);
               random.setDecoratorSeed(vault.get(Vault.SEED), pos.getX(), pos.getZ(), 329057345);
            }

            PlacementSettings settings = new PlacementSettings().setFlags(3);
            settings.getProcessorContext().random = random;
            settings.getProcessorContext().vault = vault;
            CompoundTag nbt = new CompoundTag();
            nbt.putString("target", tile.target.toString());
            nbt.putString("pool", tile.pool.toString());
            nbt.putString("final_state", "minecraft:air");
            PartialTile jigsaw = PartialTile.of(
                  (BlockState)Blocks.JIGSAW
                     .defaultBlockState()
                     .setValue(
                        JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(((Direction)state.getValue(TreasureDoorBlock.FACING)).getOpposite(), Direction.UP)
                     ),
                  nbt
               )
               .setPos(pos.relative(((Direction)state.getValue(TreasureDoorBlock.FACING)).getOpposite(), tile.getTunnelSize()));
            DynamicTemplate root = new DynamicTemplate();
            root.add(jigsaw);
            JigsawTemplate template = JigsawTemplate.of(
               version, root, tile.palettes.stream().map(location -> VaultRegistry.PALETTE.getKey(location)).filter(Objects::nonNull).toList(), 10, random
            );
            template.place(world, settings);
            tile.setStep(TreasureDoorTileEntity.Step.GENERATED);
         }
      }
   }

   private static enum Step {
      PLACED,
      FILLED,
      GENERATED;
   }
}