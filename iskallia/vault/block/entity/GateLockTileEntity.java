package iskallia.vault.block.entity;

import iskallia.vault.block.GateLockBlock;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.template.data.IndirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GateLockTileEntity extends BlockEntity {
   private GateLockTileEntity.Step step = GateLockTileEntity.Step.PLACED;
   private int offsetX;
   private int offsetY;
   private int width;
   private int height;
   private TemplatePool room;
   private TemplatePool tunnel;
   private String fullName;
   private String tunnelName;
   private int color;
   private VaultGod god;
   private List<ItemLootEntry> fullCost = new ArrayList<>();
   private List<ItemLootEntry> tunnelCost = new ArrayList<>();
   private GateLockTileEntity.State state;
   private List<OverSizedItemStack> cost = new ArrayList<>();
   private int reputationCost;
   private ResourceLocation modifierPool;
   private List<VaultModifierStack> modifiers = new ArrayList<>();

   public GateLockTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public GateLockTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.GATE_LOCK_TILE_ENTITY, pos, state);
   }

   public GateLockTileEntity.Step getStep() {
      return this.step;
   }

   public VaultGod getGod() {
      return this.god;
   }

   public TemplatePool getRoom() {
      return this.room;
   }

   public TemplatePool getTunnel() {
      return this.tunnel;
   }

   public String getName() {
      return this.state == GateLockTileEntity.State.TUNNEL_AND_ROOM ? this.fullName : this.tunnelName;
   }

   public int getColor() {
      return this.color;
   }

   public GateLockTileEntity.State getState() {
      return this.state;
   }

   public List<ItemStack> getCost() {
      return this.cost.stream().map(OverSizedItemStack::overSizedStack).toList();
   }

   public List<OverSizedItemStack> getOversizedCost() {
      return this.cost;
   }

   public int getReputationCost() {
      return this.reputationCost;
   }

   public List<VaultModifierStack> getModifiers() {
      return this.modifiers;
   }

   public void setStep(GateLockTileEntity.Step step) {
      this.step = step;
      this.sendUpdates();
   }

   public void setState(GateLockTileEntity.State state) {
      this.state = state;
      this.sendUpdates();
   }

   public void setReputationCost(int reputationCost) {
      this.reputationCost = reputationCost;
      this.sendUpdates();
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.step = Adapters.ofEnum(GateLockTileEntity.Step.class, EnumAdapter.Mode.NAME).readNbt(nbt.get("Step")).orElse(GateLockTileEntity.Step.PLACED);
      this.offsetX = Adapters.INT.readNbt(nbt.get("OffsetX")).orElse(0);
      this.offsetY = Adapters.INT.readNbt(nbt.get("OffsetY")).orElse(0);
      this.width = Adapters.INT.readNbt(nbt.get("Width")).orElse(0);
      this.height = Adapters.INT.readNbt(nbt.get("Height")).orElse(0);
      this.god = Adapters.GOD_NAME.readNbt(nbt.get("God")).orElse(null);
      if (nbt.contains("RoomReference", 8)) {
         ResourceLocation reference = Adapters.IDENTIFIER.readNbt(nbt.get("RoomReference")).orElseThrow();
         List<ResourceLocation> palettes = new ArrayList<>();
         if (nbt.contains("RoomPalettes", 9)) {
            for (Tag tag : nbt.getList("RoomPalettes", 8)) {
               Adapters.IDENTIFIER.readNbt(tag).ifPresent(palettes::add);
            }
         }

         this.room = new TemplatePool().addLeaf(new IndirectTemplateEntry(reference, palettes), 1.0);
      } else {
         this.room = Adapters.TEMPLATE_POOL.readNbt((ListTag)nbt.get("Room")).orElse(null);
      }

      if (nbt.contains("TunnelReference", 8)) {
         ResourceLocation reference = Adapters.IDENTIFIER.readNbt(nbt.get("TunnelReference")).orElseThrow();
         List<ResourceLocation> palettes = new ArrayList<>();
         if (nbt.contains("TunnelPalettes", 9)) {
            for (Tag tag : nbt.getList("TunnelPalettes", 8)) {
               Adapters.IDENTIFIER.readNbt(tag).ifPresent(palettes::add);
            }
         }

         this.tunnel = new TemplatePool().addLeaf(new IndirectTemplateEntry(reference, palettes), 1.0);
      } else {
         this.tunnel = Adapters.TEMPLATE_POOL.readNbt((ListTag)nbt.get("Tunnel")).orElse(null);
      }

      this.fullName = Adapters.UTF_8.readNbt(nbt.get("FullName")).orElse("Unknown");
      this.tunnelName = Adapters.UTF_8.readNbt(nbt.get("TunnelName")).orElse("Unknown");
      this.color = Adapters.INT.readNbt(nbt.get("Color")).orElse(16777215);
      this.fullCost = new ArrayList<>();
      nbt.getList("FullCost", 10).forEach(tagx -> this.fullCost.add(new ItemLootEntry(tagx)));
      this.tunnelCost = new ArrayList<>();
      nbt.getList("TunnelCost", 10).forEach(tagx -> this.tunnelCost.add(new ItemLootEntry(tagx)));
      this.state = Adapters.ofEnum(GateLockTileEntity.State.class, EnumAdapter.Mode.NAME).readNbt(nbt.get("State")).orElse(null);
      ListTag list = nbt.getList("Cost", 10);
      this.cost = new ArrayList<>();

      for (Tag tag : list) {
         Adapters.ITEM_STACK.readNbt(tag).ifPresent(stack -> this.cost.add(OverSizedItemStack.of(stack)));
      }

      this.reputationCost = Adapters.INT.readNbt(nbt.get("ReputationCost")).orElse(0);
      this.modifierPool = Adapters.IDENTIFIER.readNbt(nbt.get("ModifierPool")).orElse(null);
      this.modifiers = new ArrayList<>();

      for (Tag tag : nbt.getList("Modifiers", 10)) {
         this.modifiers.add(VaultModifierStack.of((CompoundTag)tag));
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.ofEnum(GateLockTileEntity.Step.class, EnumAdapter.Mode.NAME).writeNbt(this.step).ifPresent(tag -> nbt.put("Step", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.offsetX)).ifPresent(tag -> nbt.put("OffsetX", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.offsetY)).ifPresent(tag -> nbt.put("OffsetY", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.width)).ifPresent(tag -> nbt.put("Width", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.height)).ifPresent(tag -> nbt.put("Height", tag));
      Adapters.GOD_NAME.writeNbt(this.god).ifPresent(tag -> nbt.put("God", tag));
      Adapters.TEMPLATE_POOL.writeNbt(this.room).ifPresent(tag -> nbt.put("Room", tag));
      Adapters.TEMPLATE_POOL.writeNbt(this.tunnel).ifPresent(tag -> nbt.put("Tunnel", tag));
      Adapters.UTF_8.writeNbt(this.fullName).ifPresent(tag -> nbt.put("FullName", tag));
      Adapters.UTF_8.writeNbt(this.tunnelName).ifPresent(tag -> nbt.put("TunnelName", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> nbt.put("Color", tag));
      ListTag list1 = new ListTag();

      for (ItemLootEntry entry : this.fullCost) {
         entry.writeNbt().ifPresent(list1::add);
      }

      nbt.put("FullCost", list1);
      ListTag list2 = new ListTag();

      for (ItemLootEntry entry : this.tunnelCost) {
         entry.writeNbt().ifPresent(list2::add);
      }

      nbt.put("TunnelCost", list2);
      Adapters.ofEnum(GateLockTileEntity.State.class, EnumAdapter.Mode.NAME).writeNbt(this.state).ifPresent(tag -> nbt.put("State", tag));
      ListTag list = new ListTag();

      for (OverSizedItemStack stack : this.cost) {
         Adapters.ITEM_STACK.writeNbt(stack.overSizedStack()).ifPresent(list::add);
      }

      nbt.put("Cost", list);
      Adapters.INT.writeNbt(Integer.valueOf(this.reputationCost)).ifPresent(tag -> nbt.put("ReputationCost", tag));
      Adapters.IDENTIFIER.writeNbt(this.modifierPool).ifPresent(tag -> nbt.put("ModifierPool", tag));
      ListTag modifierList = new ListTag();

      for (VaultModifierStack modifier : this.modifiers) {
         modifierList.add(modifier.serializeNBT());
      }

      nbt.put("Modifiers", modifierList);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, GateLockTileEntity tile) {
      if (level instanceof ServerLevel world) {
         if (tile.getStep() == GateLockTileEntity.Step.PLACED) {
            fillGate(world, pos, state, tile);
            tile.setStep(GateLockTileEntity.Step.GENERATED);
         } else if (tile.getStep() == GateLockTileEntity.Step.REMOVED) {
            tile.iterate(pos, (Direction)state.getValue(GateLockBlock.FACING), p -> world.setBlock(p, Blocks.AIR.defaultBlockState(), 3));
         }

         if (level.getGameTime() % 20L == 0L) {
            CommonEvents.GATE_LOCK_UPDATE.invoke(world, state, pos, tile);
         }
      }
   }

   public void iterate(BlockPos pos, Direction facing, Consumer<BlockPos> consumer) {
      Direction planeX = switch (facing) {
         case NORTH, SOUTH -> Direction.EAST;
         default -> Direction.SOUTH;
      };

      Direction planeY = switch (facing) {
         case UP, DOWN -> Direction.EAST;
         default -> Direction.UP;
      };
      BlockPos min = pos.relative(planeX, this.offsetX).relative(planeY, this.offsetY);
      BlockPos max = min.relative(planeX, this.width - 1).relative(planeY, this.height - 1);

      for (int x = min.getX(); x <= max.getX(); x++) {
         for (int z = min.getZ(); z <= max.getZ(); z++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
               consumer.accept(new BlockPos(x, y, z));
            }
         }
      }
   }

   public static void fillGate(ServerLevel world, BlockPos pos, BlockState state, GateLockTileEntity entity) {
      Direction direction = (Direction)state.getValue(GateLockBlock.FACING);
      entity.iterate(pos, direction, p -> {
         if (!pos.equals(p)) {
            world.setBlock(p, ModBlocks.POLISHED_VAULT_STONE.defaultBlockState(), 3);
         }
      });
   }

   public void onOpen(Level world, Player player, BlockPos pos, BlockState state) {
      CommonEvents.GATE_LOCK_UPDATE.invoke(world, state, pos, this);
      CommonEvents.GATE_LOCK_OPEN.invoke(world, player, pos, this);
   }

   public Direction getDirection() {
      return (Direction)this.getBlockState().getValue(GateLockBlock.FACING);
   }

   public void generateCostAndModifiers(ChunkRandom random) {
      List<ItemLootEntry> table = this.state == GateLockTileEntity.State.TUNNEL ? this.tunnelCost : this.fullCost;
      this.cost.clear();
      if (table != null) {
         for (ItemLootEntry entry : table) {
            this.cost.add(entry.getOverStack(random));
         }
      }

      this.modifiers.clear();
      if (this.modifierPool != null && this.state == GateLockTileEntity.State.TUNNEL_AND_ROOM) {
         Map<VaultModifier<?>, Integer> map = new HashMap<>();
         ModConfigs.VAULT_MODIFIER_POOLS
            .getRandom(this.modifierPool, 0, random)
            .forEach(modifier -> map.put((VaultModifier<?>)modifier, map.getOrDefault(modifier, 0) + 1));
         map.forEach((id, count) -> this.modifiers.add(VaultModifierStack.of((VaultModifier<?>)id, count)));
      }

      this.sendUpdates();
   }

   public static enum State {
      TUNNEL,
      TUNNEL_AND_ROOM;
   }

   public static enum Step {
      PLACED,
      GENERATED,
      REMOVED;
   }
}
