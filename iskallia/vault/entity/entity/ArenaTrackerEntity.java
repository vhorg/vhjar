package iskallia.vault.entity.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.world.data.ArenaRaidData;
import iskallia.vault.world.legacy.raid.ArenaRaid;
import java.util.Collections;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArenaTrackerEntity extends LivingEntity {
   public final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.PROGRESS);

   public ArenaTrackerEntity(EntityType<? extends LivingEntity> type, Level world) {
      super(type, world);
      this.bossInfo.setDarkenScreen(true);
      this.bossInfo.setVisible(true);
      this.setNoGravity(true);
      this.setInvulnerable(true);
      this.setInvisible(true);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
      }
   }

   public float getHealth() {
      if (!this.level.isClientSide && this.level.dimension() == VaultMod.ARENA_KEY) {
         ArenaRaid raid = ArenaRaidData.get((ServerLevel)this.level).getAt(this.blockPosition());
         if (raid == null) {
            return super.getHealth();
         } else {
            float health = 0.0F;

            for (UUID uuid : raid.spawner.fighters) {
               Entity entity = ((ServerLevel)this.level).getEntity(uuid);
               if (entity instanceof LivingEntity) {
                  health += ((LivingEntity)entity).getHealth();
               }
            }

            return health;
         }
      } else {
         return super.getHealth();
      }
   }

   public void setCustomName(Component name) {
      super.setCustomName(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public Iterable<ItemStack> getArmorSlots() {
      return Collections.emptyList();
   }

   public ItemStack getItemBySlot(EquipmentSlot slot) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.LEFT;
   }
}
