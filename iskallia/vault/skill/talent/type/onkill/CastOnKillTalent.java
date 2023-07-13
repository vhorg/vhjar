package iskallia.vault.skill.talent.type.onkill;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.mana.FullManaPlayer;
import iskallia.vault.skill.ability.effect.spi.core.InstantAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CastOnKillTalent extends OnKillTalent {
   private String ability;
   private float probability;

   public CastOnKillTalent(int unlockLevel, int learnPointCost, int regretPointCost, EntityPredicate[] filter, String ability, float probability) {
      super(unlockLevel, learnPointCost, regretPointCost, filter);
      this.ability = ability;
      this.probability = probability;
   }

   public CastOnKillTalent() {
   }

   @Override
   public void onDeath(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         if (this.isValid(event.getEntity())) {
            if (player.getLevel().getRandom().nextFloat() >= this.probability) {
               return;
            }

            AbilityTree tree = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);
            this.resolve(tree, player)
               .ifPresent(
                  ability -> ability.onAction(
                     SkillContext.of(player, SkillSource.of(player).setPos(event.getEntity().position()).setMana(FullManaPlayer.INSTANCE))
                  )
               );
         }
      }
   }

   public Optional<InstantAbility> resolve(AbilityTree tree, Player player) {
      return tree.getForId(this.ability).map(skill -> {
         Skill var2x = skill instanceof SpecializedSkill specialized ? specialized.getSpecialization() : skill;
         var2x = var2x instanceof TieredSkill tiered ? tiered.getChild() : var2x;
         return (InstantAbility)(var2x instanceof InstantAbility ? var2x : null);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UTF_8.writeBits(this.ability, buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.probability), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.ability = Adapters.UTF_8.readBits(buffer).orElseThrow();
      this.probability = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UTF_8.writeNbt(this.ability).ifPresent(tag -> nbt.put("ability", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.probability)).ifPresent(tag -> nbt.put("probability", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.ability = Adapters.UTF_8.readNbt(nbt.get("ability")).orElseThrow();
      this.probability = Adapters.FLOAT.readNbt(nbt.get("probability")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UTF_8.writeJson(this.ability).ifPresent(element -> json.add("ability", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.probability)).ifPresent(element -> json.add("probability", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.ability = Adapters.UTF_8.readJson(json.get("ability")).orElseThrow();
      this.probability = Adapters.FLOAT.readJson(json.get("probability")).orElseThrow();
   }
}
