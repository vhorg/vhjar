package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;

public class TalentsGUIConfig extends Config {
   @Expose
   private HashMap<String, SkillStyle> styles;

   @Override
   public String getName() {
      return "talents_gui_styles";
   }

   public HashMap<String, SkillStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      SkillStyle style = new SkillStyle(0, 0, VaultMod.id("gui/talents/haste"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.HASTE.getParentName(), style);
      style = new SkillStyle(70, 0, VaultMod.id("gui/talents/last_stand"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.LAST_STAND.getParentName(), style);
      style = new SkillStyle(140, 0, VaultMod.id("gui/talents/berserking"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.BERSERKING.getParentName(), style);
      style = new SkillStyle(280, 0, VaultMod.id("gui/talents/strength"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.STRENGTH.getParentName(), style);
      style = new SkillStyle(0, 70, VaultMod.id("gui/talents/speed"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.SPEED.getParentName(), style);
      style = new SkillStyle(350, 70, VaultMod.id("gui/talents/angel"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.ANGEL.getParentName(), style);
      style = new SkillStyle(0, 140, VaultMod.id("gui/talents/reach"));
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.REACH.getParentName(), style);
      style = new SkillStyle(70, 140, VaultMod.id("gui/talents/experienced"));
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.EXPERIENCED.getParentName(), style);
      style = new SkillStyle(210, 140, VaultMod.id("gui/talents/stone_skin"));
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.STONE_SKIN.getParentName(), style);
      style = new SkillStyle(280, 140, VaultMod.id("gui/talents/unbreakable"));
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.UNBREAKABLE.getParentName(), style);
      style = new SkillStyle(350, 140, VaultMod.id("gui/talents/critical_strike"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.CRITICAL_STRIKE.getParentName(), style);
      style = new SkillStyle(350, 280, VaultMod.id("gui/talents/lucky_altar"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.LUCKY_ALTAR.getParentName(), style);
      style = new SkillStyle(70, 350, VaultMod.id("gui/talents/fatal_strike_chance"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.FATAL_STRIKE_CHANCE.getParentName(), style);
      style = new SkillStyle(140, 350, VaultMod.id("gui/talents/fatal_strike_chance"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.FATAL_STRIKE_DAMAGE.getParentName(), style);
      style = new SkillStyle(280, 350, VaultMod.id("gui/talents/thorns_chance"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.THORNS_CHANCE.getParentName(), style);
      style = new SkillStyle(350, 350, VaultMod.id("gui/talents/thorns_damage"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.THORNS_DAMAGE.getParentName(), style);
      style = new SkillStyle(0, 420, VaultMod.id("gui/talents/weakness_affinity"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.WEAKNESS_AFFINITY.getParentName(), style);
      style = new SkillStyle(70, 420, VaultMod.id("gui/talents/wither_affinity"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.WITHER_AFFINITY.getParentName(), style);
      style = new SkillStyle(140, 420, VaultMod.id("gui/talents/slowness_affinity"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.SLOWNESS_AFFINITY.getParentName(), style);
      style = new SkillStyle(210, 420, VaultMod.id("gui/talents/bartering"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.BARTERING.getParentName(), style);
      style = new SkillStyle(280, 420, VaultMod.id("gui/talents/blacksmithing"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.BLACKSMITH.getParentName(), style);
   }
}
