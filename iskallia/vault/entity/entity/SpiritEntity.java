package iskallia.vault.entity.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.EntityState;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class SpiritEntity extends Mob implements IPlayerSkinHolder {
   private static final String OWNER_PROFILE_TAG = "ownerProfile";
   private static final String JOIN_STATE_TAG = "joinState";
   private GameProfile gameProfile = null;
   private final NonNullList<ItemStack> items = NonNullList.create();
   private int vaultLevel;
   private long yeetCooldown;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   @Nullable
   private EntityState joinState = null;

   public SpiritEntity(EntityType<SpiritEntity> entityType, Level level) {
      super(entityType, level);
   }

   public void transferPlayerData(Player player, EntityState joinState) {
      this.setGameProfile(player.getGameProfile());
      this.setVaultLevel(SidedHelper.getVaultLevel(player));
      this.addPlayersItems(player);
      this.setJoinState(joinState);
   }

   public void setJoinState(EntityState joinState) {
      this.joinState = joinState;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onPlayerInVaultDrops(LivingDropsEvent event) {
      if (event.getEntity() instanceof Player player && !player.level.isClientSide()) {
         Level level = player.level;
         ServerVaults.get(level)
            .ifPresent(
               vault -> {
                  if (level instanceof ServerLevel serverLevel
                     && level.players().size() > 1
                     && vault.get(Vault.MODIFIERS).getModifiers().stream().noneMatch(m -> m instanceof PlayerInventoryRestoreModifier)
                     && ModEntities.SPIRIT.spawn(serverLevel, null, null, player.blockPosition(), MobSpawnType.EVENT, false, false) instanceof SpiritEntity spirit
                     )
                   {
                     EntityState joinState = vault.get(Vault.LISTENERS).get(player.getUUID()).get(Listener.JOIN_STATE);
                     spirit.transferPlayerData(player, joinState);
                     event.setCanceled(true);
                  }
               }
            );
      }
   }

   public void addPlayersItems(Player player) {
      for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
         ItemStack stackInSlot = player.getInventory().getItem(slot);
         if (this.shouldAddItem(stackInSlot)) {
            this.items.add(stackInSlot.copy());
         }
      }

      if (ModList.get().isLoaded("curios")) {
         IntegrationCurios.getCuriosItemStacks(player).forEach((slotType, stacks) -> stacks.forEach(stack -> {
            if (this.shouldAddItem(stack)) {
               this.items.add(stack.copy());
            }
         }));
      }
   }

   private boolean shouldAddItem(ItemStack stack) {
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

   public boolean fireImmune() {
      return true;
   }

   public boolean ignoreExplosion() {
      return true;
   }

   public boolean isInvulnerableTo(DamageSource pSource) {
      return true;
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
      compound.putInt("vaultLevel", this.vaultLevel);
      if (this.joinState != null) {
         ArrayBitBuffer buffer = ArrayBitBuffer.empty();
         this.joinState.write(buffer, new DiskSyncContext(Version.v1_0));
         compound.putLongArray("joinState", buffer.toLongArray());
      }

      if (this.isPassenger()) {
         compound.putBoolean("isBeingCarried", true);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGameProfile(compound.contains("ownerProfile") ? NbtUtils.readGameProfile(compound.getCompound("ownerProfile")) : null);
      this.items.clear();

      for (Tag tag : compound.getList("items", 10)) {
         this.items.add(ItemStack.of((CompoundTag)tag));
      }

      this.vaultLevel = compound.getInt("vaultLevel");
      if (compound.contains("joinState", 12)) {
         this.joinState = new EntityState().read(ArrayBitBuffer.backing(compound.getLongArray("joinState"), 0), new DiskSyncContext(Version.v1_0));
      } else {
         this.joinState = null;
      }

      if (compound.getBoolean("isBeingCarried")) {
         this.setPose(Pose.SLEEPING);
      }
   }

   public void setItems(List<ItemStack> items) {
      this.items.clear();
      this.items.addAll(items);
   }

   public void teleportOut() {
      if (this.joinState != null) {
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.joinState.teleport(this);
         this.joinState = null;
      }
   }
}