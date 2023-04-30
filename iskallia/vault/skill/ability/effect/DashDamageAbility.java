package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class DashDamageAbility extends DashAbility {
   private static final float DAMAGE_RANGE = 2.0F;
   private static final int DAMAGE_DURATION_TICKS = 20;
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(2.0).selector(entity -> !(entity instanceof Player));
   private float attackDamagePercentPerDash;
   private DashDamageAbility.PlayerDashDamageData data;

   public DashDamageAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int extraDistance, float attackDamagePercentPerDash
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, extraDistance);
      this.attackDamagePercentPerDash = attackDamagePercentPerDash;
      this.data = null;
   }

   public DashDamageAbility() {
   }

   public float getAttackDamagePercentPerDash() {
      return this.attackDamagePercentPerDash;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         super.doAction(context);
         this.data = new DashDamageAbility.PlayerDashDamageData(20);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> {
               if (this.data != null) {
                  for (LivingEntity nearbyEntity : player.level
                     .getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, player, AABBHelper.create(player.position(), 2.0F))) {
                     UUID nearbyEntityUUID = nearbyEntity.getUUID();
                     if (!this.data.hitEntityIdSet.contains(nearbyEntityUUID)) {
                        float playerAttackDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float playerDashDamage = playerAttackDamage * this.getAttackDamagePercentPerDash();
                        nearbyEntity.hurt(DamageSource.playerAttack(player), playerDashDamage);
                        this.data.hitEntityIdSet.add(nearbyEntityUUID);
                     }
                  }

                  this.data.durationTicks--;
                  if (this.data.durationTicks <= 0) {
                     this.data = null;
                  }
               }
            }
         );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.attackDamagePercentPerDash), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.attackDamagePercentPerDash = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.attackDamagePercentPerDash)).ifPresent(tag -> nbt.put("attackDamagePercentPerDash", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.attackDamagePercentPerDash = Adapters.FLOAT.readNbt(nbt.get("attackDamagePercentPerDash")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.attackDamagePercentPerDash)).ifPresent(element -> json.add("attackDamagePercentPerDash", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.attackDamagePercentPerDash = Adapters.FLOAT.readJson(json.get("attackDamagePercentPerDash")).orElse(0.0F);
   }

   private static class PlayerDashDamageData {
      private final Set<UUID> hitEntityIdSet = new HashSet<>();
      private int durationTicks;

      private PlayerDashDamageData(int durationTicks) {
         this.durationTicks = durationTicks;
      }
   }
}
