package iskallia.vault.skill.ability.effect.spi.core;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.ManaCostHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public abstract class InstantManaAbility extends InstantAbility implements IInstantManaAbility {
   private float manaCost;

   public InstantManaAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
      this.manaCost = manaCost;
   }

   protected InstantManaAbility() {
   }

   @Override
   public float getManaCost() {
      return this.manaCost;
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().getMana().map(mana -> {
         float cost = this.manaCost;
         if (mana instanceof ServerPlayer player) {
            if (player.isCreative()) {
               return true;
            }

            cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), cost);
            if (mana.getMana() < cost) {
               player.level.playSound((Player)null, player, ModSounds.ABILITY_OUT_OF_MANA, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
         }

         return mana.getMana() >= cost;
      }).orElse(false);
   }

   @Override
   protected void doActionPost(SkillContext context) {
      context.getSource().getMana().ifPresent(mana -> {
         float cost = this.manaCost;
         if (mana instanceof ServerPlayer player) {
            if (player.isCreative()) {
               return;
            }

            cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), cost);
         }

         mana.decreaseMana(cost);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.manaCost), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.manaCost = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.manaCost)).ifPresent(tag -> nbt.put("manaCost", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.manaCost = Adapters.FLOAT.readNbt(nbt.get("manaCost")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.manaCost)).ifPresent(element -> json.add("manaCost", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.manaCost = Adapters.FLOAT.readJson(json.get("manaCost")).orElse(0.0F);
   }
}
