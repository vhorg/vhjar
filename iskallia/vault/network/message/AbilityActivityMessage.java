package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.util.MiscUtils;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityActivityMessage {
   private final String ability;
   private final int cooldownTicks;
   private final int maxCooldownTicks;
   private final Ability.ActivityFlag activeFlag;

   public AbilityActivityMessage(String ability, int cooldownTicks, int maxCooldownTicks, Ability.ActivityFlag activeFlag) {
      this.ability = ability;
      this.cooldownTicks = cooldownTicks;
      this.maxCooldownTicks = maxCooldownTicks;
      this.activeFlag = activeFlag;
   }

   public String getAbility() {
      return this.ability;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   public int getMaxCooldownTicks() {
      return this.maxCooldownTicks;
   }

   public Ability.ActivityFlag getActiveFlag() {
      return this.activeFlag;
   }

   public static void encode(AbilityActivityMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.ability);
      buffer.writeInt(message.cooldownTicks);
      buffer.writeInt(message.maxCooldownTicks);
      buffer.writeInt(message.activeFlag.ordinal());
   }

   public static AbilityActivityMessage decode(FriendlyByteBuf buffer) {
      String selectedAbility = buffer.readUtf(32767);
      int cooldownTicks = buffer.readInt();
      int maxCooldownTicks = buffer.readInt();
      Ability.ActivityFlag activeFlag = MiscUtils.getEnumEntry(Ability.ActivityFlag.class, buffer.readInt());
      return new AbilityActivityMessage(selectedAbility, cooldownTicks, maxCooldownTicks, activeFlag);
   }

   public static void handle(AbilityActivityMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateActivity(message));
      context.setPacketHandled(true);
   }
}
