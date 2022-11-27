package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.calc.GrantedEffectHelper;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MobCurseOnHitModifier extends VaultModifier<MobCurseOnHitModifier.Properties> {
   public MobCurseOnHitModifier(ResourceLocation id, MobCurseOnHitModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.onHitApplyChance * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_DAMAGE.register(context.getUUID(), event -> {
         if (event.getEntityLiving() instanceof ServerPlayer player) {
            if (event.getSource().getEntity() instanceof LivingEntity) {
               if (vault.get(Vault.LISTENERS).contains(player.getUUID())) {
                  if (!(world.random.nextFloat() >= this.properties.onHitApplyChance)) {
                     MobEffect effect = this.properties.getEffect();
                     if (!GrantedEffectHelper.hasImmunity(player, effect)) {
                        int grantedAmplifier = GrantedEffectHelper.getEffectData(player, player.getLevel(), effect);
                        int amplifier = grantedAmplifier + this.properties.effectAmplifier + 1;
                        player.addEffect(new MobEffectInstance(effect, this.properties.effectDurationTicks, amplifier, true, false));
                     }
                  }
               }
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final MobEffect effect;
      @Expose
      private final int effectAmplifier;
      @Expose
      private final int effectDurationTicks;
      @Expose
      private final float onHitApplyChance;

      public Properties(MobEffect effect, int effectAmplifier, int effectDurationTicks, float onHitApplyChance) {
         this.effect = effect;
         this.effectAmplifier = effectAmplifier;
         this.effectDurationTicks = effectDurationTicks;
         this.onHitApplyChance = onHitApplyChance;
      }

      public MobEffect getEffect() {
         return this.effect;
      }

      public int getEffectAmplifier() {
         return this.effectAmplifier;
      }

      public int getEffectDurationTicks() {
         return this.effectDurationTicks;
      }

      public float getOnHitApplyChance() {
         return this.onHitApplyChance;
      }
   }
}
