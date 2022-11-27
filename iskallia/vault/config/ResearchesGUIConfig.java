package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import java.util.HashMap;

public class ResearchesGUIConfig extends Config {
   @Expose
   private HashMap<String, SkillStyle> styles;

   @Override
   public String getName() {
      return "researches_gui_styles";
   }

   public HashMap<String, SkillStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      SkillStyle style = new SkillStyle(0, 0, VaultMod.id("gui/researches/backpacks"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Backpacks!", style);
      style = new SkillStyle(50, 0, VaultMod.id("gui/researches/waystones"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Waystones", style);
      style = new SkillStyle(100, 0, VaultMod.id("gui/researches/safety_first"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Safety First", style);
      style = new SkillStyle(150, 0, VaultMod.id("gui/researches/organisation"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Organisation", style);
      style = new SkillStyle(200, 0, VaultMod.id("gui/researches/super_builder"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Super Builder", style);
      style = new SkillStyle(0, 50, VaultMod.id("gui/researches/super_miner"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Super Miner", style);
      style = new SkillStyle(50, 50, VaultMod.id("gui/researches/storage_noob"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Storage Noob", style);
      style = new SkillStyle(100, 50, VaultMod.id("gui/researches/storage_master"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Storage Master", style);
      style = new SkillStyle(150, 50, VaultMod.id("gui/researches/storage_refined"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Storage Refined", style);
      style = new SkillStyle(200, 50, VaultMod.id("gui/researches/storage_energistic"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Storage Energistic", style);
      style = new SkillStyle(0, 100, VaultMod.id("gui/researches/storage_enthusiast"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Storage Enthusiast", style);
      style = new SkillStyle(50, 100, VaultMod.id("gui/researches/decorator"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Decorator", style);
      style = new SkillStyle(100, 100, VaultMod.id("gui/researches/decorator_pro"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Decorator Pro", style);
      style = new SkillStyle(150, 100, VaultMod.id("gui/researches/engineer"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Engineer", style);
      style = new SkillStyle(200, 100, VaultMod.id("gui/researches/super_engineer"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Super Engineer", style);
      style = new SkillStyle(0, 150, VaultMod.id("gui/researches/one_with_ender"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("One with Ender", style);
      style = new SkillStyle(50, 150, VaultMod.id("gui/researches/the_chef"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("The Chef", style);
      style = new SkillStyle(100, 150, VaultMod.id("gui/researches/traveller"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Traveller", style);
      style = new SkillStyle(150, 150, VaultMod.id("gui/researches/adventurer"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Adventurer", style);
      style = new SkillStyle(200, 150, VaultMod.id("gui/researches/hacker"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Hacker", style);
      style = new SkillStyle(0, 200, VaultMod.id("gui/researches/redstoner"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Redstoner", style);
      style = new SkillStyle(50, 200, VaultMod.id("gui/researches/natural_magical"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Natural Magical", style);
      style = new SkillStyle(100, 200, VaultMod.id("gui/researches/tech_freak"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Tech Freak", style);
      style = new SkillStyle(150, 200, VaultMod.id("gui/researches/the_emerald_king"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("The Emerald King", style);
      style = new SkillStyle(200, 200, VaultMod.id("gui/researches/quarry"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Quarry", style);
      style = new SkillStyle(0, 250, VaultMod.id("gui/researches/spaceman"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Spaceman", style);
      style = new SkillStyle(50, 250, VaultMod.id("gui/researches/total_control"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Total Control", style);
      style = new SkillStyle(100, 250, VaultMod.id("gui/researches/let_there_be_light"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Let there be light!", style);
      style = new SkillStyle(150, 250, VaultMod.id("gui/researches/energetic"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Energetic", style);
      style = new SkillStyle(200, 250, VaultMod.id("gui/researches/thermal_technician"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Thermal Technician", style);
      style = new SkillStyle(0, 300, VaultMod.id("gui/researches/plastic_technician"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Plastic Technician", style);
      style = new SkillStyle(50, 300, VaultMod.id("gui/researches/extended_possibilities"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Extended Possibilities", style);
      style = new SkillStyle(100, 300, VaultMod.id("gui/researches/power_overwhelming"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Power Overwhelming", style);
      style = new SkillStyle(150, 300, VaultMod.id("gui/researches/nuclear_power"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Nuclear Power", style);
      style = new SkillStyle(200, 300, VaultMod.id("gui/researches/automatic_genius"));
      style.frameType = SkillFrame.STAR;
      this.styles.put("Automatic Genius", style);
      style = new SkillStyle(0, 350, VaultMod.id("gui/researches/creator"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Creator", style);
      style = new SkillStyle(50, 350, VaultMod.id("gui/researches/piper"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Piper", style);
      style = new SkillStyle(100, 350, VaultMod.id("gui/researches/uber_sand"));
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put("Uber Sand", style);
      style = new SkillStyle(150, 350, VaultMod.id("gui/researches/power_manager"));
      style.frameType = SkillFrame.STAR;
      this.styles.put("Power Manager", style);
   }
}
