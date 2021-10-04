package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.util.MiscUtils;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilityActivityMessage {
   private final String selectedAbility;
   private final int cooldownTicks;
   private final int maxCooldownTicks;
   private final AbilityTree.ActivityFlag activeFlag;

   public AbilityActivityMessage(AbilityGroup<?, ?> ability, int cooldownTicks, int maxCooldownTicks, AbilityTree.ActivityFlag activeFlag) {
      this(ability.getParentName(), cooldownTicks, maxCooldownTicks, activeFlag);
   }

   private AbilityActivityMessage(String selectedAbility, int cooldownTicks, int maxCooldownTicks, AbilityTree.ActivityFlag activeFlag) {
      this.selectedAbility = selectedAbility;
      this.cooldownTicks = cooldownTicks;
      this.maxCooldownTicks = maxCooldownTicks;
      this.activeFlag = activeFlag;
   }

   public String getSelectedAbility() {
      return this.selectedAbility;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   public int getMaxCooldownTicks() {
      return this.maxCooldownTicks;
   }

   public AbilityTree.ActivityFlag getActiveFlag() {
      return this.activeFlag;
   }

   public static void encode(AbilityActivityMessage message, PacketBuffer buffer) {
      buffer.func_180714_a(message.selectedAbility);
      buffer.writeInt(message.cooldownTicks);
      buffer.writeInt(message.maxCooldownTicks);
      buffer.writeInt(message.activeFlag.ordinal());
   }

   public static AbilityActivityMessage decode(PacketBuffer buffer) {
      String selectedAbility = buffer.func_150789_c(32767);
      int cooldownTicks = buffer.readInt();
      int maxCooldownTicks = buffer.readInt();
      AbilityTree.ActivityFlag activeFlag = MiscUtils.getEnumEntry(AbilityTree.ActivityFlag.class, buffer.readInt());
      return new AbilityActivityMessage(selectedAbility, cooldownTicks, maxCooldownTicks, activeFlag);
   }

   public static void handle(AbilityActivityMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateActivity(message));
      context.setPacketHandled(true);
   }
}
