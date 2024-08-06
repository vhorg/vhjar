package iskallia.vault.network.message;

import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import iskallia.vault.entity.boss.trait.VaultBossTraitRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundBossSyncTraitsMessage {
   private final int entityId;
   private final List<CompoundTag> traitsNbt;

   public ClientboundBossSyncTraitsMessage(int entityId, Map<String, ITrait> traits) {
      this.entityId = entityId;
      this.traitsNbt = new ArrayList<>();
      traits.forEach((id, trait) -> {
         CompoundTag traitNbt = trait.serializeNBT();
         traitNbt.putString("Type", trait.getType());
         traitNbt.putString("Id", id);
         this.traitsNbt.add(traitNbt);
      });
   }

   private ClientboundBossSyncTraitsMessage(int entityId, List<CompoundTag> traitsNbt, boolean dummy) {
      this.entityId = entityId;
      this.traitsNbt = traitsNbt;
   }

   public static void encode(ClientboundBossSyncTraitsMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.entityId);
      buffer.writeCollection(message.traitsNbt, FriendlyByteBuf::writeNbt);
   }

   public static ClientboundBossSyncTraitsMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundBossSyncTraitsMessage(
         buffer.readInt(), (List<CompoundTag>)buffer.readCollection(size -> new ArrayList(), FriendlyByteBuf::readNbt), true
      );
   }

   public static void handle(ClientboundBossSyncTraitsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updateAffixes(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateAffixes(ClientboundBossSyncTraitsMessage message) {
      Level level = Minecraft.getInstance().level;
      if (level != null && level.getEntity(message.entityId) instanceof VaultBossEntity vaultBoss) {
         Map<String, ITrait> traits = new HashMap<>();
         message.traitsNbt.forEach(traitNbt -> VaultBossTraitRegistry.createTrait(traitNbt.getString("Type"), vaultBoss, traitNbt).ifPresent(trait -> {
            String id = traitNbt.getString("Id");
            traits.put(id, trait);
         }));
         vaultBoss.setTraits(traits);
      }
   }
}
