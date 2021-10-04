package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.MathHelper;

public class CryoChamberConfig extends Config {
   @Expose
   private int INFUSION_TIME;
   @Expose
   private int GROW_ETERNAL_TIME;
   @Expose
   private float UNUSED_TRADER_REWARD_CHANCE;
   @Expose
   private List<Integer> TRADERS_REQ = new ArrayList<>();
   @Expose
   private Map<String, Float> PLAYER_TRADER_REQ_MULTIPLIER = new HashMap<>();

   @Override
   public String getName() {
      return "cryo_chamber";
   }

   public int getPlayerCoreCount(String name, int createdEternals) {
      int index = MathHelper.func_76125_a(createdEternals, 0, this.TRADERS_REQ.size() - 1);
      int requiredCount = this.TRADERS_REQ.get(index);
      return MathHelper.func_76141_d(this.PLAYER_TRADER_REQ_MULTIPLIER.getOrDefault(name, 1.0F) * requiredCount);
   }

   public float getUnusedTraderRewardChance() {
      return this.UNUSED_TRADER_REWARD_CHANCE;
   }

   public int getGrowEternalTime() {
      return this.GROW_ETERNAL_TIME * 20;
   }

   public int getInfusionTime() {
      return this.INFUSION_TIME * 20;
   }

   @Override
   protected void reset() {
      this.INFUSION_TIME = 2;
      this.GROW_ETERNAL_TIME = 10;
      this.UNUSED_TRADER_REWARD_CHANCE = 0.1F;
      this.PLAYER_TRADER_REQ_MULTIPLIER.put("iskall85", 1.0F);
      this.TRADERS_REQ.add(20);
      this.TRADERS_REQ.add(40);
      this.TRADERS_REQ.add(60);
      this.TRADERS_REQ.add(80);
      this.TRADERS_REQ.add(100);
      this.TRADERS_REQ.add(100);
      this.TRADERS_REQ.add(120);
      this.TRADERS_REQ.add(120);
      this.TRADERS_REQ.add(140);
      this.TRADERS_REQ.add(140);
      this.TRADERS_REQ.add(160);
      this.TRADERS_REQ.add(160);
      this.TRADERS_REQ.add(180);
      this.TRADERS_REQ.add(180);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(200);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(250);
      this.TRADERS_REQ.add(300);
   }
}
