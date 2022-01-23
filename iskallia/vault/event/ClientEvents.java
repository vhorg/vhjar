package iskallia.vault.event;

import com.google.common.collect.Lists;
import iskallia.vault.Vault;
import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.client.ClientDamageData;
import iskallia.vault.client.ClientTalentData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.type.PlayerTalent;
import iskallia.vault.skill.talent.type.archetype.FrenzyTalent;
import iskallia.vault.util.PlayerRageHelper;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientEvents {
   private static final ResourceLocation OVERLAY_ICONS = Vault.id("textures/gui/overlay_icons.png");

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupHealthTexture(Pre event) {
      if (event.getType() == ElementType.HEALTH) {
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         if (player != null) {
            TalentNode<?> talentNode = ClientTalentData.getLearnedTalentNode(ModConfigs.TALENTS.FRENZY);
            if (talentNode != null && talentNode.isLearned()) {
               PlayerTalent talent = talentNode.getTalent();
               if (talent instanceof FrenzyTalent) {
                  if (player.func_110143_aJ() / player.func_110138_aP() <= ((FrenzyTalent)talent).getThreshold()) {
                     Minecraft.func_71410_x().func_110434_K().func_110577_a(OVERLAY_ICONS);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void cleanupHealthTexture(Post event) {
      if (event.getType() == ElementType.HEALTH) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
      }
   }

   @SubscribeEvent
   public static void onDisconnect(LoggedOutEvent event) {
      PlayerRageHelper.clearClientCache();
      ClientActiveEternalData.clearClientCache();
      ClientDamageData.clearClientCache();
   }

   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ModConfigs.TOOLTIP.getTooltipString(event.getItemStack().func_77973_b()).ifPresent(str -> {
         List<ITextComponent> tooltip = event.getToolTip();
         List<String> added = Lists.reverse(Lists.newArrayList(str.split("\n")));
         if (!added.isEmpty()) {
            tooltip.add(1, StringTextComponent.field_240750_d_);

            for (String newStr : added) {
               tooltip.add(1, new StringTextComponent(newStr).func_240699_a_(TextFormatting.GRAY));
            }
         }
      });
   }
}
