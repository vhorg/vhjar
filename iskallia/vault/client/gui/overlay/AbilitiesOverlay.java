package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AbilitiesOverlay {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/abilities.png");
   public static List<AbilityNode<?>> learnedAbilities;
   public static Map<Integer, Integer> cooldowns = new HashMap<>();
   public static int focusedIndex;
   public static boolean active;

   @SubscribeEvent
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         if (learnedAbilities != null && learnedAbilities.size() != 0) {
            int previousIndex = focusedIndex - 1;
            if (previousIndex < 0) {
               previousIndex += learnedAbilities.size();
            }

            int nextIndex = focusedIndex + 1;
            if (nextIndex >= learnedAbilities.size()) {
               nextIndex -= learnedAbilities.size();
            }

            MatrixStack matrixStack = event.getMatrixStack();
            Minecraft minecraft = Minecraft.func_71410_x();
            int bottom = minecraft.func_228018_at_().func_198087_p();
            int barWidth = 62;
            int barHeight = 22;
            minecraft.func_213239_aq().func_76320_a("abilityBar");
            matrixStack.func_227860_a_();
            RenderSystem.enableBlend();
            matrixStack.func_227861_a_(10.0, bottom - barHeight, 0.0);
            minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
            minecraft.field_71456_v.func_238474_b_(matrixStack, 0, 0, 1, 13, barWidth, barHeight);
            minecraft.func_110434_K().func_110577_a(ABILITIES_RESOURCE);
            AbilityNode<?> focusedAbility = learnedAbilities.get(focusedIndex);
            SkillStyle focusedStyle = ModConfigs.ABILITIES_GUI.getStyles().get(focusedAbility.getGroup().getParentName());
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 0.4F : 1.0F);
            minecraft.field_71456_v.func_238474_b_(matrixStack, 23, 3, focusedStyle.u, focusedStyle.v, 16, 16);
            if (cooldowns.getOrDefault(focusedIndex, 0) > 0) {
               float cooldownPercent = (float)cooldowns.get(focusedIndex).intValue() / ModConfigs.ABILITIES.cooldownOf(focusedAbility, minecraft.field_71439_g);
               int cooldownHeight = (int)(16.0F * cooldownPercent);
               AbstractGui.func_238467_a_(matrixStack, 23, 3 + (16 - cooldownHeight), 39, 19, -1711276033);
               RenderSystem.enableBlend();
            }

            GlStateManager.func_227702_d_(0.7F, 0.7F, 0.7F, 0.5F);
            AbilityNode<?> previousAbility = learnedAbilities.get(previousIndex);
            if (cooldowns.getOrDefault(previousIndex, 0) > 0) {
               float cooldownPercent = (float)cooldowns.get(previousIndex).intValue()
                  / ModConfigs.ABILITIES.cooldownOf(previousAbility, minecraft.field_71439_g);
               int cooldownHeight = (int)(16.0F * cooldownPercent);
               AbstractGui.func_238467_a_(matrixStack, 43, 3 + (16 - cooldownHeight), 59, 19, -1711276033);
               RenderSystem.enableBlend();
            }

            SkillStyle previousStyle = ModConfigs.ABILITIES_GUI.getStyles().get(previousAbility.getGroup().getParentName());
            minecraft.field_71456_v.func_238474_b_(matrixStack, 43, 3, previousStyle.u, previousStyle.v, 16, 16);
            AbilityNode<?> nextAbility = learnedAbilities.get(nextIndex);
            if (cooldowns.getOrDefault(nextIndex, 0) > 0) {
               float cooldownPercent = (float)cooldowns.get(nextIndex).intValue() / ModConfigs.ABILITIES.cooldownOf(nextAbility, minecraft.field_71439_g);
               int cooldownHeight = (int)(16.0F * cooldownPercent);
               AbstractGui.func_238467_a_(matrixStack, 3, 3 + (16 - cooldownHeight), 19, 19, -1711276033);
               RenderSystem.enableBlend();
            }

            SkillStyle nextStyle = ModConfigs.ABILITIES_GUI.getStyles().get(nextAbility.getGroup().getParentName());
            minecraft.field_71456_v.func_238474_b_(matrixStack, 3, 3, nextStyle.u, nextStyle.v, 16, 16);
            minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
            GlStateManager.func_227702_d_(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.field_71456_v
               .func_238474_b_(matrixStack, 19, -1, 64 + (cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 50 : (active ? 25 : 0)), 13, 24, 24);
            matrixStack.func_227865_b_();
            minecraft.func_213239_aq().func_76319_b();
         }
      }
   }
}
