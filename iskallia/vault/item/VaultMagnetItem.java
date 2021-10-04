package iskallia.vault.item;

import iskallia.vault.config.entry.MagnetEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.VectorHelper;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultMagnetItem extends Item {
   private VaultMagnetItem.MagnetType type;
   private static final HashMap<UUID, UUID> pulledItems = new HashMap<>();

   public VaultMagnetItem(ResourceLocation id, VaultMagnetItem.MagnetType type) {
      super(new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.setRegistryName(id);
      this.type = type;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (worldIn != null) {
         int totalRepairs = stack.func_196082_o().func_74762_e("TotalRepairs");
         tooltip.add(new StringTextComponent(" "));
         tooltip.add(new StringTextComponent("Enabled: " + (this.isEnabled(stack) ? TextFormatting.GREEN + "true" : TextFormatting.RED + "false")));
         tooltip.add(new StringTextComponent("Repairs Remaining: " + this.getColor(30 - totalRepairs) + Math.max(0, 30 - totalRepairs)));
         tooltip.add(new StringTextComponent(" "));
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      }
   }

   private TextFormatting getColor(int amount) {
      if (amount < 10) {
         return TextFormatting.RED;
      } else {
         return amount < 20 ? TextFormatting.YELLOW : TextFormatting.GREEN;
      }
   }

   public boolean func_77636_d(ItemStack stack) {
      return this.isEnabled(stack);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack stack = player.func_184586_b(hand);
      this.setEnabled(stack, !this.isEnabled(stack), false);
      return new ActionResult(ActionResultType.SUCCESS, stack);
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.field_72995_K) {
         CompoundNBT nbt = stack.func_196082_o();
         if (!nbt.func_74764_b("Enabled")) {
            nbt.func_74757_a("Enabled", false);
            stack.func_77982_d(nbt);
         }

         if (entity instanceof PlayerEntity && this.isEnabled(stack)) {
            PlayerEntity player = (PlayerEntity)entity;
            VaultMagnetItem.MagnetType magnetType = ((VaultMagnetItem)stack.func_77973_b()).getType();
            MagnetEntry settings = ModConfigs.VAULT_UTILITIES.getMagnetSetting(magnetType);
            boolean instant = settings.shouldPullInstantly();
            boolean moveItems = settings.shouldPullItems();
            boolean moveXp = settings.shouldPullExperience();
            float speed = settings.getSpeed() / 20.0F;
            float radius = settings.getRadius();
            if (moveItems) {
               for (ItemEntity item : world.func_217357_a(ItemEntity.class, player.func_174813_aQ().func_186662_g(radius))) {
                  if (item.func_70089_S()
                     && !stack.func_196082_o().func_74767_n("PreventRemoteMovement")
                     && !item.func_184216_O().contains("PreventMagnetMovement")) {
                     if (!pulledItems.containsKey(item.func_110124_au())) {
                        PlayerEntity closest = this.getClosestPlayerWithMagnet(item, radius);
                        pulledItems.put(item.func_110124_au(), closest == null ? player.func_110124_au() : closest.func_110124_au());
                     }

                     if (pulledItems.get(item.func_110124_au()).equals(player.func_110124_au())) {
                        item.func_174868_q();
                        this.moveItemToPlayer(item, player, speed, instant);
                     }
                  }
               }
            }

            if (moveXp) {
               for (ExperienceOrbEntity orb : world.func_217357_a(ExperienceOrbEntity.class, player.func_174813_aQ().func_186662_g(radius))) {
                  this.moveXpToPlayer(orb, player, speed, instant);
               }
            }
         }
      }
   }

   private void moveItemToPlayer(ItemEntity item, PlayerEntity player, float speed, boolean instant) {
      if (instant) {
         item.func_70107_b(player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_());
      } else {
         Vector3d target = VectorHelper.getVectorFromPos(player.func_233580_cy_());
         Vector3d current = VectorHelper.getVectorFromPos(item.func_233580_cy_());
         Vector3d velocity = VectorHelper.getMovementVelocity(current, target, speed);
         item.func_70024_g(velocity.field_72450_a, velocity.field_72448_b, velocity.field_72449_c);
         item.field_70133_I = true;
      }
   }

   private void moveXpToPlayer(ExperienceOrbEntity orb, PlayerEntity player, float speed, boolean instant) {
      if (instant) {
         orb.func_70107_b(player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_());
      } else {
         Vector3d target = VectorHelper.getVectorFromPos(player.func_233580_cy_());
         Vector3d current = VectorHelper.getVectorFromPos(orb.func_233580_cy_());
         Vector3d velocity = VectorHelper.getMovementVelocity(current, target, speed);
         orb.func_70024_g(velocity.field_72450_a, velocity.field_72448_b, velocity.field_72449_c);
         orb.field_70133_I = true;
      }
   }

   public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
      if (stack.func_77952_i() + amount >= stack.func_77958_k()) {
         this.setEnabled(stack, false, true);
         return 0;
      } else {
         return amount;
      }
   }

   private void setEnabled(ItemStack stack, boolean enabled, boolean force) {
      if (force) {
         this.setEnabled(stack, enabled);
      } else if (stack.func_77952_i() < stack.func_77958_k() - 1) {
         this.setEnabled(stack, enabled);
      }
   }

   private void setEnabled(ItemStack stack, boolean enabled) {
      CompoundNBT tag = stack.func_196082_o();
      tag.func_74757_a("Enabled", enabled);
      stack.func_77982_d(tag);
   }

   private boolean isEnabled(ItemStack stack) {
      return stack.func_196082_o().func_74767_n("Enabled");
   }

   public VaultMagnetItem.MagnetType getType() {
      return this.type;
   }

   public static boolean isMagnet(ItemStack stack) {
      return stack.func_77973_b() instanceof VaultMagnetItem;
   }

   public boolean showDurabilityBar(ItemStack stack) {
      return stack.func_77952_i() > 0;
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      return (double)stack.func_77952_i() / this.getMaxDamage(stack);
   }

   public int getMaxDamage(ItemStack stack) {
      if (ModConfigs.VAULT_UTILITIES != null) {
         MagnetEntry setting = ModConfigs.VAULT_UTILITIES.getMagnetSetting(this.type);
         return setting.getMaxDurability();
      } else {
         return 0;
      }
   }

   public boolean func_77645_m() {
      return true;
   }

   public boolean func_82789_a(ItemStack toRepair, ItemStack repair) {
      return toRepair.func_77973_b() instanceof VaultMagnetItem && repair.func_77973_b() == ModItems.MAGNETITE;
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   public boolean func_77616_k(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   @SubscribeEvent
   public static void onItemPickup(ItemPickupEvent event) {
      PlayerEntity player = event.getPlayer();
      PlayerInventory inventory = player.field_71071_by;
      pulledItems.remove(event.getOriginalEntity().func_110124_au());

      for (int i = 0; i < inventory.func_70302_i_(); i++) {
         ItemStack stack = inventory.func_70301_a(i);
         if (!stack.func_190926_b()) {
            if (isMagnet(stack) && ((VaultMagnetItem)stack.func_77973_b()).isEnabled(stack)) {
               stack.func_222118_a(1, player, onBroken -> {});
            } else {
               LazyOptional<IItemHandler> itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
               itemHandler.ifPresent(h -> {
                  for (int j = 0; j < h.getSlots(); j++) {
                     ItemStack stackInHandler = h.getStackInSlot(j);
                     if (isMagnet(stackInHandler) && ((VaultMagnetItem)stackInHandler.func_77973_b()).isEnabled(stackInHandler)) {
                        stackInHandler.func_222118_a(1, player, onBroken -> {});
                     }
                  }
               });
            }
         }
      }
   }

   @Nullable
   private PlayerEntity getClosestPlayerWithMagnet(ItemEntity item, double radius) {
      List<PlayerEntity> players = item.func_130014_f_().func_217357_a(PlayerEntity.class, item.func_174813_aQ().func_186662_g(radius));
      if (players.isEmpty()) {
         return null;
      } else {
         PlayerEntity closest = players.get(0);
         double distance = radius;

         for (PlayerEntity player : players) {
            double temp = player.func_70032_d(item);
            if (temp < distance && this.hasEnabledMagnetInRange(player, radius)) {
               closest = player;
               distance = temp;
            }
         }

         return closest;
      }
   }

   private boolean hasEnabledMagnetInRange(PlayerEntity player, double radius) {
      PlayerInventory inventory = player.field_71071_by;

      for (int i = 0; i < inventory.func_70302_i_(); i++) {
         ItemStack stack = inventory.func_70301_a(i);
         if (!stack.func_190926_b() && isMagnet(stack)) {
            VaultMagnetItem magnet = (VaultMagnetItem)stack.func_77973_b();
            if (magnet.isEnabled(stack)) {
               MagnetEntry setting = ModConfigs.VAULT_UTILITIES.getMagnetSetting(magnet.getType());
               if (setting.getRadius() >= radius) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static enum MagnetType {
      WEAK,
      STRONG,
      OMEGA;
   }
}
