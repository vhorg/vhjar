package iskallia.vault.gear.charm;

import com.google.common.collect.Lists;
import iskallia.vault.config.CharmConfig;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class AttributeCharm<T extends Number> extends CharmEffect<CharmEffect.Config<?>> implements GearAttributeCharm {
   private final VaultGearAttribute<T> defaultAttribute;
   private final CharmConfig.Size size;

   public AttributeCharm(CharmConfig.Size size, ResourceLocation name, VaultGearAttribute<T> attribute) {
      super(name);
      this.size = size;
      this.defaultAttribute = attribute;
   }

   public CharmConfig.Size getSize() {
      return this.size;
   }

   @Override
   public Class<CharmEffect.Config<?>> getConfigClass() {
      return MiscUtils.cast(CharmEffect.Config.class);
   }

   @Override
   public CharmEffect.Config<T> getDefaultConfig() {
      return new CharmEffect.Config<>(this.defaultAttribute, 0.0F);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(new VaultGearAttributeInstance[]{this.getConfig().toAttributeInstance()});
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes(float value) {
      return Lists.newArrayList(new VaultGearAttributeInstance[]{this.getConfig().toAttributeInstance(value)});
   }
}
