package iskallia.vault.skill.talent.type.luckyhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.LuckyHitLeechParticleMessage;
import iskallia.vault.util.PlayerLeechHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

public class HealthLeechLuckyHitTalent extends LuckyHitTalent {
   private float maxHealthPercentage;

   public HealthLeechLuckyHitTalent(int unlockLevel, int learnPointCost, int regretPointCost, float maxHealthPercentage) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.maxHealthPercentage = maxHealthPercentage;
   }

   public HealthLeechLuckyHitTalent() {
   }

   @Override
   public void onLuckyHit(LivingHurtEvent event) {
      if (!event.getEntity().level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity attacker) {
         LivingEntity attacked = event.getEntityLiving();
         PlayerLeechHelper.onLeech(attacker, attacked, event.getAmount(), this.maxHealthPercentage);
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.ALL.noArg(),
               new LuckyHitLeechParticleMessage(new Vec3(attacked.getX(), attacked.getY() + 0.15F, attacked.getZ()), attacker.getId(), 16150747, 20)
            );
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.maxHealthPercentage), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.maxHealthPercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.maxHealthPercentage)).ifPresent(tag -> nbt.put("maxHealthPercentage", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.maxHealthPercentage = Adapters.FLOAT.readNbt(nbt.get("maxHealthPercentage")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.maxHealthPercentage)).ifPresent(element -> json.add("maxHealthPercentage", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.maxHealthPercentage = Adapters.FLOAT.readJson(json.get("maxHealthPercentage")).orElseThrow();
   }
}
