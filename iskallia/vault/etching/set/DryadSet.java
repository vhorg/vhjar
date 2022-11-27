package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DryadSet extends EtchingSet<DryadSet.Config> implements GearAttributeSet {
   private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("a31d29a6-072c-46e2-b623-2740a7508afe");

   public DryadSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<DryadSet.Config> getConfigClass() {
      return DryadSet.Config.class;
   }

   public DryadSet.Config getDefaultConfig() {
      return new DryadSet.Config(20.0F);
   }

   @Override
   public void tick(ServerPlayer player) {
      super.tick(player);
      AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
      AttributeModifier mod = instance.getModifier(HEALTH_MODIFIER_ID);
      if (mod == null) {
         instance.addTransientModifier(new AttributeModifier(HEALTH_MODIFIER_ID, "Dryad Added Health", this.getConfig().getExtraHealth(), Operation.ADDITION));
      }
   }

   @Override
   public void remove(ServerPlayer player) {
      super.remove(player);
      player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_ID);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(new VaultGearAttributeInstance[]{new VaultGearAttributeInstance<>(ModGearAttributes.HEALTH, this.getConfig().getExtraHealth())});
   }

   public static class Config {
      @Expose
      private float extraHealth;

      public Config(float extraHealth) {
         this.extraHealth = extraHealth;
      }

      public float getExtraHealth() {
         return this.extraHealth;
      }
   }
}
