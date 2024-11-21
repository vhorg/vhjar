package iskallia.vault.skill.ability.effect.spi.core;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.ManaCostHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public abstract class HoldManaAbility extends HoldAbility implements IPerSecondManaAbility {
   private float manaCostPerSecond;

   public HoldManaAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks);
      this.manaCostPerSecond = manaCostPerSecond;
   }

   protected HoldManaAbility() {
   }

   @Override
   public float getManaCostPerSecond() {
      return this.manaCostPerSecond;
   }

   @Override
   protected boolean canBeginHold(SkillContext context) {
      return context.getSource().getMana().map(mana -> {
         float cost = this.getManaCostPerSecond() / 20.0F;
         if (mana instanceof ServerPlayer player) {
            if (player.isCreative()) {
               return true;
            }

            cost = ManaCostHelper.adjustManaCost(player, this, cost);
         }

         return mana.getMana() >= cost;
      }).orElse(false);
   }

   @Override
   public Ability.TickResult doActiveTick(SkillContext context) {
      return context.getSource().getMana().map(mana -> {
         float cost = this.getManaCostPerSecond() / 20.0F;
         if (mana instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) {
               return Ability.TickResult.PASS;
            }

            cost = ManaCostHelper.adjustManaCost(player, this, cost);
         }

         return mana.decreaseMana(ManaAction.PLAYER_ACTION, cost) <= 0.0F ? Ability.TickResult.COOLDOWN : Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.manaCostPerSecond), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.manaCostPerSecond = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.manaCostPerSecond)).ifPresent(tag -> nbt.put("manaCostPerSecond", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.manaCostPerSecond = Adapters.FLOAT.readNbt(nbt.get("manaCostPerSecond")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.manaCostPerSecond)).ifPresent(element -> json.add("manaCostPerSecond", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.manaCostPerSecond = Adapters.FLOAT.readJson(json.get("manaCostPerSecond")).orElse(0.0F);
   }
}
