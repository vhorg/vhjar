package iskallia.vault.skill.talent.type.onhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.EntityFilterTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class DamageOnHitTalent extends EntityFilterTalent {
   private float damageIncrease;

   public DamageOnHitTalent(int unlockLevel, int learnPointCost, int regretPointCost, EntityPredicate[] filter, float damageIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost, filter);
      this.damageIncrease = damageIncrease;
   }

   public DamageOnHitTalent() {
   }

   @SubscribeEvent
   public static void onAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);
         float damageIncrease = 0.0F;

         for (DamageOnHitTalent talent : talents.getAll(DamageOnHitTalent.class, Skill::isUnlocked)) {
            if (talent.isValid(event.getEntity())) {
               damageIncrease += talent.damageIncrease;
            }
         }

         event.setAmount(event.getAmount() * (1.0F + damageIncrease));
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageIncrease = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageIncrease)).ifPresent(tag -> nbt.put("damageIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageIncrease = Adapters.FLOAT.readNbt(nbt.get("damageIncrease")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageIncrease)).ifPresent(element -> json.add("damageIncrease", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageIncrease = Adapters.FLOAT.readJson(json.get("damageIncrease")).orElseThrow();
   }
}
