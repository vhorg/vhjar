package iskallia.vault.block.entity;

import com.mojang.math.Vector3f;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItems;
import iskallia.vault.event.event.CraftCrystalEvent;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.network.message.ClientboundUpdateAltarIndexMessage;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerVaultAltarData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;

public class VaultAltarTileEntity extends BlockEntity {
   private UUID owner;
   private AltarInfusionRecipe recipe;
   private VaultAltarTileEntity.AltarState altarState;
   private HashMap<String, Integer> displayedIndex = new HashMap<>();
   private int infusionTimer = -666;

   public VaultAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_ALTAR_TILE_ENTITY, pos, state);
   }

   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public AltarInfusionRecipe getRecipe() {
      return this.recipe;
   }

   public void setRecipe(AltarInfusionRecipe recipe) {
      this.recipe = recipe;
   }

   public VaultAltarTileEntity.AltarState getAltarState() {
      return this.altarState;
   }

   public void setAltarState(VaultAltarTileEntity.AltarState state) {
      this.altarState = state;
   }

   public int getInfusionTimer() {
      return this.infusionTimer;
   }

   public HashMap<String, Integer> getDisplayedIndex() {
      return this.displayedIndex;
   }

   public void setDisplayedIndex(HashMap<String, Integer> displayedIndex) {
      this.displayedIndex = displayedIndex;
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, VaultAltarTileEntity altar) {
      if (level instanceof ServerLevel serverLevel) {
         if (altar.altarState != VaultAltarTileEntity.AltarState.IDLE) {
            PlayerVaultAltarData altarData = PlayerVaultAltarData.get(serverLevel);
            altar.recipe = altarData.getRecipe(altar.owner);
            if (altar.recipe != null && altar.recipe.isComplete() && altar.altarState != VaultAltarTileEntity.AltarState.INFUSING) {
               altar.altarState = VaultAltarTileEntity.AltarState.COMPLETE;
            }

            if (altar.altarState == VaultAltarTileEntity.AltarState.ACCEPTING) {
               if (serverLevel.getGameTime() % ModConfigs.VAULT_ALTAR.GROUP_DISPLAY_TICKS == 0L) {
                  altar.updateDisplayedItems();
               }

               altar.pullNearbyItems(serverLevel, ModConfigs.VAULT_ALTAR.ITEM_RANGE_CHECK);
            } else if (altar.altarState == VaultAltarTileEntity.AltarState.INFUSING) {
               altar.playInfusionEffects(serverLevel);
               if (altar.infusionTimer-- <= 0) {
                  altar.completeInfusion(level);
               }
            }
         }
      }
   }

   private void updateDisplayedItems() {
      if (this.getLevel() != null) {
         AltarInfusionRecipe infusionRecipe = this.getRecipe();
         if (infusionRecipe != null) {
            this.updateDisplayedIndex(infusionRecipe);
            ServerLevel level = (ServerLevel)this.getLevel();
            List<Player> players = level.getEntitiesOfClass(Player.class, this.getAABB(120.0), player -> player instanceof ServerPlayer);
            players.stream()
               .map(player -> (ServerPlayer)player)
               .forEach(
                  player -> ModNetwork.CHANNEL
                     .sendTo(
                        new ClientboundUpdateAltarIndexMessage(this.getBlockPos(), this.getDisplayedIndex()),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                     )
               );
         }
      }
   }

   private void updateDisplayedIndex(AltarInfusionRecipe infusionRecipe) {
      for (RequiredItems required : infusionRecipe.getRequiredItems()) {
         String id = required.getPoolId();
         List<ItemStack> stacks = required.getItems();
         int index = this.displayedIndex.computeIfAbsent(id, poolId -> 0);
         this.displayedIndex.put(id, index + 1 >= stacks.size() ? 0 : index + 1);
      }
   }

   public void onAltarPowered() {
      if (this.level instanceof ServerLevel serverWorld && this.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE) {
         PlayerVaultAltarData.get(serverWorld)
            .getAltars(this.owner)
            .stream()
            .filter(pos -> this.level.isLoaded(pos))
            .forEach(
               altarPos -> {
                  if (!this.getBlockPos().equals(altarPos)
                     && this.level.getBlockEntity(altarPos) instanceof VaultAltarTileEntity altar
                     && altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
                     altar.onRemoveVaultRock(this.owner);
                  }
               }
            );
         serverWorld.playSound(
            null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 0.5F
         );
         this.infusionTimer = ModConfigs.VAULT_ALTAR.INFUSION_TIME * 20;
         this.altarState = VaultAltarTileEntity.AltarState.INFUSING;
         this.sendUpdates();
      }
   }

   public InteractionResult onAddVaultRock(ServerPlayer player, ItemStack heldItem) {
      if (this.level != null && this.owner.equals(player.getUUID())) {
         ServerLevel serverLevel = (ServerLevel)this.level;

         for (BlockPos altarPosition : PlayerVaultAltarData.get(serverLevel).getAltars(player.getUUID())) {
            if (serverLevel.isLoaded(altarPosition)
               && serverLevel.getBlockEntity(altarPosition) instanceof VaultAltarTileEntity altar
               && altar.altarState == VaultAltarTileEntity.AltarState.INFUSING) {
               return InteractionResult.FAIL;
            }
         }

         PlayerVaultAltarData altarData = PlayerVaultAltarData.get(serverLevel);
         this.recipe = altarData.getRecipe(player, this.worldPosition);
         this.updateDisplayedIndex(this.recipe);
         this.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
         if (!player.isCreative()) {
            heldItem.shrink(1);
         }

         this.sendUpdates();
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   public InteractionResult onRemoveVaultRock(UUID playerId) {
      if (!this.owner.equals(playerId)) {
         return InteractionResult.FAIL;
      } else {
         this.setAltarState(VaultAltarTileEntity.AltarState.IDLE);
         this.recipe = null;
         this.infusionTimer = -666;
         if (this.getLevel() != null) {
            this.getLevel()
               .addFreshEntity(
                  new ItemEntity(
                     this.getLevel(),
                     this.getBlockPos().getX() + 0.5,
                     this.getBlockPos().getY() + 1.5,
                     this.getBlockPos().getZ() + 0.5,
                     new ItemStack(ModItems.VAULT_ROCK)
                  )
               );
         }

         this.sendUpdates();
         return InteractionResult.SUCCESS;
      }
   }

   private void completeInfusion(Level world) {
      if (this.recipe != null) {
         ServerLevel serverLevel = (ServerLevel)world;
         ItemStack stack = this.createCrystal(serverLevel);
         serverLevel.addFreshEntity(
            new ItemEntity(world, this.getBlockPos().getX() + 0.5, this.worldPosition.getY() + 1.5, this.worldPosition.getZ() + 0.5, stack)
         );
         PlayerStatsData.get(serverLevel.getServer()).onCrystalCrafted(this.owner, this.recipe.getRequiredItems());
         this.resetAltar(serverLevel);
         this.playCompletionEffects(serverLevel);
         MinecraftForge.EVENT_BUS.post(new CraftCrystalEvent(serverLevel.getPlayerByUUID(this.owner)));
         this.sendUpdates();
      }
   }

   @NotNull
   private ItemStack createCrystal(ServerLevel serverLevel) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData crystal = CrystalData.read(stack);
      int level = PlayerVaultStatsData.get(serverLevel).getVaultStats(this.owner).getVaultLevel();
      crystal.setLevel(level);
      crystal.write(stack);
      return stack;
   }

   private void playInfusionEffects(ServerLevel world) {
      float speed = this.infusionTimer * 0.01F - 0.5F;
      if (speed > 0.0F) {
         world.sendParticles(
            ParticleTypes.PORTAL, this.worldPosition.getX() + 0.5, this.getBlockPos().getY() + 1.6, this.getBlockPos().getZ() + 0.5, 3, 0.0, 0.0, 0.0, speed
         );
      }
   }

   private void playCompletionEffects(ServerLevel serverLevel) {
      DustParticleOptions particleData = new DustParticleOptions(new Vector3f(0.0F, 1.0F, 0.0F), 1.0F);

      for (int i = 0; i < 10; i++) {
         float offset = 0.1F * i;
         if (serverLevel.random.nextFloat() < 0.5F) {
            offset *= -1.0F;
         }

         serverLevel.sendParticles(
            particleData, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.6, this.worldPosition.getZ() + 0.5, 10, offset, offset, offset, 1.0
         );
      }

      serverLevel.playSound(
         null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.7F, 1.5F
      );
   }

   private void resetAltar(ServerLevel world) {
      this.infusionTimer = -666;
      this.altarState = VaultAltarTileEntity.AltarState.IDLE;
      PlayerVaultAltarData.get(world).removeRecipe(this.owner);
      this.recipe = null;
   }

   private void pullNearbyItems(ServerLevel level, double range) {
      if (this.recipe != null) {
         for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, this.getAABB(range))) {
            ItemStack stack = itemEntity.getItem();
            if (this.isItemValid(stack)) {
               List<RequiredItems> itemsToPull = this.recipe.getRequiredItems();
               itemsToPull.stream()
                  .filter(required -> !required.isComplete())
                  .filter(required -> required.getItems().stream().anyMatch(itemStack -> ItemStack.isSameIgnoreDurability(itemStack, stack)))
                  .forEach(required -> {
                     this.moveItemTowardPedestal(itemEntity);
                     if (this.isItemInRange(itemEntity.blockPosition())) {
                        this.incrementRequiredItems(itemEntity, required);
                     }
                  });
            }
         }
      }
   }

   public boolean isItemValid(ItemStack stack) {
      if (this.level instanceof ServerLevel serverLevel) {
         AltarInfusionRecipe altarInfusionRecipe = PlayerVaultAltarData.get(serverLevel).getRecipe(this.owner);
         if (altarInfusionRecipe == null) {
            return false;
         } else {
            return altarInfusionRecipe.isComplete()
               ? false
               : altarInfusionRecipe.getRequiredItems()
                  .stream()
                  .filter(requiredItem -> !requiredItem.isComplete())
                  .map(RequiredItems::getItems)
                  .flatMap(Collection::stream)
                  .anyMatch(requiredStack -> ItemStack.isSameIgnoreDurability(stack, requiredStack));
         }
      } else {
         return false;
      }
   }

   private void incrementRequiredItems(ItemEntity itemEntity, RequiredItems requiredItems) {
      int excess = requiredItems.getRemainder(itemEntity.getItem().getCount());
      if (excess > 0) {
         requiredItems.setCurrentAmount(requiredItems.getAmountRequired());
         itemEntity.getItem().setCount(excess);
      } else {
         requiredItems.addAmount(itemEntity.getItem().getCount());
         itemEntity.getItem().setCount(excess);
         itemEntity.discard();
      }

      this.sendUpdates();
      PlayerVaultAltarData.get().setDirty();
   }

   private void moveItemTowardPedestal(ItemEntity itemEntity) {
      float speed = ModConfigs.VAULT_ALTAR.PULL_SPEED / 20.0F;
      Vec3 target = Vec3.atCenterOf(this.getBlockPos());
      Vec3 current = itemEntity.position();
      Vec3 velocity = target.subtract(current).normalize().scale(speed);
      itemEntity.push(velocity.x, velocity.y, velocity.z);
   }

   private boolean isItemInRange(BlockPos itemPos) {
      return itemPos.distSqr(this.getBlockPos()) <= 4.0;
   }

   public AABB getAABB(double range) {
      return new AABB(
         this.getBlockPos().getX() + 0.5 - range,
         this.getBlockPos().getY() + 0.5 - range,
         this.getBlockPos().getZ() + 0.5 - range,
         this.getBlockPos().getX() + 0.5 + range,
         this.getBlockPos().getY() + 0.5 + range,
         this.getBlockPos().getZ() + 0.5 + range
      );
   }

   protected void saveAdditional(@NotNull CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.altarState != null) {
         tag.putInt("AltarState", this.altarState.ordinal());
      }

      if (this.owner != null) {
         tag.putUUID("Owner", this.owner);
      }

      if (this.recipe != null) {
         tag.put("Recipe", this.recipe.serializeNBT());
      }

      tag.putInt("InfusionTimer", this.infusionTimer);
      CompoundTag displayed = new CompoundTag();
      this.displayedIndex.forEach(displayed::putInt);
      tag.put("Displayed", displayed);
   }

   public void load(@NotNull CompoundTag tag) {
      super.load(tag);
      if (!tag.contains("AltarState")) {
         this.migrate(tag.getBoolean("containsVaultRock"));
      }

      if (tag.contains("AltarState")) {
         this.altarState = VaultAltarTileEntity.AltarState.values()[tag.getInt("AltarState")];
      }

      if (tag.contains("Owner")) {
         this.owner = tag.getUUID("Owner");
      }

      if (tag.contains("Recipe")) {
         this.recipe = new AltarInfusionRecipe(tag.getCompound("Recipe"));
      }

      if (tag.contains("InfusionTimer")) {
         this.infusionTimer = tag.getInt("InfusionTimer");
      }

      if (tag.contains("Displayed")) {
         this.displayedIndex.clear();
         CompoundTag displayed = tag.getCompound("Displayed");

         for (String poolId : displayed.getAllKeys()) {
            this.displayedIndex.put(poolId, displayed.getInt(poolId));
         }
      }
   }

   private void migrate(boolean containsVaultRock) {
      this.altarState = containsVaultRock ? VaultAltarTileEntity.AltarState.ACCEPTING : VaultAltarTileEntity.AltarState.IDLE;
   }

   @NotNull
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithoutMetadata);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public static enum AltarState {
      IDLE,
      ACCEPTING,
      COMPLETE,
      INFUSING;
   }
}
