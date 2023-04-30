package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.TalentDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentGroupStyle;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentGroupWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.TalentTree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.TextComponent;

public class TalentPanRegion extends SkillPanRegion<TalentTree, TalentsElementContainerScreen, TalentWidget> {
   public TalentPanRegion(TalentDialog talentDialog, TalentsElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Talents Tab"), talentDialog);
   }

   protected void initSkillWidget(TalentTree skillTree, String skillName, SkillStyle style, Map<String, TalentWidget> widgets, List<TalentGroupWidget> groups) {
      Skill skill = skillTree.getForId(skillName).orElse(null);
      if (skill instanceof TieredSkill tiered && !(tiered.getParent() instanceof GroupedSkill)) {
         widgets.put(skillName, new TalentWidget(tiered, skillTree, style));
      } else if (skill instanceof GroupedSkill grouped) {
         int topLeftX = style.x;
         int topLeftY = style.y;
         int bottomRightX = style.x;
         int bottomRightY = style.y;

         for (LearnableSkill child : grouped.getChildren()) {
            SkillStyle offset = this.getStyles().get(child.getId());
            if (offset != null) {
               if (topLeftX > style.x + offset.x) {
                  topLeftX = style.x + offset.x;
               }

               if (topLeftY > style.y + offset.y) {
                  topLeftY = style.y + offset.y;
               }

               if (bottomRightX < style.x + offset.x) {
                  bottomRightX = style.x + offset.x;
               }

               if (bottomRightY < style.y + offset.y) {
                  bottomRightY = style.y + offset.y;
               }

               widgets.put(
                  child.getId(),
                  new TalentWidget((TieredSkill)child, skillTree, new SkillStyle(style.x + offset.x, style.y + offset.y, offset.icon, offset.frameType))
               );
            }
         }

         groups.add(
            new TalentGroupWidget(grouped, new TalentGroupStyle(grouped.getName(), topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY - topLeftY))
         );
      }
   }

   @Override
   protected HashMap<String, SkillStyle> getStyles() {
      return ModConfigs.TALENTS_GUI.getStyles();
   }
}
