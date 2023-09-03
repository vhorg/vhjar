package iskallia.vault.item.bottle;

import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.init.ModConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public abstract class BottleEffect {
   private final String effectId;

   protected BottleEffect(String effectId) {
      this.effectId = effectId;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putString("type", this.getType());
      tag.putString("id", this.effectId);
      this.serializeData(tag);
      return tag;
   }

   public int getColor() {
      AlchemyTableConfig.CraftableEffectConfig effectConfig = ModConfigs.VAULT_ALCHEMY_TABLE.getConfig(this.effectId);
      if (effectConfig == null) {
         return -1;
      } else {
         return effectConfig.getColor() == null ? -1 : effectConfig.getColor().getValue();
      }
   }

   public Component getTooltip() {
      AlchemyTableConfig.CraftableEffectConfig effectConfig = ModConfigs.VAULT_ALCHEMY_TABLE.getConfig(this.effectId);
      return (Component)(effectConfig == null
         ? TextComponent.EMPTY
         : new TextComponent(this.getTooltipText(effectConfig.getTooltip())).withStyle(Style.EMPTY.withColor(effectConfig.getColor())));
   }

   protected abstract String getType();

   protected abstract void trigger(ServerPlayer var1);

   protected abstract CompoundTag serializeData(CompoundTag var1);

   protected abstract String getTooltipText(String var1);
}
