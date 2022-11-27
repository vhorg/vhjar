package iskallia.vault.easteregg;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.AdvancementHelper;
import java.util.Random;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Witchskall {
   public static final float WITCHSKALL_CHANCE = 0.001F;
   public static final int WITCHSKALLIFICATION_TICKS = 100;
   public static EntityDataAccessor<Integer> WITCHSKALL_TICKS;
   public static EntityDataAccessor<Boolean> IS_WITCHSKALL;

   public static int getWitchskallificationTicks(Witch witchEntity) {
      return (Integer)witchEntity.getEntityData().get(WITCHSKALL_TICKS);
   }

   public static boolean isWitchskall(Witch witchEntity) {
      return (Boolean)witchEntity.getEntityData().get(IS_WITCHSKALL);
   }

   public static int setWitchskallificationTicks(Witch witchEntity, int ticks) {
      witchEntity.getEntityData().set(WITCHSKALL_TICKS, ticks);
      return ticks;
   }

   public static void witchskallificate(Witch witchEntity) {
      setWitchskallificationTicks(witchEntity, 0);
      witchEntity.getEntityData().set(IS_WITCHSKALL, true);
      witchEntity.setCustomName(new TextComponent("Witchskall"));
   }

   @SubscribeEvent
   public static void onWitchTick(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      Level world = entity.level;
      if (!world.isClientSide) {
         if (entity instanceof Witch witchEntity) {
            if (!isWitchskall(witchEntity)) {
               int witchskallTicks = getWitchskallificationTicks(witchEntity);
               if (witchskallTicks != 0) {
                  if (witchskallTicks <= -1) {
                     if (new Random().nextFloat() <= 0.001F) {
                        setWitchskallificationTicks(witchEntity, 100);
                     } else {
                        setWitchskallificationTicks(witchEntity, 0);
                     }
                  } else {
                     int setWitchskallTicks = setWitchskallificationTicks(witchEntity, witchskallTicks - 1);
                     if (setWitchskallTicks == 0) {
                        ServerLevel serverWorld = (ServerLevel)world;
                        serverWorld.sendParticles(
                           (SimpleParticleType)ModParticles.GREEN_FLAME.get(), entity.getX(), entity.getY(), entity.getZ(), 100, 0.5, 1.0, 0.5, 0.1
                        );
                        serverWorld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.WITCHSKALL_IDLE, SoundSource.MASTER, 1.1F, 1.0F);
                        witchskallificate(witchEntity);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWitchskallDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level.isClientSide()) {
         if (entity instanceof Witch && isWitchskall((Witch)entity)) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               AdvancementHelper.grantCriterion(player, VaultMod.id("main/witchskall"), "witchskall_killed");
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWitchskallDrops(LivingDropsEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level.isClientSide()) {
         if (entity instanceof Witch) {
            if (isWitchskall((Witch)entity)) {
               ServerLevel world = (ServerLevel)entity.level;
            }
         }
      }
   }
}
