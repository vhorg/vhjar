package iskallia.vault.skill.talent.type.luckyhit;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.mana.ManaPlayer;
import iskallia.vault.network.message.LuckyHitManaParticleMessage;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;

public class ManaLeechLuckyHitTalent extends LuckyHitTalent {
   private float maxManaPercentage;

   public ManaLeechLuckyHitTalent(int unlockLevel, int learnPointCost, int regretPointCost, float maxManaPercentage) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.maxManaPercentage = maxManaPercentage;
   }

   public ManaLeechLuckyHitTalent() {
   }

   @Override
   public void onLuckyHit(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ManaPlayer mana) {
         mana.increaseMana(ManaAction.PLAYER_ACTION, mana.getManaMax() * this.maxManaPercentage);
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.ALL.noArg(),
               new LuckyHitManaParticleMessage(
                  new Vec3(event.getEntity().getX(), event.getEntity().getY() + 0.15F, event.getEntity().getZ()),
                  event.getSource().getEntity().getId(),
                  65535,
                  20,
                  event.getEntity().getBbHeight()
               )
            );
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.maxManaPercentage), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.maxManaPercentage = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.maxManaPercentage)).ifPresent(tag -> nbt.put("maxManaPercentage", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.maxManaPercentage = Adapters.FLOAT.readNbt(nbt.get("maxManaPercentage")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.maxManaPercentage)).ifPresent(element -> json.add("maxManaPercentage", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.maxManaPercentage = Adapters.FLOAT.readJson(json.get("maxManaPercentage")).orElseThrow();
   }
}
