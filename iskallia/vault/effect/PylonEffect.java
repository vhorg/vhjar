package iskallia.vault.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class PylonEffect extends MobEffect {
   String description = null;

   public PylonEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Component getDisplayName() {
      MutableComponent component = new TextComponent("").append(super.getDisplayName());
      if (this.description != null) {
         component.append(" - " + this.description);
      }

      return component;
   }
}
