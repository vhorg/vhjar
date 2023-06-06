package iskallia.vault.skill.talent.type.luckyhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.LuckyHitDamageParticleMessage;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

public class DamageLuckyHitTalent extends LuckyHitTalent {
   private float damageIncrease;

   public DamageLuckyHitTalent(int unlockLevel, int learnPointCost, int regretPointCost, float damageIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.damageIncrease = damageIncrease;
   }

   public DamageLuckyHitTalent() {
   }

   @Override
   public void onLuckyHit(LivingHurtEvent event) {
      event.setAmount(event.getAmount() * (1.0F + this.damageIncrease));
      ModNetwork.CHANNEL
         .send(
            PacketDistributor.ALL.noArg(),
            new LuckyHitDamageParticleMessage(
               new Vec3(event.getEntity().position().x, event.getEntity().position().y + event.getEntity().getBbHeight() / 2.0F, event.getEntity().position().z),
               new Vec3(event.getEntity().getBbWidth() / 2.0F, event.getEntity().getBbHeight() / 2.0F, event.getEntity().getBbWidth() / 2.0F),
               event.getEntity().getId()
            )
         );
   }

   public float getDamageIncrease() {
      return this.damageIncrease;
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
