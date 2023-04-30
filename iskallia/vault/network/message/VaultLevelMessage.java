package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.ILegacySkillTreeScreen;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultLevelMessage {
   public final int vaultLevel;
   public final int vaultExp;
   public final int tnl;
   public final int unspentSkillPoints;
   public final int unspentExpertisePoints;
   public final int unspentKnowledgePoints;
   public final int unspentArchetypePoints;
   public final int unspentRegretPoints;

   public VaultLevelMessage(
      int vaultLevel,
      int vaultExp,
      int tnl,
      int unspentSkillPoints,
      int unspentExpertisePoints,
      int unspentKnowledgePoints,
      int unspentArchetypePoints,
      int unspentRegretPoints
   ) {
      this.vaultLevel = vaultLevel;
      this.vaultExp = vaultExp;
      this.tnl = tnl;
      this.unspentSkillPoints = unspentSkillPoints;
      this.unspentExpertisePoints = unspentExpertisePoints;
      this.unspentKnowledgePoints = unspentKnowledgePoints;
      this.unspentArchetypePoints = unspentArchetypePoints;
      this.unspentRegretPoints = unspentRegretPoints;
   }

   public static void encode(VaultLevelMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.vaultLevel);
      buffer.writeInt(message.vaultExp);
      buffer.writeInt(message.tnl);
      buffer.writeInt(message.unspentSkillPoints);
      buffer.writeInt(message.unspentExpertisePoints);
      buffer.writeInt(message.unspentKnowledgePoints);
      buffer.writeInt(message.unspentArchetypePoints);
      buffer.writeInt(message.unspentRegretPoints);
   }

   public static VaultLevelMessage decode(FriendlyByteBuf buffer) {
      int vaultLevel = buffer.readInt();
      int vaultExp = buffer.readInt();
      int tnl = buffer.readInt();
      int unspentSkillPoints = buffer.readInt();
      int unspentExpertisePoints = buffer.readInt();
      int unspentKnowledgePoints = buffer.readInt();
      int unspentArchetypePoints = buffer.readInt();
      int unspentRegretPoints = buffer.readInt();
      return new VaultLevelMessage(
         vaultLevel, vaultExp, tnl, unspentSkillPoints, unspentExpertisePoints, unspentKnowledgePoints, unspentArchetypePoints, unspentRegretPoints
      );
   }

   public static void handle(VaultLevelMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         vaultLevel = message.vaultLevel;
         vaultExp = message.vaultExp;
         tnl = message.tnl;
         unspentSkillPoints = message.unspentSkillPoints;
         unspentExpertisePoints = message.unspentExpertisePoints;
         unspentKnowledgePoints = message.unspentKnowledgePoints;
         unspentArchetypePoints = message.unspentArchetypePoints;
         unspentRegretPoints = message.unspentRegretPoints;
         VaultBarOverlay.expGainedAnimation.reset();
         VaultBarOverlay.expGainedAnimation.play();
         if (Minecraft.getInstance().screen instanceof ILegacySkillTreeScreen skillTreeScreen) {
            skillTreeScreen.update();
         }
      });
      context.setPacketHandled(true);
   }
}
