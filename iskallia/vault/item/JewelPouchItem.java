package iskallia.vault.item;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.gear.GearScoreHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.network.message.OpenClientScreenMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class JewelPouchItem extends Item implements VaultLevelItem, IdentifiableItem {
   public JewelPouchItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced) {
      String levelDescription = getStoredLevel(stack).map(String::valueOf).orElse("???");
      MutableComponent lvlComponent = new TextComponent(levelDescription).withStyle(ChatFormatting.AQUA);
      tooltip.add(new TextComponent("Level: ").withStyle(ChatFormatting.GRAY).append(lvlComponent));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (hand == InteractionHand.MAIN_HAND) {
         if (!level.isClientSide() && player.isShiftKeyDown()) {
            if (instantOpen(player, stack, true)) {
               return InteractionResultHolder.success(stack);
            }
         } else if (player instanceof ServerPlayer sPlayer) {
            List<JewelPouchItem.RolledJewel> jewels = getJewels(stack);
            if (jewels.isEmpty()) {
               int vaultLevel = getStoredLevel(stack).orElseGet(() -> PlayerVaultStatsData.get(sPlayer.getLevel()).getVaultStats(sPlayer).getVaultLevel());
               int additionalIdentifiedJewels = 0;
               ExpertiseTree expertises = PlayerExpertisesData.get(sPlayer.getLevel()).getExpertises(sPlayer);

               for (JewelExpertise expertise : expertises.getAll(JewelExpertise.class, Skill::isUnlocked)) {
                  additionalIdentifiedJewels += expertise.getAdditionalIdentifiedJewels();
               }

               generateJewels(stack, vaultLevel, additionalIdentifiedJewels);
               jewels = getJewels(stack);
               player.level
                  .playSound(
                     null,
                     player.blockPosition(),
                     SoundEvents.ARMOR_EQUIP_TURTLE,
                     player.getSoundSource(),
                     1.0F,
                     0.9F + player.getLevel().getRandom().nextFloat(0.1F)
                  );
            }

            if (!jewels.isEmpty()) {
               ServerScheduler.INSTANCE
                  .schedule(
                     1,
                     () -> ModNetwork.CHANNEL
                        .sendTo(
                           new OpenClientScreenMessage(OpenClientScreenMessage.Type.JEWEL_POUCH),
                           sPlayer.connection.getConnection(),
                           NetworkDirection.PLAY_TO_CLIENT
                        )
                  );
            }
         }
      }

      return InteractionResultHolder.pass(stack);
   }

   private static void generateJewels(ItemStack stack, int vaultLevel, int additionalIdentifiedJewels) {
      ModConfigs.JEWEL_POUCH.getJewel().ifPresent(entry -> {
         List<JewelPouchItem.RolledJewel> results = new ArrayList<>();
         int jewelCount = ModConfigs.JEWEL_POUCH.getIdentifiedJewelCount() + additionalIdentifiedJewels;

         for (int i = 0; i < jewelCount; i++) {
            ItemStack jewelStack = entry.createItemStack();
            jewelStack = LootInitialization.initializeVaultLoot(jewelStack, vaultLevel);
            results.add(new JewelPouchItem.RolledJewel(jewelStack, true));
         }

         for (int i = 0; i < ModConfigs.JEWEL_POUCH.getUnidentifiedJewelCount(); i++) {
            ItemStack jewelStack = entry.createItemStack();
            if (jewelStack.getItem() instanceof VaultLevelItem levelItem) {
               levelItem.initializeVaultLoot(vaultLevel, jewelStack, null, null);
            }

            jewelStack = DataTransferItem.doConvertStack(jewelStack);
            results.add(new JewelPouchItem.RolledJewel(jewelStack, false));
         }

         setJewels(stack, results);
      });
   }

   private static boolean instantOpen(@Nonnull Player player, ItemStack jewelPouch, boolean playEffects) {
      int storedLevel = getStoredLevel(jewelPouch)
         .or(
            () -> Optional.of(player)
               .filter(p -> p instanceof ServerPlayer)
               .map(p -> (ServerPlayer)p)
               .map(sPlayer -> PlayerVaultStatsData.get(sPlayer.getLevel()).getVaultStats(sPlayer).getVaultLevel())
         )
         .orElse(-1);
      if (storedLevel == -1) {
         return false;
      } else {
         Level level = player.getLevel();
         return ModConfigs.JEWEL_POUCH.getJewel().map(entry -> {
            int additionalIdentifiedJewels = 0;
            if (player instanceof ServerPlayer sPlayer) {
               ExpertiseTree expertises = PlayerExpertisesData.get(sPlayer.getLevel()).getExpertises(sPlayer);

               for (JewelExpertise expertise : expertises.getAll(JewelExpertise.class, Skill::isUnlocked)) {
                  additionalIdentifiedJewels += expertise.getAdditionalIdentifiedJewels();
               }
            }

            int jewelCount = ModConfigs.JEWEL_POUCH.getIdentifiedJewelCount() + additionalIdentifiedJewels + 1;
            List<ItemStack> jewels = new ArrayList<>();

            for (int i = 0; i < jewelCount; i++) {
               ItemStack jewelStack = entry.createItemStack();
               jewelStack = LootInitialization.initializeVaultLoot(jewelStack, storedLevel);
               jewels.add(jewelStack);
            }

            return GearScoreHelper.pickHighestWeight(jewels).map(jewelStackx -> {
               player.drop(jewelStackx, false, false);
               if (level instanceof ServerLevel sLevel && playEffects) {
                  Vec3 pos = player.position();
                  sLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AXE_STRIP, SoundSource.PLAYERS, 1.0F, 2.0F);
                  sLevel.sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 500, 1.0, 1.0, 1.0, 0.5);
               }

               if (!player.isCreative()) {
                  jewelPouch.shrink(1);
               }

               return true;
            }).orElse(false);
         }).orElse(false);
      }
   }

   @Override
   public void initializeVaultLoot(int vaultLevel, ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      setStoredLevel(stack, vaultLevel);
   }

   @Override
   public VaultGearState getState(ItemStack stack) {
      return !getJewels(stack).isEmpty() ? VaultGearState.IDENTIFIED : VaultGearState.UNIDENTIFIED;
   }

   @Override
   public void setState(ItemStack stack, VaultGearState state) {
   }

   @Override
   public void inventoryIdentificationTick(Player player, ItemStack stack) {
   }

   @Override
   public void tickRoll(ItemStack stack, @Nullable Player player) {
   }

   @Override
   public void tickFinishRoll(ItemStack stack, @Nullable Player player) {
      if (player != null) {
         int count = Math.min(32, stack.getCount());

         for (int i = 0; i < count; i++) {
            instantOpen(player, stack, false);
         }
      }
   }

   @Override
   public boolean canIdentify(Player player, ItemStack stack) {
      return getJewels(stack).isEmpty();
   }

   public static void setStoredLevel(ItemStack stack, int vaultLevel) {
      CompoundTag tag = stack.getOrCreateTag();
      tag.putInt("vaultLevel", vaultLevel);
   }

   public static Optional<Integer> getStoredLevel(ItemStack stack) {
      CompoundTag tag = stack.getOrCreateTag();
      return !tag.contains("vaultLevel") ? Optional.empty() : Optional.of(tag.getInt("vaultLevel"));
   }

   public static void setJewels(ItemStack stack, List<JewelPouchItem.RolledJewel> results) {
      CompoundTag tag = stack.getOrCreateTag();
      ListTag resultTag = new ListTag();

      for (JewelPouchItem.RolledJewel outcome : results) {
         resultTag.add(outcome.serialize());
      }

      tag.put("jewels", resultTag);
   }

   public static List<JewelPouchItem.RolledJewel> getJewels(ItemStack stack) {
      List<JewelPouchItem.RolledJewel> results = new ArrayList<>();
      CompoundTag tag = stack.getOrCreateTag();
      ListTag resultTag = tag.getList("jewels", 10);
      resultTag.forEach(entry -> results.add(JewelPouchItem.RolledJewel.fromTag((CompoundTag)entry)));
      return results;
   }

   public record RolledJewel(ItemStack stack, boolean identified) {
      public static JewelPouchItem.RolledJewel fromTag(CompoundTag tag) {
         return new JewelPouchItem.RolledJewel(ItemStack.of(tag.getCompound("item")), tag.getBoolean("identified"));
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.put("item", this.stack.serializeNBT());
         tag.putBoolean("identified", this.identified);
         return tag;
      }
   }
}
