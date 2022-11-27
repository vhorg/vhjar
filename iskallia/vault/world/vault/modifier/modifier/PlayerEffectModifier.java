package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class PlayerEffectModifier extends VaultModifier<PlayerEffectModifier.Properties> {
   public PlayerEffectModifier(ResourceLocation id, PlayerEffectModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.effectAmplifier * s));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.GRANTED_EFFECT.register(context.getUUID(), data -> {
         if (world == data.getWorld()) {
            if (data.getFilter().test(this.properties.effect)) {
               data.getEffects().addAmplifier(this.properties.effect, this.properties.getEffectAmplifier());
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final MobEffect effect;
      @Expose
      private final int effectAmplifier;

      public Properties(MobEffect effect, int effectAmplifier) {
         this.effect = effect;
         this.effectAmplifier = effectAmplifier;
      }

      public MobEffect getEffect() {
         return this.effect;
      }

      public int getEffectAmplifier() {
         return this.effectAmplifier;
      }
   }
}
