package iskallia.vault.network.message;

import iskallia.vault.entity.champion.ChampionAffixRegistry;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.entity.champion.IChampionAffix;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundChampionMessage {
   private final int entityId;
   private final List<IChampionAffix> affixes;

   public ClientboundChampionMessage(int entityId, List<IChampionAffix> affixes) {
      this.entityId = entityId;
      this.affixes = affixes;
   }

   public static void encode(ClientboundChampionMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.entityId);
      buffer.writeCollection(message.affixes, (buf, affix) -> buf.writeNbt(affix.serialize()));
   }

   public static ClientboundChampionMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundChampionMessage(
         buffer.readInt(),
         (List<IChampionAffix>)buffer.readCollection(size -> new ArrayList(), buf -> ChampionAffixRegistry.deserialize(buf.readNbt()).orElseThrow())
      );
   }

   public static void handle(ClientboundChampionMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updateAffixes(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateAffixes(ClientboundChampionMessage message) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Entity entity = level.getEntity(message.entityId);
         if (entity instanceof ChampionLogic.IChampionLogicHolder championLogicHolder) {
            entity.getTags().add("vault_champion");
            championLogicHolder.getChampionLogic().setAffixes(message.affixes);
         }
      }
   }
}
