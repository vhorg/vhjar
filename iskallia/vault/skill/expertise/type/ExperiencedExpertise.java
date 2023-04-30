package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ExperiencedExpertise extends LearnableSkill {
   private float increasedExpPercentage;

   public ExperiencedExpertise(int unlockLevel, int learnPointCost, int regretPointCost, float increasedExpPercentage) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.increasedExpPercentage = increasedExpPercentage;
   }

   public ExperiencedExpertise() {
   }

   public float getIncreasedExpPercentage() {
      return this.increasedExpPercentage;
   }

   @SubscribeEvent
   public static void onOrbPickup(PickupXp event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ExpertiseTree var7 = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
         ExperienceOrb orb = event.getOrb();
         float increase = 0.0F;

         for (ExperiencedExpertise expertise : var7.getAll(ExperiencedExpertise.class, Skill::isUnlocked)) {
            increase += expertise.getIncreasedExpPercentage();
         }

         orb.value = (int)(orb.value * (1.0F + increase));
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.increasedExpPercentage), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.increasedExpPercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.increasedExpPercentage)).ifPresent(tag -> nbt.put("increasedExpPercentage", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.increasedExpPercentage = Adapters.FLOAT.readNbt(nbt.get("increasedExpPercentage")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.increasedExpPercentage)).ifPresent(element -> json.add("increasedExpPercentage", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.increasedExpPercentage = Adapters.FLOAT.readJson(json.get("increasedExpPercentage")).orElseThrow();
   }
}
