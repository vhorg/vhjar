package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class PlayerEffectModifier extends VaultModifier<PlayerEffectModifier.Properties> {
   public PlayerEffectModifier(ResourceLocation id, PlayerEffectModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.getEffectAmplifier() * s));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.GRANTED_EFFECT.register(context.getUUID(), data -> {
         if (world == data.getWorld()) {
            if (!context.hasTarget() || context.getTarget().equals(data.getPlayer().getUUID())) {
               if (data.getFilter().test(this.properties.effect)) {
                  data.getEffects().addAmplifier(this.properties.effect, this.properties.getEffectAmplifier(context));
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
      private final ScalarReputationProperty reputation;

      public Properties(MobEffect effect, int effectAmplifier, ScalarReputationProperty reputation) {
         this.effect = effect;
         this.effectAmplifier = effectAmplifier;
         this.reputation = reputation;
      }

      public MobEffect getEffect() {
         return this.effect;
      }

      public int getEffectAmplifier() {
         return this.effectAmplifier;
      }

      public int getEffectAmplifier(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.effectAmplifier, context) : this.effectAmplifier;
      }
   }
}
