package iskallia.vault.skill.talent.type.luckyhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.event.PlayerActiveFlags;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.LuckyHitSweepingParticleMessage;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

public class SweepingLuckyHitTalent extends LuckyHitTalent {
   private float damagePercentage;
   private float damageRange;
   private float knockbackStrength;

   public SweepingLuckyHitTalent(int unlockLevel, int learnPointCost, int regretPointCost, float damagePercentage, float damageRange, float knockbackStrength) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.damagePercentage = damagePercentage;
      this.damageRange = damageRange;
      this.knockbackStrength = knockbackStrength;
   }

   public SweepingLuckyHitTalent() {
   }

   @Override
   public void onLuckyHit(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
         Entity attacked = event.getEntity();
         ServerLevel world = attacker.getLevel();
         ActiveFlags.IS_AOE_ATTACKING
            .runIfNotSet(
               () -> {
                  List<Mob> nearby = EntityHelper.getNearby(world, attacked.blockPosition(), this.damageRange, Mob.class);
                  nearby.remove(attacked);
                  nearby.remove(attacker);
                  nearby.removeIf(mob -> mob instanceof EternalEntity);
                  nearby.forEach(
                     mob -> {
                        Vec3 movement = mob.getDeltaMovement();
                        mob.hurt(event.getSource(), event.getAmount() * this.damagePercentage);
                        mob.setDeltaMovement(movement);
                        mob.knockback(
                           this.knockbackStrength,
                           Mth.sin(attacker.getYRot() * (float) (Math.PI / 180.0)),
                           -Mth.cos(attacker.getYRot() * (float) (Math.PI / 180.0))
                        );
                     }
                  );
                  PlayerActiveFlags.set(attacker, PlayerActiveFlags.Flag.ATTACK_AOE, 2);
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.ALL.noArg(),
                        new LuckyHitSweepingParticleMessage(
                           new Vec3(event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight() / 4.0F, event.getEntity().getZ()),
                           this.damageRange
                        )
                     );
               }
            );
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damagePercentage), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageRange), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.knockbackStrength), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damagePercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.damageRange = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damagePercentage)).ifPresent(tag -> nbt.put("damagePercentage", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageRange)).ifPresent(tag -> nbt.put("damageRange", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.knockbackStrength)).ifPresent(tag -> nbt.put("knockbackStrength", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damagePercentage = Adapters.FLOAT.readNbt(nbt.get("damagePercentage")).orElseThrow();
      this.damageRange = Adapters.FLOAT.readNbt(nbt.get("damageRange")).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readNbt(nbt.get("knockbackStrength")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damagePercentage)).ifPresent(element -> json.add("damagePercentage", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageRange)).ifPresent(element -> json.add("damageRange", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.knockbackStrength)).ifPresent(element -> json.add("knockbackStrength", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damagePercentage = Adapters.FLOAT.readJson(json.get("damagePercentage")).orElseThrow();
      this.damageRange = Adapters.FLOAT.readJson(json.get("damageRange")).orElseThrow();
      this.knockbackStrength = Adapters.FLOAT.readJson(json.get("knockbackStrength")).orElseThrow();
   }
}
