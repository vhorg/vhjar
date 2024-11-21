package iskallia.vault.entity.entity.elite;

import iskallia.vault.entity.champion.PotionAuraAffix;
import iskallia.vault.init.ModEffects;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EliteWitchEntity extends Witch {
   private final PotionAuraAffix manaStealAura = new PotionAuraAffix("Mana Steal Aura", ModEffects.MANA_STEAL, 20, 1, 5, PotionAuraAffix.Target.PLAYER);
   private final PotionAuraAffix poisonAura = new PotionAuraAffix("Poison Aura", MobEffects.POISON, 20, 1, 8, PotionAuraAffix.Target.PLAYER);
   private final List<PotionAuraAffix> auras = List.of(this.manaStealAura, this.poisonAura);

   public EliteWitchEntity(EntityType<? extends Witch> entityType, Level level) {
      super(entityType, level);
   }

   public void tick() {
      super.tick();
      this.auras.forEach(aura -> aura.tick(this));
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }

   public List<PotionAuraAffix> getAuras() {
      return this.auras;
   }

   public void performRangedAttack(LivingEntity target, float pDistanceFactor) {
      if (!this.isDrinkingPotion()) {
         Vec3 vec3 = target.getDeltaMovement();
         double xDiff = target.getX() + vec3.x - this.getX();
         double yDiff = target.getEyeY() - 1.1F - this.getY();
         double zDiff = target.getZ() + vec3.z - this.getZ();
         double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
         List<MobEffectInstance> mobEffects = List.of(new MobEffectInstance(MobEffects.HARM, 1, Math.max(1, (int)(target.getMaxHealth() / 20.0F))));
         if (target instanceof Raider) {
            if (target.getHealth() <= 4.0F) {
               mobEffects = Potions.HEALING.getEffects();
            } else {
               mobEffects = Potions.REGENERATION.getEffects();
            }

            this.setTarget(null);
         } else if (this.random.nextFloat() < 0.5F && !target.hasEffect(MobEffects.BLINDNESS)) {
            mobEffects = List.of(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
         }

         ThrownPotion thrownpotion = new ThrownPotion(this.level, this);
         ItemStack potionItem = PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), mobEffects);
         potionItem.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(mobEffects));
         thrownpotion.setItem(potionItem);
         thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
         thrownpotion.shoot(xDiff, yDiff + distance * 0.2, zDiff, 0.75F, 8.0F);
         if (!this.isSilent()) {
            this.level
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
               );
         }

         this.level.addFreshEntity(thrownpotion);
      }
   }
}
