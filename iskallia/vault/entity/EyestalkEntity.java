package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class EyestalkEntity extends VexEntity {
   public VMapNBT<UUID, EyestalkEntity.TargetData> targetCooldown = VMapNBT.ofUUID(EyestalkEntity.TargetData::new);
   public UUID currentTarget = null;
   public UUID mother = null;

   public EyestalkEntity(EntityType<? extends VexEntity> p_i50190_1_, World p_i50190_2_) {
      super(p_i50190_1_, p_i50190_2_);
   }

   protected void func_184651_r() {
   }

   public void func_213281_b(CompoundNBT nbt) {
      super.func_213281_b(nbt);
      if (this.currentTarget != null) {
         nbt.func_74778_a("CurrentTarget", this.currentTarget.toString());
      }

      if (this.mother != null) {
         nbt.func_74778_a("Mother", this.mother.toString());
      }

      nbt.func_218657_a("Cooldowns", this.targetCooldown.serializeNBT());
   }

   public void func_70037_a(CompoundNBT nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_150297_b("CurrentTarget", 8)) {
         this.currentTarget = UUID.fromString(nbt.func_74779_i("CurrentTarget"));
      }

      if (nbt.func_150297_b("Mother", 8)) {
         this.mother = UUID.fromString(nbt.func_74779_i("Mother"));
      }

      this.targetCooldown.deserializeNBT(nbt.func_150295_c("Cooldowns", 10));
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         if (this.currentTarget == null || this.currentTarget.equals(this.mother)) {
            VaultRaid vault = VaultRaidData.get((ServerWorld)this.field_70170_p).getAt((ServerWorld)this.field_70170_p, this.func_233580_cy_());
            List<UUID> players = new ArrayList<>();
            if (vault != null) {
               players = vault.getPlayers().stream().filter(player -> player instanceof VaultRunner).map(VaultPlayer::getPlayerId).collect(Collectors.toList());
               players.removeAll(this.targetCooldown.entrySet().stream().filter(e -> e.getValue().cooldown > 0).map(Entry::getKey).collect(Collectors.toList()));
            }

            if (players.isEmpty()) {
               this.currentTarget = this.mother;
            } else {
               this.currentTarget = players.get(this.field_70146_Z.nextInt(players.size()));
            }
         }

         if (this.currentTarget != null) {
            Entity entity = ((ServerWorld)this.field_70170_p).func_217461_a(this.currentTarget);
            if (entity != null) {
               if (entity instanceof LivingEntity) {
                  LivingEntity living = (LivingEntity)entity;
                  this.func_70624_b(living.func_110124_au().equals(this.mother) ? null : living);
                  Vector3d vector3d = living.func_174824_e(1.0F);
                  this.field_70765_h.func_75642_a(vector3d.field_72450_a, vector3d.field_72448_b, vector3d.field_72449_c, 1.0);
               }

               if (this.func_174813_aQ().func_72326_a(entity.func_174813_aQ()) && !entity.func_110124_au().equals(this.mother)) {
                  this.func_70652_k(entity);
               }

               if (entity.func_175149_v()) {
                  this.currentTarget = null;
               }
            } else {
               this.currentTarget = null;
            }
         }

         this.targetCooldown.values().forEach(targetData -> targetData.cooldown--);
      }
   }

   public boolean func_70652_k(Entity entity) {
      float f = ModConfigs.EYESORE.eyeStalk.getDamage(this);
      float f1 = ModConfigs.EYESORE.eyeStalk.knockback;
      if (entity instanceof LivingEntity) {
         f += EnchantmentHelper.func_152377_a(this.func_184614_ca(), ((LivingEntity)entity).func_70668_bt());
         f1 += EnchantmentHelper.func_77501_a(this);
      }

      int i = EnchantmentHelper.func_90036_a(this);
      if (i > 0) {
         entity.func_70015_d(i * 4);
      }

      boolean flag = entity.func_70097_a(DamageSource.func_76358_a(this), f);
      if (flag) {
         if (f1 > 0.0F && entity instanceof LivingEntity) {
            this.applyKnockback(
               entity,
               f1 * 0.5F,
               MathHelper.func_76126_a(this.field_70177_z * (float) (Math.PI / 180.0)),
               -MathHelper.func_76134_b(this.field_70177_z * (float) (Math.PI / 180.0))
            );
            this.func_213317_d(this.func_213322_ci().func_216372_d(0.6, 1.0, 0.6));
         }

         this.func_174815_a(this, entity);
         this.func_130011_c(entity);
      }

      return flag;
   }

   public void applyKnockback(Entity target, float strength, double ratioX, double ratioZ) {
      if (strength > 0.0F) {
         target.field_70160_al = true;
         Vector3d vector3d = target.func_213322_ci();
         Vector3d vector3d1 = new Vector3d(ratioX, 0.0, ratioZ).func_72432_b().func_186678_a(strength);
         target.func_213293_j(
            vector3d.field_72450_a / 2.0 - vector3d1.field_72450_a,
            this.field_70122_E ? Math.min(0.4, vector3d.field_72448_b / 2.0 + strength) : vector3d.field_72448_b,
            vector3d.field_72449_c / 2.0 - vector3d1.field_72449_c
         );
      }
   }

   protected void func_70665_d(DamageSource source, float damageAmount) {
      Entity direct = source.func_76364_f();
      Entity indirect = source.func_76346_g();
      if (direct != null && direct.func_110124_au().equals(this.currentTarget) || indirect != null && indirect.func_110124_au().equals(this.currentTarget)) {
         this.targetCooldown.computeIfAbsent(this.currentTarget, id -> new EyestalkEntity.TargetData()).cooldown = 600;
         this.currentTarget = null;
      }
   }

   public static MutableAttribute getAttributes() {
      return MonsterEntity.func_234295_eP_()
         .func_233815_a_(Attributes.field_233819_b_, 100.0)
         .func_233815_a_(Attributes.field_233821_d_, 0.25)
         .func_233815_a_(Attributes.field_233823_f_, 3.0)
         .func_233815_a_(Attributes.field_233824_g_, 3.0)
         .func_233815_a_(Attributes.field_233820_c_, 0.4)
         .func_233815_a_(Attributes.field_233826_i_, 2.0);
   }

   public static class TargetData implements INBTSerializable<CompoundNBT> {
      public int cooldown;

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74768_a("Cooldown", this.cooldown);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.cooldown = nbt.func_74762_e("Cooldown");
      }
   }
}
