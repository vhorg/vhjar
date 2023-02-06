package iskallia.vault.entity.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.util.SkinProfile;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EternalSpiritEntity extends Mob implements IPlayerSkinHolder {
   private static final String OWNER_PROFILE_TAG = "ownerProfile";
   private GameProfile gameProfile = null;
   private UUID eternalUUID;
   private UUID ownerUUID;
   private long yeetCooldown;
   private ResourceLocation skinLocation = null;
   private final SkinProfile skinProfile = new SkinProfile();
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   private static final EntityDataAccessor<Optional<UUID>> DATA_ID_ETERNAL_UUID = SynchedEntityData.defineId(
      EternalSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(
      EternalSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );

   public EternalSpiritEntity(EntityType<EternalSpiritEntity> entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_ETERNAL_UUID, Optional.empty());
      this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
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
      if (this.getOwnerUUID() != null && !this.getOwnerUUID().equals(player.getUUID())) {
         return false;
      } else if (!player.getOffhandItem().isEmpty()) {
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
      if (this.level.isClientSide()) {
         if (this.skinProfile.isEmpty()) {
            EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(this.getEternalUUID());
            if (snapshot != null) {
               this.skinProfile.updateSkin(snapshot.getName());
            }
         }
      } else {
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

   public void setEternalUUID(UUID eternalUUID) {
      this.entityData.set(DATA_ID_ETERNAL_UUID, Optional.ofNullable(eternalUUID));
      this.eternalUUID = eternalUUID;
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(ownerUUID));
      this.ownerUUID = ownerUUID;
   }

   public UUID getEternalUUID() {
      return ((Optional)this.entityData.get(DATA_ID_ETERNAL_UUID)).isPresent()
         ? (UUID)((Optional)this.entityData.get(DATA_ID_ETERNAL_UUID)).get()
         : this.eternalUUID;
   }

   public UUID getOwnerUUID() {
      return ((Optional)this.entityData.get(DATA_ID_OWNER_UUID)).isPresent() ? (UUID)((Optional)this.entityData.get(DATA_ID_OWNER_UUID)).get() : this.ownerUUID;
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
      return Optional.of(this.gameProfile);
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }

   @Override
   public Optional<ResourceLocation> getSkinLocation() {
      return Optional.ofNullable(this.skinProfile.getLocationSkin());
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

      if (this.eternalUUID != null) {
         compound.putUUID("eternalUUID", this.eternalUUID);
      }

      if (this.ownerUUID != null) {
         compound.putUUID("ownerUUID", this.ownerUUID);
      }

      if (this.isPassenger()) {
         compound.putBoolean("isBeingCarried", true);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGameProfile(compound.contains("ownerProfile") ? NbtUtils.readGameProfile(compound.getCompound("ownerProfile")) : null);
      if (compound.contains("eternalUUID")) {
         this.setEternalUUID(compound.getUUID("eternalUUID"));
      }

      if (compound.contains("ownerUUID")) {
         this.setOwnerUUID(compound.getUUID("ownerUUID"));
      }

      if (compound.getBoolean("isBeingCarried")) {
         this.setPose(Pose.SLEEPING);
      }
   }
}
