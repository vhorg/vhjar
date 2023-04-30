package iskallia.vault.skill.talent.type.mana;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;

public class LowManaDamageTalent extends LowManaTalent implements TickingSkill {
   private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("ed8528f4-cd3c-4aff-90b2-5471f742d4bd");
   private float damageIncrease;

   public LowManaDamageTalent(int unlockLevel, int learnPointCost, int regretPointCost, MobEffect effect, float manaThreshold, float damageIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost, effect, manaThreshold);
      this.damageIncrease = damageIncrease;
   }

   public LowManaDamageTalent() {
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::removeExistingDamageBuff);
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      if (this.isUnlocked()) {
         context.getSource()
            .as(ServerPlayer.class)
            .ifPresent(
               player -> {
                  if (!this.shouldGetBenefits(player)) {
                     this.removeExistingDamageBuff(player);
                  } else {
                     PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
                     if (existing != null && !Mth.equal(existing.getMultiplier(), this.damageIncrease)) {
                        PlayerDamageHelper.removeMultiplier(player, existing);
                        existing = null;
                     }

                     if (existing == null) {
                        PlayerDamageHelper.applyTimedMultiplier(
                           DAMAGE_MULTIPLIER_ID, player, this.damageIncrease, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY, true, 20
                        );
                     } else {
                        existing.refreshDuration(player.getServer());
                     }
                  }
               }
            );
      }
   }

   private void removeExistingDamageBuff(ServerPlayer player) {
      PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageIncrease = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageIncrease)).ifPresent(tag -> nbt.put("damageIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageIncrease = Adapters.FLOAT.readNbt(nbt.get("damageIncrease")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageIncrease)).ifPresent(element -> json.add("damageIncrease", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageIncrease = Adapters.FLOAT.readJson(json.get("damageIncrease")).orElseThrow();
   }
}
