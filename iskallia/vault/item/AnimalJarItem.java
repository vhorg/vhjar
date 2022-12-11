package iskallia.vault.item;

import iskallia.vault.client.render.AnimalJarISTER;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class AnimalJarItem extends Item {
   private final List<Component> tooltip = new ArrayList<>();

   public AnimalJarItem(ResourceLocation id) {
      this(id, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }

   public AnimalJarItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public AnimalJarItem withTooltip(Component tooltip) {
      this.tooltip.add(tooltip);
      return this;
   }

   public AnimalJarItem withTooltip(Component... tooltip) {
      this.tooltip.addAll(Arrays.asList(tooltip));
      return this;
   }

   public AnimalJarItem withTooltip(List<Component> tooltip) {
      this.tooltip.addAll(tooltip);
      return this;
   }

   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (!this.tooltip.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.addAll(this.tooltip);
      }

      if (stack.hasTag() && stack.getTag().contains("entity")) {
         tooltip.add(new TextComponent("Contains : ").append(new TranslatableComponent(this.getTranslatedEntity(stack))).withStyle(ChatFormatting.GRAY));
      }

      if (stack.hasTag() && stack.getTag().contains("count")) {
         tooltip.add(new TextComponent("Count : " + stack.getTag().getInt("count")).withStyle(ChatFormatting.GRAY));
      } else {
         tooltip.add(new TextComponent("Crouch right-click an Animal to catch").withStyle(ChatFormatting.GRAY));
         tooltip.add(new TextComponent("For use in the Animal Pen").withStyle(ChatFormatting.GRAY));
      }

      tooltip.add(new TextComponent("Animals can not be released when caught").withStyle(ChatFormatting.GRAY));
   }

   public static ItemStack AddEntity(ItemStack stack, LivingEntity entity) {
      if (!entity.level.isClientSide() && !(entity instanceof Player) && entity.isAlive() && canAddEntity(stack, entity)) {
         if (stack.getOrCreateTag().contains("count")) {
            stack.getOrCreateTag().putInt("count", stack.getOrCreateTag().getInt("count") + 1);
         }

         if (!stack.getOrCreateTag().contains("entity")) {
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
            entity.save(nbt);
            nbt.putInt("count", 1);
         }

         if (entity.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
               null, entity.position().x, entity.position().y, entity.position().z, SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 0.75F, 1.5F
            );
            serverLevel.playSound(
               null, entity.position().x, entity.position().y, entity.position().z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.75F, 1.5F
            );
            serverLevel.sendParticles(
               ParticleTypes.POOF,
               entity.position().x(),
               entity.position().y() + entity.getBbHeight() / 2.0F,
               entity.position().z(),
               20,
               0.033333335F,
               serverLevel.random.nextDouble() * 0.1,
               0.033333335F,
               0.05F
            );
         }

         entity.remove(RemovalReason.KILLED);
         return stack;
      } else {
         return stack;
      }
   }

   public static boolean containsEntity(ItemStack stack) {
      return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("entity");
   }

   public static boolean canAddEntity(ItemStack stack, LivingEntity entity) {
      if (stack.isEmpty()) {
         return false;
      } else if (!stack.hasTag()) {
         return true;
      } else {
         return !stack.getTag().contains("entity") ? true : stack.getTag().getString("entity").equals(EntityType.getKey(entity.getType()).toString());
      }
   }

   public String getEntityID(ItemStack stack) {
      return stack.getTag().getString("entity");
   }

   public String getTranslatedEntity(ItemStack stack) {
      return ((EntityType)ForgeRegistries.ENTITIES.getValue(ResourceLocation.of(stack.getTag().getString("entity"), ':'))).getDescriptionId();
   }

   @Nullable
   public static Animal getAnimalFromItemStack(ItemStack stack, Level world) {
      EntityType<?> type = (EntityType<?>)EntityType.byString(stack.getTag().getString("entity")).orElse(null);
      if (type != null) {
         Entity entity = type.create(world);
         entity.load(stack.getTag());
         return (Animal)entity;
      } else {
         return null;
      }
   }

   public boolean isFoil(ItemStack itemStack) {
      return containsEntity(itemStack);
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(AnimalJarISTER.INSTANCE);
   }
}
