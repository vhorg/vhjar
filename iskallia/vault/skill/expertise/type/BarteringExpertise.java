package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.event.ShopPedestalPriceEvent;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class BarteringExpertise extends LearnableSkill {
   private float costReduction;

   public BarteringExpertise(int unlockLevel, int learnPointCost, int regretPointCost, float costReduction) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.costReduction = costReduction;
   }

   public BarteringExpertise() {
   }

   public float getCostReduction() {
      return this.costReduction;
   }

   @SubscribeEvent
   public static void adjustPrice(ShopPedestalPriceEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
         float costReduction = 0.0F;

         for (BarteringExpertise expertise : expertises.getAll(BarteringExpertise.class, Skill::isUnlocked)) {
            costReduction += expertise.getCostReduction();
         }

         float multiplier = 1.0F - costReduction;
         ItemStack costStack = event.getCost().copy();
         costStack.setCount(Mth.floor(costStack.getCount() * multiplier));
         event.setNewCost(costStack);
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.costReduction), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.costReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.costReduction)).ifPresent(tag -> nbt.put("costReduction", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.costReduction = Adapters.FLOAT.readNbt(nbt.get("costReduction")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.costReduction)).ifPresent(element -> json.add("costReduction", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.costReduction = Adapters.FLOAT.readJson(json.get("costReduction")).orElseThrow();
   }
}
