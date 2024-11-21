package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.RetributionParticleMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.ThornsHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ShieldBashAbility extends InstantManaAbility {
   private float damageDistance;
   private float percentageThornsDamageDealt;

   public ShieldBashAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, int manaCost, float damageRadius, float percentageThornsDamageDealt
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.damageDistance = damageRadius;
      this.percentageThornsDamageDealt = percentageThornsDamageDealt;
   }

   public ShieldBashAbility() {
   }

   public float getUnmodifiedDamageRadius() {
      return this.damageDistance;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedDamageRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
      }

      return realRadius;
   }

   public float getPercentageThornsDamageDealt() {
      return this.percentageThornsDamageDealt;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               AtomicBoolean success = new AtomicBoolean(false);
               ActiveFlags.IS_AOE_ATTACKING
                  .runIfNotSet(
                     () -> {
                        List<LivingEntity> result = new ArrayList<>();
                        EntityHelper.getEntitiesInRange(player.level, player.position(), 20.0F, EntityHelper.VAULT_TARGET_SELECTOR, result);
                        double thornsDamage = ThornsHelper.getAdditionalThornsFlatDamage(player);
                        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
                        float fullDamageForward = this.damageDistance;
                        fullDamageForward += fullDamageForward
                           * snapshot.getAttributeValue(ModGearAttributes.ON_HIT_AOE, VaultGearAttributeTypeMerger.intSum()).intValue();

                        for (LivingEntity entity : result) {
                           float percentDamage = this.getPercentDamageDealt(player, entity, this.damageDistance);
                           if (percentDamage > 0.0F) {
                              entity.hurt(DamageSource.thorns(player), (float)(thornsDamage * percentDamage));
                           }
                        }

                        ModNetwork.CHANNEL
                           .send(
                              PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                              new RetributionParticleMessage(player.position().add(-0.5, 0.5, -0.5), fullDamageForward, player.getYHeadRot())
                           );
                        player.level
                           .playSound(
                              null,
                              player.getX(),
                              player.getY(),
                              player.getZ(),
                              SoundEvents.SHIELD_BREAK,
                              SoundSource.PLAYERS,
                              1.0F,
                              0.3F + player.getRandom().nextFloat() * 0.2F
                           );
                        success.set(true);
                     }
                  );
               return success.get() ? Ability.ActionResult.successCooldownImmediate() : Ability.ActionResult.fail();
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   private float getPercentDamageDealt(Player player, LivingEntity entity, float fullDamageForward) {
      Vec3 viewVector = player.getViewVector(1.0F);
      double forwardX = viewVector.x();
      double forwardZ = viewVector.z();
      double d = Math.sqrt(forwardZ * forwardZ + forwardX * forwardX);
      forwardX /= d;
      forwardZ /= d;
      double rightX = -forwardZ;
      double relativeX = entity.getX() - player.getX();
      double relativeZ = entity.getZ() - player.getZ();
      double distForward = relativeX * forwardX + relativeZ * forwardZ - entity.getBbWidth() / 2.0;
      double distRight = relativeX * rightX + relativeZ * forwardX;
      distRight = distRight > 0.0 ? distRight - entity.getBbWidth() / 2.0 : distRight + entity.getBbWidth() / 2.0;
      if (!(distForward < 0.0) && !(distForward > fullDamageForward + 0.5) && !(distRight < -1.0) && !(distRight > 1.0)) {
         boolean isInFullDamageRectangle = 0.0 <= distForward && distForward <= fullDamageForward && -0.5 <= distRight && distRight <= 0.5;
         if (isInFullDamageRectangle) {
            return 1.0F;
         } else {
            boolean isHalfBehindForward = -0.5 <= distRight && distRight <= 0.5;
            boolean isOnTheAffectedSide = distForward <= fullDamageForward - 1.0F;
            return !isHalfBehindForward && !isOnTheAffectedSide ? 0.0F : 0.5F;
         }
      } else {
         return 0.0F;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageDistance), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentageThornsDamageDealt), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageDistance = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.percentageThornsDamageDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageDistance)).ifPresent(tag -> nbt.put("damageDistance", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentageThornsDamageDealt)).ifPresent(tag -> nbt.put("percentageThornsDamageDealt", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageDistance = Adapters.FLOAT.readNbt(nbt.get("damageDistance")).orElse(0.0F);
      this.percentageThornsDamageDealt = Adapters.FLOAT.readNbt(nbt.get("percentageThornsDamageDealt")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageDistance)).ifPresent(element -> json.add("damageDistance", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentageThornsDamageDealt)).ifPresent(element -> json.add("percentageThornsDamageDealt", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageDistance = Adapters.FLOAT.readJson(json.get("damageDistance")).orElse(0.0F);
      this.percentageThornsDamageDealt = Adapters.FLOAT.readJson(json.get("percentageThornsDamageDealt")).orElse(0.0F);
   }
}
