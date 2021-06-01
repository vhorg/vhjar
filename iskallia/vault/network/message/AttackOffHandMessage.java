package iskallia.vault.network.message;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AttackOffHandMessage {
   private int entityId;

   public AttackOffHandMessage(int entityId) {
      this.entityId = entityId;
   }

   public static void encode(AttackOffHandMessage packet, PacketBuffer buf) {
      buf.writeInt(packet.entityId);
   }

   public static AttackOffHandMessage decode(PacketBuffer buf) {
      return new AttackOffHandMessage(buf.readInt());
   }

   public static void handle(AttackOffHandMessage packet, Supplier<Context> ctx) {
      if (packet != null) {
         ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            ItemStack offhand = player.func_184592_cb();
            if (!offhand.func_190926_b()) {
               int level = PlayerVaultStatsData.get((ServerWorld)player.field_70170_p).getVaultStats(player).getVaultLevel();
               if (!ModAttributes.MIN_VAULT_LEVEL.exists(offhand) || level >= ModAttributes.MIN_VAULT_LEVEL.get(offhand).get().getValue(offhand)) {
                  Entity target = player.field_70170_p.func_73045_a(packet.entityId);
                  if (target != null) {
                     float reach = 6.0F;
                     float renderViewEntityOffsetFromPlayerMitigator = 0.2F;
                     reach += renderViewEntityOffsetFromPlayerMitigator;
                     double distanceSquared = player.func_70068_e(target);
                     if (reach * reach >= distanceSquared) {
                        attackTargetEntityWithCurrentOffhandItem(player, target);
                     }

                     swingArm(player, Hand.OFF_HAND);
                  }
               }
            }
         });
      }
   }

   public static void swingArm(ServerPlayerEntity playerEntity, Hand hand) {
      ItemStack stack = playerEntity.func_184586_b(hand);
      if ((stack.func_190926_b() || !stack.onEntitySwing(playerEntity))
         && (!playerEntity.field_82175_bq || playerEntity.field_110158_av >= getArmSwingAnimationEnd(playerEntity) / 2 || playerEntity.field_110158_av < 0)) {
         playerEntity.field_110158_av = -1;
         playerEntity.field_82175_bq = true;
         playerEntity.field_184622_au = hand;
         if (playerEntity.field_70170_p instanceof ServerWorld) {
            SAnimateHandPacket sanimatehandpacket = new SAnimateHandPacket(playerEntity, hand == Hand.MAIN_HAND ? 0 : 3);
            ServerChunkProvider serverchunkprovider = ((ServerWorld)playerEntity.field_70170_p).func_72863_F();
            serverchunkprovider.func_217218_b(playerEntity, sanimatehandpacket);
         }
      }
   }

   private static int getArmSwingAnimationEnd(LivingEntity livingEntity) {
      if (EffectUtils.func_205135_a(livingEntity)) {
         return 6 - (1 + EffectUtils.func_205134_b(livingEntity));
      } else {
         return livingEntity.func_70644_a(Effects.field_76419_f) ? 6 + (1 + livingEntity.func_70660_b(Effects.field_76419_f).func_76458_c()) * 2 : 6;
      }
   }

   public static void attackTargetEntityWithCurrentOffhandItem(ServerPlayerEntity serverPlayerEntity, Entity target) {
      if (serverPlayerEntity.field_71134_c.func_73081_b() == GameType.SPECTATOR) {
         serverPlayerEntity.func_175399_e(target);
      } else {
         attackTargetEntityWithCurrentOffhandItemAsSuper(serverPlayerEntity, target);
      }
   }

   public static void attackTargetEntityWithCurrentOffhandItemAsSuper(PlayerEntity player, Entity target) {
      if (ForgeHooks.onPlayerAttackTarget(player, target) && target.func_70075_an() && !target.func_85031_j(player)) {
         float attackDamage = (float)player.func_233637_b_(Attributes.field_233823_f_);
         float enchantmentAffectsTargetBonus;
         if (target instanceof LivingEntity) {
            enchantmentAffectsTargetBonus = EnchantmentHelper.func_152377_a(player.func_184592_cb(), ((LivingEntity)target).func_70668_bt());
         } else {
            enchantmentAffectsTargetBonus = EnchantmentHelper.func_152377_a(player.func_184592_cb(), CreatureAttribute.field_223222_a_);
         }

         float cooledAttackStrength = player.func_184825_o(0.5F);
         attackDamage *= 0.2F + cooledAttackStrength * cooledAttackStrength * 0.8F;
         enchantmentAffectsTargetBonus *= cooledAttackStrength;
         if (attackDamage > 0.0F || enchantmentAffectsTargetBonus > 0.0F) {
            boolean flag = cooledAttackStrength > 0.9F;
            boolean flag1 = false;
            int i = 0;
            i += EnchantmentHelper.func_77501_a(player);
            if (player.func_70051_ag() && flag) {
               player.field_70170_p
                  .func_184148_a(
                     (PlayerEntity)null,
                     player.func_226277_ct_(),
                     player.func_226278_cu_(),
                     player.func_226281_cx_(),
                     SoundEvents.field_187721_dT,
                     player.func_184176_by(),
                     1.0F,
                     1.0F
                  );
               i++;
               flag1 = true;
            }

            boolean flag2 = flag
               && player.field_70143_R > 0.0F
               && !player.func_233570_aj_()
               && !player.func_70617_f_()
               && !player.func_70090_H()
               && !player.func_70644_a(Effects.field_76440_q)
               && !player.func_184218_aH()
               && target instanceof LivingEntity;
            flag2 = flag2 && !player.func_70051_ag();
            CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, flag2, flag2 ? 1.5F : 1.0F);
            flag2 = hitResult != null;
            if (flag2) {
               attackDamage *= hitResult.getDamageModifier();
            }

            attackDamage += enchantmentAffectsTargetBonus;
            boolean flag3 = false;
            double d0 = player.field_70140_Q - player.field_70141_P;
            if (flag && !flag2 && !flag1 && player.func_233570_aj_() && d0 < player.func_70689_ay()) {
               ItemStack itemstack = player.func_184586_b(Hand.OFF_HAND);
               if (itemstack.func_77973_b() instanceof SwordItem) {
                  flag3 = true;
               }
            }

            float f4 = 0.0F;
            boolean flag4 = false;
            int j = EnchantmentHelper.func_90036_a(player);
            if (target instanceof LivingEntity) {
               f4 = ((LivingEntity)target).func_110143_aJ();
               if (j > 0 && !target.func_70027_ad()) {
                  flag4 = true;
                  target.func_70015_d(1);
               }
            }

            Vector3d vector3d = target.func_213322_ci();
            DamageSource offhandAttack = DamageSource.func_76365_a(player);
            boolean flag5 = target.func_70097_a(offhandAttack, attackDamage);
            if (flag5) {
               if (i > 0) {
                  if (target instanceof LivingEntity) {
                     ((LivingEntity)target)
                        .func_233627_a_(
                           i * 0.5F,
                           MathHelper.func_76126_a(player.field_70177_z * (float) (Math.PI / 180.0)),
                           -MathHelper.func_76134_b(player.field_70177_z * (float) (Math.PI / 180.0))
                        );
                  } else {
                     target.func_70024_g(
                        -MathHelper.func_76126_a(player.field_70177_z * (float) (Math.PI / 180.0)) * i * 0.5F,
                        0.1,
                        MathHelper.func_76134_b(player.field_70177_z * (float) (Math.PI / 180.0)) * i * 0.5F
                     );
                  }

                  player.func_213317_d(player.func_213322_ci().func_216372_d(0.6, 1.0, 0.6));
                  player.func_70031_b(false);
               }

               if (flag3) {
                  float f3 = 1.0F + EnchantmentHelper.func_191527_a(player) * attackDamage;

                  for (LivingEntity livingentity : player.field_70170_p.func_217357_a(LivingEntity.class, target.func_174813_aQ().func_72314_b(1.0, 0.25, 1.0))) {
                     if (livingentity != player
                        && livingentity != target
                        && !player.func_184191_r(livingentity)
                        && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).func_181026_s())
                        && player.func_70068_e(livingentity) < 9.0) {
                        livingentity.func_233627_a_(
                           0.4F,
                           MathHelper.func_76126_a(player.field_70177_z * (float) (Math.PI / 180.0)),
                           -MathHelper.func_76134_b(player.field_70177_z * (float) (Math.PI / 180.0))
                        );
                        livingentity.func_70097_a(offhandAttack, f3);
                     }
                  }

                  player.field_70170_p
                     .func_184148_a(
                        null,
                        player.func_226277_ct_(),
                        player.func_226278_cu_(),
                        player.func_226281_cx_(),
                        SoundEvents.field_187730_dW,
                        player.func_184176_by(),
                        1.0F,
                        1.0F
                     );
                  player.func_184810_cG();
               }

               if (target instanceof ServerPlayerEntity && target.field_70133_I) {
                  ((ServerPlayerEntity)target).field_71135_a.func_147359_a(new SEntityVelocityPacket(target));
                  target.field_70133_I = false;
                  target.func_213317_d(vector3d);
               }

               if (flag2) {
                  player.field_70170_p
                     .func_184148_a(
                        null,
                        player.func_226277_ct_(),
                        player.func_226278_cu_(),
                        player.func_226281_cx_(),
                        SoundEvents.field_187718_dS,
                        player.func_184176_by(),
                        1.0F,
                        1.0F
                     );
                  player.func_71009_b(target);
               }

               if (!flag2 && !flag3) {
                  if (flag) {
                     player.field_70170_p
                        .func_184148_a(
                           null,
                           player.func_226277_ct_(),
                           player.func_226278_cu_(),
                           player.func_226281_cx_(),
                           SoundEvents.field_187727_dV,
                           player.func_184176_by(),
                           1.0F,
                           1.0F
                        );
                  } else {
                     player.field_70170_p
                        .func_184148_a(
                           null,
                           player.func_226277_ct_(),
                           player.func_226278_cu_(),
                           player.func_226281_cx_(),
                           SoundEvents.field_187733_dX,
                           player.func_184176_by(),
                           1.0F,
                           1.0F
                        );
                  }
               }

               if (enchantmentAffectsTargetBonus > 0.0F) {
                  player.func_71047_c(target);
               }

               player.func_130011_c(target);
               if (target instanceof LivingEntity) {
                  EnchantmentHelper.func_151384_a((LivingEntity)target, player);
               }

               EnchantmentHelper.func_151385_b(player, target);
               ItemStack itemstack1 = player.func_184592_cb();
               Entity entity = target;
               if (target instanceof EnderDragonPartEntity) {
                  entity = ((EnderDragonPartEntity)target).field_213852_b;
               }

               if (!player.field_70170_p.field_72995_K && !itemstack1.func_190926_b() && entity instanceof LivingEntity) {
                  ItemStack copy = itemstack1.func_77946_l();
                  itemstack1.func_77961_a((LivingEntity)entity, player);
                  if (itemstack1.func_190926_b()) {
                     ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.OFF_HAND);
                     player.func_184611_a(Hand.OFF_HAND, ItemStack.field_190927_a);
                  }
               }

               if (target instanceof LivingEntity) {
                  float f5 = f4 - ((LivingEntity)target).func_110143_aJ();
                  player.func_195067_a(Stats.field_188111_y, Math.round(f5 * 10.0F));
                  if (j > 0) {
                     target.func_70015_d(j * 4);
                  }

                  if (player.field_70170_p instanceof ServerWorld && f5 > 2.0F) {
                     int k = (int)(f5 * 0.5);
                     ((ServerWorld)player.field_70170_p)
                        .func_195598_a(
                           ParticleTypes.field_197615_h, target.func_226277_ct_(), target.func_226283_e_(0.5), target.func_226281_cx_(), k, 0.1, 0.0, 0.1, 0.2
                        );
                  }
               }

               player.func_71020_j(0.1F);
            } else {
               player.field_70170_p
                  .func_184148_a(
                     null,
                     player.func_226277_ct_(),
                     player.func_226278_cu_(),
                     player.func_226281_cx_(),
                     SoundEvents.field_187724_dU,
                     player.func_184176_by(),
                     1.0F,
                     1.0F
                  );
               if (flag4) {
                  target.func_70066_B();
               }
            }
         }
      }
   }
}
