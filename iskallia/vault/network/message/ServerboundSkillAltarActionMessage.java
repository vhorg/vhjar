package iskallia.vault.network.message;

import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.container.SkillAltarContainer;
import iskallia.vault.world.data.SkillAltarData;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundSkillAltarActionMessage {
   private final BlockPos skillAltarPos;
   private final ServerboundSkillAltarActionMessage.Action action;
   private final int templateIndex;
   private SkillAltarData.SkillIcon icon;
   private Boolean isTalentIcon;

   public ServerboundSkillAltarActionMessage(BlockPos skillAltarPos, ServerboundSkillAltarActionMessage.Action action, int templateIndex) {
      this(skillAltarPos, action, templateIndex, null);
   }

   public ServerboundSkillAltarActionMessage(
      BlockPos skillAltarPos, ServerboundSkillAltarActionMessage.Action action, int templateIndex, SkillAltarData.SkillIcon icon
   ) {
      this.skillAltarPos = skillAltarPos;
      this.action = action;
      this.templateIndex = templateIndex;
      this.icon = icon;
   }

   public static void encode(ServerboundSkillAltarActionMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.skillAltarPos);
      buffer.writeInt(message.action.ordinal());
      buffer.writeInt(message.templateIndex);
      if (message.action == ServerboundSkillAltarActionMessage.Action.UPDATE_ICON) {
         message.icon.writeTo(buffer);
      }
   }

   public static ServerboundSkillAltarActionMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      ServerboundSkillAltarActionMessage.Action action = ServerboundSkillAltarActionMessage.Action.values()[buffer.readInt()];
      return action == ServerboundSkillAltarActionMessage.Action.UPDATE_ICON
         ? new ServerboundSkillAltarActionMessage(pos, action, buffer.readInt(), SkillAltarData.SkillIcon.readFrom(buffer))
         : new ServerboundSkillAltarActionMessage(pos, action, buffer.readInt());
   }

   public static void handle(ServerboundSkillAltarActionMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      if (context.getSender() != null) {
         context.enqueueWork(() -> {
            if (context.getSender().containerMenu instanceof SkillAltarContainer container) {
               switch (message.action) {
                  case OPEN_TAB:
                     SkillAltarBlock.openGui(message.skillAltarPos, context.getSender(), message.templateIndex);
                     break;
                  case SAVE:
                     container.saveTemplate();
                     break;
                  case LOAD:
                     container.setPlayerAbilitiesAndTalentsFromTemplate();
                     break;
                  case UPDATE_ICON:
                     container.updateTemplateIcon(message.icon);
               }
            }
         });
         context.setPacketHandled(true);
      }
   }

   public static enum Action {
      OPEN_TAB,
      SAVE,
      LOAD,
      UPDATE_ICON;
   }
}
