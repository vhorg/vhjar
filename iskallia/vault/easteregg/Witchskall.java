package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.util.GearItemStackBuilder;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Witchskall {
   public static final float WITCHSKALL_CHANCE = 0.001F;
   public static final int WITCHSKALLIFICATION_TICKS = 100;
   public static DataParameter<Integer> WITCHSKALL_TICKS;
   public static DataParameter<Boolean> IS_WITCHSKALL;

   public static int getWitchskallificationTicks(WitchEntity witchEntity) {
      return (Integer)witchEntity.func_184212_Q().func_187225_a(WITCHSKALL_TICKS);
   }

   public static boolean isWitchskall(WitchEntity witchEntity) {
      return (Boolean)witchEntity.func_184212_Q().func_187225_a(IS_WITCHSKALL);
   }

   public static int setWitchskallificationTicks(WitchEntity witchEntity, int ticks) {
      witchEntity.func_184212_Q().func_187227_b(WITCHSKALL_TICKS, ticks);
      return ticks;
   }

   public static void witchskallificate(WitchEntity witchEntity) {
      setWitchskallificationTicks(witchEntity, 0);
      witchEntity.func_184212_Q().func_187227_b(IS_WITCHSKALL, true);
      witchEntity.func_200203_b(new StringTextComponent("Witchskall"));
   }

   @SubscribeEvent
   public static void onWitchTick(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.field_70170_p;
      if (!world.field_72995_K) {
         if (entity instanceof WitchEntity) {
            WitchEntity witchEntity = (WitchEntity)entity;
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
                        ServerWorld serverWorld = (ServerWorld)world;
                        serverWorld.func_195598_a(
                           (IParticleData)ModParticles.GREEN_FLAME.get(),
                           entity.func_226277_ct_(),
                           entity.func_226278_cu_(),
                           entity.func_226281_cx_(),
                           100,
                           0.5,
                           1.0,
                           0.5,
                           0.1
                        );
                        serverWorld.func_184148_a(
                           null,
                           entity.func_226277_ct_(),
                           entity.func_226278_cu_(),
                           entity.func_226281_cx_(),
                           ModSounds.WITCHSKALL_IDLE,
                           SoundCategory.MASTER,
                           1.1F,
                           1.0F
                        );
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
      if (!entity.field_70170_p.func_201670_d()) {
         if (entity instanceof WitchEntity && isWitchskall((WitchEntity)entity)) {
            Entity trueSource = event.getSource().func_76346_g();
            if (trueSource instanceof ServerPlayerEntity) {
               ServerPlayerEntity player = (ServerPlayerEntity)trueSource;
               AdvancementHelper.grantCriterion(player, Vault.id("main/witchskall"), "witchskall_killed");
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWitchskallDrops(LivingDropsEvent event) {
      Entity entity = event.getEntity();
      if (!entity.field_70170_p.func_201670_d()) {
         if (entity instanceof WitchEntity) {
            if (isWitchskall((WitchEntity)entity)) {
               ServerWorld world = (ServerWorld)entity.field_70170_p;
               ItemStack itemStack = new GearItemStackBuilder(ModItems.HELMET)
                  .setGearRarity(VaultGear.Rarity.UNIQUE)
                  .setColor(-5384139)
                  .setSpecialModelId(ModModels.SpecialGearModel.ISKALL_HOLOLENS.getId())
                  .build();
               ItemEntity itemEntity = new ItemEntity(world, entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), itemStack);
               itemEntity.func_174869_p();
               event.getDrops().add(itemEntity);
            }
         }
      }
   }
}
