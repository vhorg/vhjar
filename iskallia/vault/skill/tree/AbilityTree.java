package iskallia.vault.skill.tree;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;

public class AbilityTree extends SkillTree {
   private SpecializedSkill selected;

   public SpecializedSkill getSelected() {
      return this.selected;
   }

   public Optional<Ability> getSelectedAbility() {
      return Optional.ofNullable(this.selected)
         .map(SpecializedSkill::getSpecialization)
         .filter(skill -> skill instanceof TieredSkill)
         .map(skill -> ((TieredSkill)skill).getChild())
         .filter(skill -> skill instanceof Ability)
         .map(skill -> (Ability)skill);
   }

   public void onKeyUp(SkillContext context) {
      this.getSelectedAbility().ifPresent(selected -> {
         if (selected.onKeyUp(context)) {
            this.sync(context);
         }
      });
   }

   public void onKeyDown(SkillContext context) {
      this.getSelectedAbility().ifPresent(selected -> {
         if (selected.onKeyDown(context)) {
            this.sync(context);
         }
      });
   }

   public void onCancelKeyDown(SkillContext context) {
      this.getSelectedAbility().ifPresent(selected -> {
         if (selected.onCancelKeyDown(context)) {
            this.sync(context);
         }
      });
   }

   public void onScroll(int delta, SkillContext context) {
      int index = this.selected == null ? -1 : this.skills.indexOf(this.selected);
      if (index >= 0) {
         int signum = delta >> 31 | -delta >>> 31;

         for (int offset = signum; delta != 0; offset += signum) {
            Skill skill = this.skills.get(Math.floorMod(index + offset, this.skills.size()));
            if (skill instanceof SpecializedSkill) {
               SpecializedSkill specialized = (SpecializedSkill)skill;
               if (skill.isUnlocked()) {
                  this.selected = specialized;
                  delta -= signum;
               }
            }
         }

         this.onQuickSelect(this.selected.getId(), context);
      }
   }

   public void onQuickSelect(String id, SkillContext context) {
      this.getSelectedAbility().ifPresent(selected -> selected.onBlur(context));
      this.getForId(id).ifPresent(skill -> {
         if (skill instanceof SpecializedSkill specialized) {
            this.selected = specialized;
            this.getSelectedAbility().ifPresent(ability -> ability.onFocus(context));
         }
      });
      this.sync(context);
   }

   public void specialize(String id, SkillContext context) {
      this.getForId(id).ifPresent(skill -> {
         if (skill.getParent() instanceof SpecializedSkill specialized) {
            specialized.specialize(id, context);
         }
      });
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      if (context.getSource().as(Entity.class).filter(entity -> entity.level.getGameTime() % 10L == 0L).isPresent()) {
         this.sync(context);
      }

      if (this.selected == null) {
         for (Skill skill : this.skills) {
            if (skill instanceof SpecializedSkill specialized && specialized.isUnlocked()) {
               this.selected = specialized;
               break;
            }
         }
      }
   }

   public void sync(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(player -> ModNetwork.CHANNEL.sendTo(new AbilityKnownOnesMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      other = super.mergeFrom(other, context);
      if (other instanceof AbilityTree tree && this.selected != null && this.selected.getId() != null) {
         tree.selected = (SpecializedSkill)tree.getForId(this.selected.getId()).filter(skill -> skill instanceof SpecializedSkill).orElse(null);
      }

      return other;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UTF_8.asNullable().writeBits(this.selected == null ? null : this.selected.getId(), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.selected = Adapters.UTF_8.asNullable().readBits(buffer).flatMap(this::getForId).filter(skill -> skill instanceof SpecializedSkill).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UTF_8.asNullable().writeNbt(this.selected == null ? null : this.selected.getId()).ifPresent(tag -> nbt.put("selected", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.selected = Adapters.UTF_8
         .asNullable()
         .readNbt(nbt.get("selected"))
         .flatMap(this::getForId)
         .filter(skill -> skill instanceof SpecializedSkill)
         .orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UTF_8.asNullable().writeJson(this.selected == null ? null : this.selected.getId()).ifPresent(element -> json.add("selected", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.selected = Adapters.UTF_8
         .asNullable()
         .readJson(json.get("selected"))
         .flatMap(this::getForId)
         .filter(skill -> skill instanceof SpecializedSkill)
         .orElse(null);
   }
}
