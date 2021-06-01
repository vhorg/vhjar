package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AttackOffHandMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class VaultDaggerItem extends SwordItem implements VaultGear<VaultDaggerItem> {
   public VaultDaggerItem(ResourceLocation id, Properties builder) {
      super(VaultGear.Tier.INSTANCE, 0, -2.4F, builder);
      this.setRegistryName(id);
   }

   @Override
   public int getModelsFor(VaultGear.Rarity rarity) {
      return rarity == VaultGear.Rarity.SCRAPPY ? 1 : 1;
   }

   public void attackOffHand() {
      Minecraft mc = Minecraft.func_71410_x();
      if (Minecraft.func_71410_x().field_71441_e != null
         && Minecraft.func_71410_x().field_71462_r == null
         && !Minecraft.func_71410_x().func_147113_T()
         && mc.field_71439_g != null
         && !mc.field_71439_g.func_184585_cz()) {
         RayTraceResult rayTrace = getEntityMouseOverExtended(6.0F);
         if (rayTrace instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityRayTrace = (EntityRayTraceResult)rayTrace;
            Entity entityHit = entityRayTrace.func_216348_a();
            if (entityHit != mc.field_71439_g && entityHit != mc.field_71439_g.func_184187_bx()) {
               ModNetwork.CHANNEL
                  .sendTo(new AttackOffHandMessage(entityHit.func_145782_y()), mc.field_71439_g.field_71174_a.func_147298_b(), NetworkDirection.PLAY_TO_SERVER);
            }
         }
      }
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
      return this.getAttributeModifiers(this, slot, stack, super.getAttributeModifiers(slot, stack));
   }

   public boolean isDamageable(ItemStack stack) {
      return this.isDamageable(this, stack);
   }

   public int getMaxDamage(ItemStack stack) {
      return this.getMaxDamage(this, stack, super.getMaxDamage(stack));
   }

   public ITextComponent func_200295_i(ItemStack itemStack) {
      return this.getDisplayName(this, itemStack, super.func_200295_i(itemStack));
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      return this.onItemRightClick(this, world, player, hand, super.func_77659_a(world, player, hand));
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
   }

   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      this.addInformation(this, stack, world, tooltip, flag);
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return this.canElytraFly(this, stack, entity);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.elytraFlightTick(this, stack, entity, flightTicks);
   }

   private static RayTraceResult getEntityMouseOverExtended(float reach) {
      RayTraceResult result = null;
      Minecraft mc = Minecraft.func_71410_x();
      Entity viewEntity = mc.field_175622_Z;
      if (viewEntity != null && mc.field_71441_e != null) {
         double reachDistance = reach;
         RayTraceResult rayTrace = viewEntity.func_213324_a(reachDistance, 0.0F, false);
         Vector3d eyePos = viewEntity.func_174824_e(0.0F);
         boolean hasExtendedReach = false;
         if (mc.field_71442_b != null) {
            if (mc.field_71442_b.func_78749_i() && reachDistance < 6.0) {
               double attackReach = 6.0;
               reachDistance = attackReach;
            } else if (reachDistance > reach) {
               hasExtendedReach = true;
            }
         }

         double attackReach = rayTrace.func_216347_e().func_72436_e(eyePos);
         Vector3d lookVec = viewEntity.func_70676_i(1.0F);
         Vector3d attackVec = eyePos.func_72441_c(
            lookVec.field_72450_a * reachDistance, lookVec.field_72448_b * reachDistance, lookVec.field_72449_c * reachDistance
         );
         AxisAlignedBB axisAlignedBB = viewEntity.func_174813_aQ().func_216361_a(lookVec.func_186678_a(reachDistance)).func_72314_b(1.0, 1.0, 1.0);
         EntityRayTraceResult entityRayTrace = ProjectileHelper.func_221273_a(
            viewEntity, eyePos, attackVec, axisAlignedBB, entity -> !entity.func_175149_v() && entity.func_70067_L(), attackReach
         );
         if (entityRayTrace != null) {
            Vector3d hitVec = entityRayTrace.func_216347_e();
            double squareDistanceTo = eyePos.func_72436_e(hitVec);
            if (hasExtendedReach && squareDistanceTo > reach * reach) {
               result = BlockRayTraceResult.func_216352_a(
                  hitVec, Direction.func_210769_a(lookVec.field_72450_a, lookVec.field_72448_b, lookVec.field_72449_c), new BlockPos(hitVec)
               );
            } else if (squareDistanceTo < attackReach) {
               result = entityRayTrace;
            }
         } else {
            result = BlockRayTraceResult.func_216352_a(
               attackVec, Direction.func_210769_a(lookVec.field_72450_a, lookVec.field_72448_b, lookVec.field_72449_c), new BlockPos(attackVec)
            );
         }
      }

      return result;
   }
}
