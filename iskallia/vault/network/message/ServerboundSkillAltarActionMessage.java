package iskallia.vault.network.message;

import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.container.SkillAltarContainer;
import iskallia.vault.world.data.SkillAltarData;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundSkillAltarActionMessage {
   private final BlockPos skillAltarPos;
   private final ServerboundSkillAltarActionMessage.Action action;
   private final int templateIndex;
   private final SkillAltarData.SkillIcon icon;
   @Nullable
   private final String serializedTemplate;

   public ServerboundSkillAltarActionMessage(BlockPos skillAltarPos, ServerboundSkillAltarActionMessage.Action action, int templateIndex) {
      this(skillAltarPos, action, templateIndex, null, null);
   }

   public ServerboundSkillAltarActionMessage(
      BlockPos skillAltarPos, ServerboundSkillAltarActionMessage.Action action, int templateIndex, String serializedTemplate
   ) {
      this(skillAltarPos, action, templateIndex, null, serializedTemplate);
   }

   public ServerboundSkillAltarActionMessage(
      BlockPos skillAltarPos, ServerboundSkillAltarActionMessage.Action action, int templateIndex, SkillAltarData.SkillIcon icon
   ) {
      this(skillAltarPos, action, templateIndex, icon, null);
   }

   public ServerboundSkillAltarActionMessage(
      BlockPos skillAltarPos,
      ServerboundSkillAltarActionMessage.Action action,
      int templateIndex,
      SkillAltarData.SkillIcon icon,
      @Nullable String serializedTemplate
   ) {
      this.skillAltarPos = skillAltarPos;
      this.action = action;
      this.templateIndex = templateIndex;
      this.icon = icon;
      this.serializedTemplate = serializedTemplate;
   }

   public static void encode(ServerboundSkillAltarActionMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.skillAltarPos);
      buffer.writeInt(message.action.ordinal());
      buffer.writeInt(message.templateIndex);
      if (message.action == ServerboundSkillAltarActionMessage.Action.UPDATE_ICON) {
         message.icon.writeTo(buffer);
      }

      if (message.action == ServerboundSkillAltarActionMessage.Action.IMPORT) {
         buffer.writeUtf(message.serializedTemplate);
      }
   }

   public static ServerboundSkillAltarActionMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      ServerboundSkillAltarActionMessage.Action action = ServerboundSkillAltarActionMessage.Action.values()[buffer.readInt()];
      if (action == ServerboundSkillAltarActionMessage.Action.IMPORT) {
         return new ServerboundSkillAltarActionMessage(pos, action, buffer.readInt(), buffer.readUtf());
      } else {
         return action == ServerboundSkillAltarActionMessage.Action.UPDATE_ICON
            ? new ServerboundSkillAltarActionMessage(pos, action, buffer.readInt(), SkillAltarData.SkillIcon.readFrom(buffer))
            : new ServerboundSkillAltarActionMessage(pos, action, buffer.readInt());
      }
   }

   public static void handle(ServerboundSkillAltarActionMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      if (context.getSender() != null) {
         context.enqueueWork(
            () -> {
               if (context.getSender().containerMenu instanceof SkillAltarContainer.Default container) {
                  switch (message.action) {
                     case SAVE:
                        container.saveTemplate();
                        break;
                     case LOAD:
                        container.setPlayerAbilitiesAndTalentsFromTemplate();
                        break;
                     case UPDATE_ICON:
                        container.updateTemplateIcon(message.icon);
                        break;
                     case SHARE:
                        container.shareInChat();
                  }
               } else if (context.getSender().containerMenu instanceof SkillAltarContainer.Import container
                  && message.action == ServerboundSkillAltarActionMessage.Action.IMPORT) {
                  container.importTemplate(message.serializedTemplate);
               }

               if (context.getSender().containerMenu instanceof SkillAltarContainer container) {
                  if (message.action == ServerboundSkillAltarActionMessage.Action.OPEN_TAB) {
                     SkillAltarBlock.openGui(message.skillAltarPos, context.getSender(), message.templateIndex, true);
                  } else if (message.action == ServerboundSkillAltarActionMessage.Action.OPEN_IMPORT) {
                     container.openImportScreen(message.templateIndex);
                  }
               }
            }
         );
         context.setPacketHandled(true);
      }
   }

   public static enum Action {
      OPEN_TAB,
      SAVE,
      LOAD,
      UPDATE_ICON,
      OPEN_IMPORT,
      IMPORT,
      SHARE;
   }
}
