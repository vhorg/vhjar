package iskallia.vault.network.message;

import iskallia.vault.skill.ability.cooldown.AbilityCooldownManager;
import iskallia.vault.skill.ability.cooldown.CooldownInstance;
import iskallia.vault.util.NetcodeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityCooldownMessage {
   private final Map<String, CooldownInstance> cooldowns;

   public AbilityCooldownMessage(Map<String, CooldownInstance> cooldowns) {
      this.cooldowns = cooldowns;
   }

   public static void encode(AbilityCooldownMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.cooldowns.size());
      message.cooldowns.forEach((abilityName, cooldownInstance) -> {
         NetcodeUtils.writeString(buffer, abilityName);
         cooldownInstance.write(buffer);
      });
   }

   public static AbilityCooldownMessage decode(FriendlyByteBuf buffer) {
      Map<String, CooldownInstance> cooldowns = new HashMap<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         String abilityName = NetcodeUtils.readString(buffer);
         CooldownInstance cooldownInstance = CooldownInstance.read(buffer);
         cooldowns.put(abilityName, cooldownInstance);
      }

      return new AbilityCooldownMessage(cooldowns);
   }

   public static void handle(AbilityCooldownMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> AbilityCooldownManager.updateClientCooldown(message.cooldowns));
      context.setPacketHandled(true);
   }
}
