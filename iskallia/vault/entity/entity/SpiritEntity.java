package iskallia.vault.entity.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.EntityState;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.VaultMode;
import iskallia.vault.world.data.InventorySnapshot;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class SpiritEntity extends Mob implements IPlayerSkinHolder {
   private static final String OWNER_PROFILE_TAG = "ownerProfile";
   private static final String JOIN_STATE_TAG = "joinState";
   private static final String RECYCLABLE_TAG = "recyclable";
   private GameProfile gameProfile = null;
   @Deprecated
   private final NonNullList<ItemStack> items = NonNullList.create();
   private int vaultLevel;
   private int playerLevel;
   private boolean recyclable = false;
   private long yeetCooldown;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   @Nullable
   private EntityState joinState = null;
   @Nullable
   private UUID heroId = null;
   private float rescuedBonus = 0.0F;
   @Nullable
   private InventorySnapshot inventorySnapshot = null;

   public SpiritEntity(EntityType<SpiritEntity> entityType, Level level) {
      super(entityType, level);
   }

   public void transferSpiritData(Player player, EntityState joinState, int vaultLevel) {
      this.setGameProfile(player.getGameProfile());
      this.setVaultLevel(vaultLevel);
      this.setPlayerLevel(SidedHelper.getVaultLevel(player));
      this.addDrops(player);
      this.setJoinState(joinState);
   }

   private void addDrops(Player player) {
      this.inventorySnapshot = new InventorySnapshot.Builder(player).setStackFilter((p, stack) -> shouldAddItem(stack)).createSnapshot();
   }

   public void setJoinState(EntityState joinState) {
      this.joinState = joinState;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPlayerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ServerLevel serverLevel = player.getLevel();
         MinecraftServer server = serverLevel.getServer();
         ServerVaults.get(serverLevel)
            .ifPresent(
               vault -> {
                  if (vault.get(Vault.MODIFIERS).getModifiers().stream().noneMatch(m -> m instanceof PlayerInventoryRestoreModifier)
                     && ((VaultMode.GameRuleValue)serverLevel.getGameRules().getRule(ModGameRules.MODE)).get() != VaultMode.HARDCORE) {
                     int vaultLevel = vault.get(Vault.LEVEL).get();
                     PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
                     InventorySnapshot invSnapshot = new InventorySnapshot.Builder(player).setStackFilter((p, stack) -> shouldAddItem(stack)).createSnapshot();
                     if (serverLevel.players().size() > 1) {
                        EntityState joinState = vault.get(Vault.LISTENERS).get(player.getUUID()).get(Listener.JOIN_STATE);
                        server.tell(
                           new TickTask(
                              server.getTickCount() + 10,
                              () -> {
                                 data.putVaultSpiritData(initSpiritData(vault, player, invSnapshot, vaultLevel));
                                 if (ModEntities.SPIRIT.spawn(serverLevel, null, null, player.blockPosition(), MobSpawnType.EVENT, false, false) instanceof SpiritEntity spirit
                                    )
                                  {
                                    spirit.transferSpiritData(player, joinState, vaultLevel);
                                 }
                              }
                           )
                        );
                     } else {
                        server.tell(
                           new TickTask(server.getTickCount() + 10, () -> data.putVaultSpiritData(initSpiritData(vault, player, invSnapshot, vaultLevel)))
                        );
                     }
                  }
               }
            );
      }
   }

   public static void onVaultEnd(Level world, UUID vaultId) {
      if (world instanceof ServerLevel serverLevel) {
         PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
         MinecraftServer server = serverLevel.getServer();
         server.tell(
            new TickTask(
               server.getTickCount() + 20,
               () -> {
                  for (PlayerSpiritRecoveryData.SpiritData spiritData : data.getVaultSpiritData(vaultId)) {
                     ServerLevel respawnLevel = server.getLevel(spiritData.respawnDimension());
                     spawnSpirit(
                        respawnLevel,
                        spiritData.vaultLevel(),
                        spiritData.drops(),
                        spiritData.inventorySnapshot(),
                        spiritData.playerLevel(),
                        Vec3.atCenterOf(spiritData.respawnPos()),
                        spiritData.respawnPos(),
                        spiritData.playerGameProfile()
                     );
                  }

                  data.removeVaultSpiritData(vaultId);
               }
            )
         );
      }
   }

   private static void spawnSpirit(
      ServerLevel respawnLevel,
      int vaultLevel,
      List<ItemStack> drops,
      InventorySnapshot inventorySnapshot,
      int playerLevel,
      Vec3 precisePosition,
      BlockPos position,
      GameProfile playerGameProfile
   ) {
      for (MutableBlockPos p : BlockPos.spiralAround(new BlockPos(precisePosition), 1, Direction.EAST, Direction.SOUTH)) {
         if (!p.equals(position)) {
            Vec3 respawnPos = DismountHelper.findSafeDismountLocation(ModEntities.SPIRIT, respawnLevel, p.immutable(), false);
            if (respawnPos != null) {
               spawnSpirit(respawnLevel, respawnPos, playerLevel, vaultLevel, drops, inventorySnapshot, playerGameProfile);
               return;
            }
         }
      }

      spawnSpirit(respawnLevel, precisePosition, playerLevel, vaultLevel, drops, inventorySnapshot, playerGameProfile);
   }

   private static PlayerSpiritRecoveryData.SpiritData initSpiritData(Vault vault, ServerPlayer player, InventorySnapshot invSnapshot, int vaultLevel) {
      BlockPos respawnPosition = player.getRespawnPosition();
      if (respawnPosition == null) {
         ServerLevel respawnDimensionLevel = player.getServer().getLevel(player.getRespawnDimension());
         respawnPosition = respawnDimensionLevel.getSharedSpawnPos();
         respawnPosition = PlayerRespawnLogic.getOverworldRespawnPos(respawnDimensionLevel, respawnPosition.getX(), respawnPosition.getZ());
      }

      return new PlayerSpiritRecoveryData.SpiritData(
         vault.get(Vault.ID),
         player.getUUID(),
         invSnapshot,
         vaultLevel,
         SidedHelper.getVaultLevel(player),
         player.getRespawnDimension(),
         respawnPosition,
         player.getGameProfile()
      );
   }

   private static void spawnSpirit(
      ServerLevel serverLevel,
      Vec3 respawnPos,
      int playerLevel,
      int vaultLevel,
      List<ItemStack> drops,
      InventorySnapshot inventorySnapshot,
      GameProfile playerGameProfile
   ) {
      if (ModEntities.SPIRIT.spawn(serverLevel, null, null, new BlockPos(respawnPos), MobSpawnType.EVENT, false, false) instanceof SpiritEntity spirit) {
         spirit.setItems(drops);
         spirit.setInventorySnapshot(inventorySnapshot);
         spirit.setGameProfile(playerGameProfile);
         spirit.setVaultLevel(vaultLevel);
         spirit.setPlayerLevel(playerLevel);
         spirit.setRecyclable(true);
      }
   }

   public void setInventorySnapshot(InventorySnapshot inventorySnapshot) {
      this.inventorySnapshot = inventorySnapshot;
   }

   public void addPlayersItems(Player player) {
      this.setInventorySnapshot(new InventorySnapshot.Builder(player).setStackFilter((p, stack) -> shouldAddItem(stack)).createSnapshot());
   }

   private static boolean shouldAddItem(ItemStack stack) {
      return !stack.isEmpty()
         && (
            !AttributeGearData.hasData(stack)
               || !AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue())
         );
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public void setVaultLevel(int vaultLevel) {
      this.vaultLevel = vaultLevel;
   }

   public int getVaultLevel() {
      return this.vaultLevel;
   }

   public void setPlayerLevel(int playerLevel) {
      this.playerLevel = playerLevel;
   }

   public int getPlayerLevel() {
      return this.playerLevel;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE, Optional.empty());
   }

   public void checkDespawn() {
   }

   public boolean canBeLeashed(Player pPlayer) {
      return false;
   }

   protected InteractionResult mobInteract(Player player, InteractionHand pHand) {
      return player.getPassengers().isEmpty() && this.putInPlayersHand(player) ? InteractionResult.SUCCESS : super.mobInteract(player, pHand);
   }

   public boolean putInPlayersHand(Player player) {
      if (!player.getOffhandItem().isEmpty()) {
         player.displayClientMessage(new TextComponent("You can't pick me up with that in offhand"), true);
         return false;
      } else if (this.startRiding(player)) {
         this.setPose(Pose.SLEEPING);
         if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(player));
         }

         this.yeetCooldown = player.level.getGameTime() + 20L;
         return true;
      } else {
         return false;
      }
   }

   public boolean startRiding(Entity pEntity, boolean pForce) {
      boolean ret = super.startRiding(pEntity, pForce);
      if (this.isPassenger()) {
         this.setPose(Pose.SLEEPING);
      }

      return ret;
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide()) {
         Entity vehicle = this.getVehicle();
         if (vehicle != null) {
            if (this.yeetCooldown > this.level.getGameTime()) {
               return;
            }

            if (vehicle instanceof Player player && !player.getOffhandItem().isEmpty()) {
               this.stopRiding();
               this.markWithHero(vehicle);
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection.send(new ClientboundSetPassengersPacket(vehicle));
               }

               float motionAngle = vehicle.getYHeadRot() + 90.0F;
               float x = Mth.cos(motionAngle * (float) (Math.PI / 180.0));
               float z = Mth.sin(motionAngle * (float) (Math.PI / 180.0));
               this.setPos(this.getPosition(0.0F).add(x, 0.0, z));
               player.displayClientMessage(new TextComponent("You can't hold me with that in offhand"), true);
            } else if (vehicle.isCrouching()) {
               this.stopRiding();
               this.markWithHero(vehicle);
               if (vehicle instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection.send(new ClientboundSetPassengersPacket(vehicle));
               }

               this.setPos(this.getPosition(0.0F).add(0.0, 1.0, 0.0));
               float motionAngle = vehicle.getYHeadRot() + 90.0F;
               float x = Mth.cos(motionAngle * (float) (Math.PI / 180.0));
               float z = Mth.sin(motionAngle * (float) (Math.PI / 180.0));
               Vec3 vehicleMotion = vehicle.getDeltaMovement();
               this.setDeltaMovement(new Vec3(x + vehicleMotion.x() * 10.0, 0.1, z + vehicleMotion.z() * 10.0));
            }
         } else if (this.getPose() == Pose.SLEEPING) {
            Vec3 movement = this.getDeltaMovement();
            if (Math.abs(movement.x()) + Math.abs(movement.z()) < 0.2) {
               this.setPose(Pose.STANDING);
            }
         }
      }
   }

   private void markWithHero(Entity vehicle) {
      if (vehicle instanceof ServerPlayer player
         && ServerVaults.get(vehicle.level).isPresent()
         && this.items.size() > 5
         && this.gameProfile.getId() != player.getUUID()) {
         this.heroId = player.getUUID();
      }
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean ignoreExplosion() {
      return true;
   }

   public boolean isInvulnerableTo(DamageSource pSource) {
      return pSource != DamageSource.OUT_OF_WORLD;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return new ArrayList<>();
   }

   public ItemStack getItemBySlot(EquipmentSlot pSlot) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   @Override
   public Optional<GameProfile> getGameProfile() {
      return (Optional<GameProfile>)this.entityData.get(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE);
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      this.entityData.set(OPTIONAL_GAME_PROFILE, Optional.ofNullable(gameProfile));
      this.gameProfile = gameProfile;
   }

   public void setRecyclable(boolean recyclable) {
      this.recyclable = recyclable;
   }

   @Override
   public Optional<ResourceLocation> getSkinLocation() {
      return Optional.ofNullable(this.skinLocation);
   }

   @Override
   public boolean isUpdatingSkin() {
      return this.updatingSkin;
   }

   @Override
   public void setSkinLocation(ResourceLocation skinLocation) {
      this.skinLocation = skinLocation;
   }

   @Override
   public void startUpdatingSkin() {
      this.updatingSkin = true;
   }

   @Override
   public void stopUpdatingSkin() {
      this.updatingSkin = false;
   }

   @Override
   public boolean hasSlimSkin() {
      return this.slimSkin;
   }

   @Override
   public void setSlimSkin(boolean slimSkin) {
      this.slimSkin = slimSkin;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.gameProfile != null) {
         compound.put("ownerProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.gameProfile));
      }

      ListTag itemList = new ListTag();

      for (ItemStack item : this.items) {
         itemList.add(item.save(new CompoundTag()));
      }

      compound.put("items", itemList);
      if (this.inventorySnapshot != null) {
         compound.put("inventorySnapshot", this.inventorySnapshot.serializeNBT());
      }

      compound.putInt("vaultLevel", this.vaultLevel);
      compound.putInt("playerLevel", this.playerLevel);
      if (this.joinState != null) {
         ArrayBitBuffer buffer = ArrayBitBuffer.empty();
         this.joinState.write(buffer, new DiskSyncContext(Version.v1_0));
         compound.putLongArray("joinState", buffer.toLongArray());
      }

      if (this.isPassenger()) {
         compound.putBoolean("isBeingCarried", true);
      }

      compound.putBoolean("recyclable", this.recyclable);
      if (this.heroId != null) {
         compound.putUUID("heroId", this.heroId);
      }

      compound.putFloat("rescuedBonus", this.rescuedBonus);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGameProfile(compound.contains("ownerProfile") ? NbtUtils.readGameProfile(compound.getCompound("ownerProfile")) : null);
      this.items.clear();

      for (Tag tag : compound.getList("items", 10)) {
         this.items.add(ItemStack.of((CompoundTag)tag));
      }

      if (compound.contains("inventorySnapshot")) {
         this.inventorySnapshot = new InventorySnapshot(false, false);
         this.inventorySnapshot.deserializeNBT(compound.getCompound("inventorySnapshot"));
      } else {
         this.inventorySnapshot = null;
      }

      this.vaultLevel = compound.getInt("vaultLevel");
      this.playerLevel = compound.contains("playerLevel") ? compound.getInt("playerLevel") : this.vaultLevel;
      if (compound.contains("joinState", 12)) {
         this.joinState = new EntityState().read(ArrayBitBuffer.backing(compound.getLongArray("joinState"), 0), new DiskSyncContext(Version.v1_0));
      } else {
         this.joinState = null;
      }

      if (compound.getBoolean("isBeingCarried")) {
         this.setPose(Pose.SLEEPING);
      }

      this.recyclable = !compound.contains("recyclable") || compound.getBoolean("recyclable");
      if (compound.contains("heroId", 11)) {
         this.heroId = compound.getUUID("heroId");
      }

      this.rescuedBonus = compound.getFloat("rescuedBonus");
   }

   public void setItems(List<ItemStack> items) {
      this.items.clear();
      this.items.addAll(items);
   }

   public void teleportOut() {
      if (this.joinState != null) {
         ServerVaults.get(this.level).ifPresent(vault -> {
            if (this.level instanceof ServerLevel serverLevelx) {
               PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevelx);
               data.removeVaultSpiritData(this.gameProfile.getId(), vault.get(Vault.ID));
            }
         });
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.joinState.teleport(this);
         this.joinState = null;
         if (this.heroId != null && this.level instanceof ServerLevel serverLevel) {
            PlayerSpiritRecoveryData.get(serverLevel).setHeroDiscount(this.heroId, serverLevel.random);
         }

         this.rescuedBonus = ModConfigs.SPIRIT.getRescuedBonus(this.level.random);
      }
   }

   public boolean isRecyclable() {
      return this.recyclable;
   }

   public float getRescuedBonus() {
      return this.rescuedBonus;
   }

   public void setRescuedBonus(float rescuedBonus) {
      this.rescuedBonus = rescuedBonus;
   }

   public InventorySnapshot getInventorySnapshot() {
      return this.inventorySnapshot;
   }
}
