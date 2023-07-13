package iskallia.vault.block.entity;

import iskallia.vault.container.VaultEnhancementAltarContainer;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.enhancement.EnhancementData;
import iskallia.vault.core.vault.enhancement.EnhancementTask;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultEnhancementAltarTileEntity extends BlockEntity implements MenuProvider {
   private final SimpleContainer inventory = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         VaultEnhancementAltarTileEntity.this.setChanged();
      }
   };
   private UUID uuid = UUID.randomUUID();
   private EnhancementTask.Config<?> config = null;
   private Map<UUID, EnhancementTask<?>> tasks = new HashMap<>();
   private List<UUID> usedPlayers = new ArrayList<>();

   public VaultEnhancementAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ENHANCEMENT_ALTAR_TILE_ENTITY, pos, state);
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public EnhancementTask.Config<?> getConfig() {
      return this.config;
   }

   public Map<UUID, EnhancementTask<?>> getTasks() {
      return this.tasks;
   }

   public static void tick(Level level, BlockPos pos, BlockState blockState, VaultEnhancementAltarTileEntity tile) {
      if (level.isClientSide()) {
         spawnParticles(pos);
      } else {
         if (level.getGameTime() % 10L == 0L) {
            tile.tasks = EnhancementData.getForAltar(tile.uuid);
            tile.level.sendBlockUpdated(tile.worldPosition, tile.getBlockState(), tile.getBlockState(), 3);
            tile.level.updateNeighborsAt(tile.worldPosition, tile.getBlockState().getBlock());
            tile.setChanged();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void spawnParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random rand = level.random;
         if (rand.nextInt(3) == 0) {
            level.addParticle(
               ParticleTypes.ENCHANT,
               pos.getX() + 0.5,
               pos.getY() + 2,
               pos.getZ() + 0.5,
               -4.0F + rand.nextFloat() * 8.0F + 0.5,
               rand.nextFloat() * 2.0F - 2.5,
               -4.0F + rand.nextFloat() * 8.0F + 0.5
            );
         }
      }
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public List<UUID> getUsedPlayers() {
      return this.usedPlayers;
   }

   public void setUsedByPlayer(Player player) {
      this.usedPlayers.add(player.getUUID());
      this.setChanged();
   }

   public boolean canBeUsed(Player player) {
      ItemStack input = this.getInventory().getItem(0);
      if (input.isEmpty() || !input.is(ModItems.HELMET)) {
         return false;
      } else {
         return AttributeGearData.hasData(input) && AttributeGearData.<AttributeGearData>read(input).isModifiable()
            ? !this.getUsedPlayers().contains(player.getUUID())
            : false;
      }
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this;
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      NBTHelper.deserializeSimpleContainer(this.inventory, nbt.getList("inventory", 10));
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElse(UUID.randomUUID());
      this.config = Adapters.ENHANCEMENT_CONFIG.readNbt(nbt.getCompound("config")).orElse(null);
      this.tasks = NBTHelper.<UUID, EnhancementTask<?>>readMap(
            nbt, "tasks", UUID::fromString, (key, tag) -> tag instanceof CompoundTag compound ? Adapters.ENHANCEMENT_TASK.readNbt(compound) : Optional.empty()
         )
         .orElse(new HashMap<>());
      this.usedPlayers = NBTHelper.readList(nbt, "players", StringTag.class, strTag -> UUID.fromString(strTag.getAsString()));
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
      Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
      Adapters.ENHANCEMENT_CONFIG.writeNbt(this.config).ifPresent(tag -> nbt.put("config", tag));
      NBTHelper.writeMap(nbt, "tasks", this.tasks, UUID::toString, t -> Adapters.ENHANCEMENT_TASK.writeNbt(t).orElseThrow());
      NBTHelper.writeCollection(nbt, "players", this.usedPlayers, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultEnhancementAltarContainer(id, this.getLevel(), this.getBlockPos(), inv);
   }
}
