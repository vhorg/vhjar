package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class PlayerStatTalent extends LearnableSkill {
   private PlayerStat stat;
   private float value;
   private EntityPredicate enemy;
   public static final EnumAdapter<PlayerStat> STAT = Adapters.ofEnum(PlayerStat.class, EnumAdapter.Mode.NAME);

   public PlayerStat getStat() {
      return this.stat;
   }

   public float getValue() {
      return this.value;
   }

   public EntityPredicate getEnemy() {
      return this.enemy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      STAT.writeBits(this.stat, buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.value), buffer);
      Adapters.ENTITY_PREDICATE.writeBits(this.enemy, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.stat = STAT.readBits(buffer).orElse(null);
      this.value = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.enemy = Adapters.ENTITY_PREDICATE.readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         STAT.writeNbt(this.stat).ifPresent(tag -> nbt.put("stat", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.value)).ifPresent(tag -> nbt.put("value", tag));
         Adapters.ENTITY_PREDICATE.writeNbt(this.enemy).ifPresent(tag -> nbt.put("enemy", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.stat = STAT.readNbt(nbt.get("stat")).orElse(null);
      this.value = Adapters.FLOAT.readNbt(nbt.get("value")).orElse(0.0F);
      this.enemy = Adapters.ENTITY_PREDICATE.readNbt(nbt.get("enemy")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         STAT.writeJson(this.stat).ifPresent(tag -> json.add("stat", tag));
         Adapters.FLOAT.writeJson(Float.valueOf(this.value)).ifPresent(tag -> json.add("value", tag));
         Adapters.ENTITY_PREDICATE.writeJson(this.enemy).ifPresent(tag -> json.add("enemy", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.stat = STAT.readJson(json.get("stat")).orElse(null);
      this.value = Adapters.FLOAT.readJson(json.get("value")).orElse(0.0F);
      this.enemy = Adapters.ENTITY_PREDICATE.readJson(json.get("enemy")).orElse(null);
   }

   static {
      CommonEvents.PLAYER_STAT
         .register(
            PlayerStatTalent.class,
            data -> {
               if (data.getEntity() instanceof ServerPlayer player) {
                  TalentTree talents = PlayerTalentsData.get(player.getServer()).getTalents(player);

                  for (PlayerStatTalent talent : talents.getAll(PlayerStatTalent.class, Skill::isUnlocked)) {
                     if (talent.getStat() == data.getStat()
                        && (talent.getEnemy() == null || !data.getEnemy().isEmpty() && talent.getEnemy().test((Entity)data.getEnemy().get()))) {
                        data.setValue(data.getValue() + talent.getValue());
                     }
                  }
               }
            }
         );
   }
}
