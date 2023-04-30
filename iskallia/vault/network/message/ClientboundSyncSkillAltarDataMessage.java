package iskallia.vault.network.message;

import iskallia.vault.client.ClientSkillAltarData;
import iskallia.vault.world.data.SkillAltarData;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundSyncSkillAltarDataMessage {
   private final Map<UUID, List<SkillAltarData.SkillIcon>> playerAbilityIconKeys;

   public ClientboundSyncSkillAltarDataMessage(UUID playerId, List<SkillAltarData.SkillIcon> abilityIconKeys) {
      this.playerAbilityIconKeys = Map.of(playerId, abilityIconKeys);
   }

   public ClientboundSyncSkillAltarDataMessage(Map<UUID, List<SkillAltarData.SkillIcon>> playerAbilityIconKeys) {
      this.playerAbilityIconKeys = playerAbilityIconKeys;
   }

   public static void encode(ClientboundSyncSkillAltarDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.playerAbilityIconKeys.entrySet(), (buf, entry) -> {
         buf.writeUUID((UUID)entry.getKey());
         buf.writeCollection((Collection)entry.getValue(), (b, icon) -> icon.writeTo(b));
      });
   }

   public static ClientboundSyncSkillAltarDataMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundSyncSkillAltarDataMessage(buffer.readMap(FriendlyByteBuf::readUUID, buf -> buf.readList(SkillAltarData.SkillIcon::readFrom)));
   }

   public static void handle(ClientboundSyncSkillAltarDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updatePlayerAbilityIconKeys(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updatePlayerAbilityIconKeys(ClientboundSyncSkillAltarDataMessage message) {
      message.playerAbilityIconKeys.forEach(ClientSkillAltarData::setAbilityIconKeys);
   }
}
