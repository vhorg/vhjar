package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.container.VaultDiffuserContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.AnimalJarItem;
import iskallia.vault.network.message.AnimalPenParticleMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class AnimalPenTileEntity extends BlockEntity implements MenuProvider {
   private final AnimalPenTileEntity.AnimalPenInventory inventory = new AnimalPenTileEntity.AnimalPenInventory();
   private ItemStack prevInput = ItemStack.EMPTY;
   private Animal animalToReference = null;
   private Animal dyingAnimalToReference = null;
   private List<Integer> deathTime = new ArrayList<>();
   private int tickCount = 0;
   private static final Method GET_DEATH_SOUND = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_5592_", new Class[0]);
   private static final Method GET_EXPERIENCE_REWARD = ObfuscationReflectionHelper.findMethod(Animal.class, "m_6552_", new Class[]{Player.class});

   public AnimalPenTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.ANIMAL_PEN_ENTITY, pWorldPosition, pBlockState);
   }

   public Animal getAnimalToReference() {
      return this.animalToReference;
   }

   public Animal getDyingAnimalToReference() {
      return this.dyingAnimalToReference;
   }

   public void playAmbient() {
      if (this.animalToReference != null) {
         Vec3 temp = this.animalToReference.position();
         this.animalToReference.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() - 0.1F, this.getBlockPos().getZ() + 0.5F);
         this.animalToReference.setOldPosAndRot();
         this.animalToReference.playAmbientSound();
         this.animalToReference.setPos(temp);
         this.animalToReference.setOldPosAndRot();
      }
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public static void tick(Level world, BlockPos pos, BlockState state, AnimalPenTileEntity tile) {
      if (world.isClientSide()) {
         tile.tickCount++;
         int itor = 0;

         for (int time : tile.deathTime) {
            if (time > 0) {
               tile.deathTime.set(itor, time + 1);
               if (time > 25) {
                  tile.deathTime.set(itor, 0);

                  for (int i = 0; i < 10; i++) {
                     Random random = world.getRandom();
                     Vec3 offset = new Vec3(
                        random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
                     );
                     world.addParticle(
                        ParticleTypes.POOF,
                        true,
                        tile.getBlockPos().getX() + 0.5 + offset.x,
                        tile.getBlockPos().getY() + random.nextDouble() * 0.35F + 0.35F,
                        tile.getBlockPos().getZ() + 0.5 + offset.z,
                        offset.x / 20.0,
                        random.nextDouble() * 0.1,
                        offset.z / 20.0
                     );
                  }
               }
            }

            itor++;
         }

         tile.deathTime.remove(Integer.valueOf(0));
         ItemStack invItem = tile.inventory.getItem(0);
         ItemStack prevItem = tile.prevInput;
         if (!prevItem.sameItem(invItem)) {
            if (AnimalJarItem.containsEntity(tile.inventory.getItem(0))) {
               tile.animalToReference = AnimalJarItem.getAnimalFromItemStack(tile.inventory.getItem(0), world);
            } else {
               tile.animalToReference = null;
            }
         }
      } else {
         if (tile.animalToReference == null && AnimalJarItem.containsEntity(tile.inventory.getItem(0))) {
            tile.animalToReference = AnimalJarItem.getAnimalFromItemStack(tile.inventory.getItem(0), world);
         } else {
            tile.animalToReference = null;
         }

         if (tile.animalToReference instanceof Bee bee) {
            ItemStack invItem = tile.inventory.getItem(0);
            if (invItem.hasTag()) {
               CompoundTag tag = invItem.getOrCreateTag();
               if (tag.contains("pollenTimer")) {
                  int timer = tag.getInt("pollenTimer");
                  int count = tag.getInt("count");
                  boolean honeyReady = false;
                  if (tag.contains("honeyReady") && tag.getBoolean("honeyReady")) {
                     honeyReady = true;
                  }

                  if (timer > 0 && !honeyReady) {
                     tag.putInt("pollenTimer", timer - 1);
                     if (timer - 1 == 0) {
                        tag.putInt("pollenTimer", Mth.clamp((3600 - (count - 1) * 10 * 20) / 5, 40, 3600));
                        if (tag.contains("honeyLevel")) {
                           tile.level.playSound(null, tile.getBlockPos(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 0.75F, 1.0F);
                           if (tag.getInt("honeyLevel") < 5) {
                              tag.putInt("honeyLevel", tag.getInt("honeyLevel") + 1);
                           }

                           if (tag.getInt("honeyLevel") >= 5) {
                              tag.putBoolean("honeyReady", true);
                              tile.level.playSound(null, tile.getBlockPos(), SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 0.75F, 1.0F);
                              tile.level.playSound(null, tile.getBlockPos(), SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 0.75F, 1.0F);
                           }
                        }
                     }

                     tile.setChanged();
                     if (world instanceof ServerLevel serverWorld) {
                        serverWorld.sendBlockUpdated(tile.getBlockPos(), state, state, 3);
                     }
                  }
               } else {
                  int countx = tag.getInt("count");
                  tag.putInt("honeyLevel", 0);
                  tag.putInt("pollenTimer", Mth.clamp((3600 - (countx - 1) * 10 * 20) / 5, 40, 3600));
                  tile.setChanged();
                  if (world instanceof ServerLevel serverWorld) {
                     serverWorld.sendBlockUpdated(tile.getBlockPos(), state, state, 3);
                  }
               }
            }
         }

         ItemStack invItem = tile.inventory.getItem(0);
         if (invItem.hasTag()) {
            CompoundTag tag = invItem.getOrCreateTag();
            boolean changed = false;
            if (tag.contains("shearTimer")) {
               int timerx = tag.getInt("shearTimer");
               if (timerx > 1) {
                  tag.putInt("shearTimer", timerx - 1);
               } else {
                  tag.remove("shearTimer");
                  tile.playAmbient();
               }

               changed = true;
            }

            if (tag.contains("breedTimer")) {
               int timerx = tag.getInt("breedTimer");
               if (timerx > 1) {
                  tag.putInt("breedTimer", timerx - 1);
               } else {
                  tag.remove("breedTimer");
                  tile.playAmbient();
               }

               changed = true;
            }

            if (tag.contains("eggTimer")) {
               int timerx = tag.getInt("eggTimer");
               if (timerx > 1) {
                  tag.putInt("eggTimer", timerx - 1);
               } else {
                  tag.remove("eggTimer");
                  tile.playAmbient();
               }

               changed = true;
            }

            if (tag.contains("turtleEggTimer")) {
               int timerx = tag.getInt("turtleEggTimer");
               if (timerx > 1) {
                  tag.putInt("turtleEggTimer", timerx - 1);
               } else {
                  tag.remove("turtleEggTimer");
                  world.playSound(null, tile.getBlockPos(), SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 1.0F, 1.0F);
               }

               changed = true;
            }

            if (changed) {
               tile.setChanged();
               if (world instanceof ServerLevel serverWorld) {
                  serverWorld.sendBlockUpdated(tile.getBlockPos(), state, state, 3);
               }
            }
         }
      }

      tile.prevInput = tile.inventory.getItem(0);
   }

   public List<Integer> getDeathTime() {
      return this.deathTime;
   }

   public boolean attack(@NotNull BlockState state, @NotNull Level level, @NotNull Player player) {
      if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem)) {
         return false;
      } else if (!AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
         return false;
      } else {
         if (AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
            this.animalToReference = AnimalJarItem.getAnimalFromItemStack(this.inventory.getItem(0), level);
         } else {
            this.animalToReference = null;
         }

         if (this.animalToReference == null) {
            return false;
         } else {
            this.dyingAnimalToReference = this.animalToReference;
            ItemStack invItem = this.getInventory().getItem(0);
            if (level.isClientSide()) {
               this.deathTime.add(1);
            } else {
               if (!invItem.hasTag() || !invItem.getTag().contains("count")) {
                  return false;
               }

               int count = invItem.getTag().getInt("count") - 1;
               if (count <= 0) {
                  this.getInventory().setItem(0, ItemStack.EMPTY);
                  Block.popResource(level, this.getBlockPos(), new ItemStack(ModItems.ANIMAL_JAR));
               } else {
                  invItem.getTag().putInt("count", count);
               }

               this.setChanged();
               if (level instanceof ServerLevel serverWorld) {
                  serverWorld.sendBlockUpdated(this.getBlockPos(), state, state, 3);
               }
            }

            if (this.animalToReference instanceof Sheep sheep) {
               sheep.setSheared(invItem.hasTag() && invItem.getTag().contains("shearTimer"));
            }

            DamageSource pDamageSource = DamageSource.playerAttack(player);
            player.sweepAttack();
            Vec3 temp = this.animalToReference.position();
            this.animalToReference.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 0.5F, this.getBlockPos().getZ() + 0.5F);
            this.animalToReference.setOldPosAndRot();
            SoundEvent death = this.getDeathSound(this.animalToReference);
            level.playSound(null, this.getBlockPos(), death, SoundSource.BLOCKS, 0.75F, 1.0F);
            level.playSound(
               player, player.position().x, player.position().y, player.position().z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.BLOCKS, 0.75F, 1.0F
            );
            if (!level.isClientSide()) {
               ResourceLocation resourcelocation = this.animalToReference.getLootTable();
               LootTable loottable = level.getServer().getLootTables().get(resourcelocation);
               Builder lootcontext$builder = new Builder((ServerLevel)level)
                  .withRandom(level.random)
                  .withParameter(LootContextParams.THIS_ENTITY, this.animalToReference)
                  .withParameter(LootContextParams.ORIGIN, this.animalToReference.position())
                  .withParameter(LootContextParams.DAMAGE_SOURCE, pDamageSource)
                  .withOptionalParameter(LootContextParams.KILLER_ENTITY, pDamageSource.getEntity())
                  .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, pDamageSource.getDirectEntity());
               lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
               LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
               loottable.getRandomItems(ctx).forEach(this.animalToReference::spawnAtLocation);
               int reward = ForgeEventFactory.getExperienceDrop(this.animalToReference, player, this.getExperienceReward(this.animalToReference, player));
               ExperienceOrb.award((ServerLevel)level, this.animalToReference.position(), reward);
            } else {
               for (int i = 0; i < 5; i++) {
                  Random random = level.getRandom();
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  level.addParticle(
                     ParticleTypes.DAMAGE_INDICATOR,
                     true,
                     this.getBlockPos().getX() + 0.5 + offset.x,
                     this.getBlockPos().getY() + random.nextDouble() * 0.35F + 0.35F,
                     this.getBlockPos().getZ() + 0.5 + offset.z,
                     offset.x / 2.0,
                     random.nextDouble() * 0.1,
                     offset.z / 2.0
                  );
               }
            }

            this.animalToReference.setPos(temp);
            this.animalToReference.setOldPosAndRot();
            return true;
         }
      }
   }

   private SoundEvent getDeathSound(LivingEntity entity) {
      try {
         return (SoundEvent)GET_DEATH_SOUND.invoke(entity);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         VaultMod.LOGGER.error("Error calling getDeathSound: ", var3);
         return SoundEvents.GENERIC_DEATH;
      }
   }

   private int getExperienceReward(Animal animal, Player player) {
      try {
         return (Integer)GET_EXPERIENCE_REWARD.invoke(animal, player);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         VaultMod.LOGGER.error("Error calling getExperienceReward: ", var4);
         return 0;
      }
   }

   public boolean interact(@NotNull BlockState state, @NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
      ItemStack itemInHand = player.getItemInHand(hand).copy();
      ItemStack itemInPen = this.inventory.getItem(0).copy();
      ItemStack blockInPen = this.inventory.getItem(1).copy();
      if (!player.isCrouching()
         && blockInPen.isEmpty()
         && itemInHand.getItem() instanceof BlockItem blockItem
         && !(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
         VoxelShape shape = blockItem.getBlock().getBlockSupportShape(blockItem.getBlock().defaultBlockState(), level, this.getBlockPos());
         if (shape == Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)) {
            if (level instanceof ServerLevel serverWorld) {
               ItemStack stack = itemInHand.copy();
               stack.setCount(1);
               this.inventory.setItem(1, stack);
               player.getItemInHand(hand).shrink(1);
               level.playSound(
                  null,
                  this.getBlockPos(),
                  blockItem.getBlock().getSoundType(blockItem.getBlock().defaultBlockState()).getPlaceSound(),
                  SoundSource.BLOCKS,
                  0.75F,
                  1.5F
               );
               this.setChanged();
               serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
            }

            return true;
         }
      }

      if (player.isCrouching() && itemInHand.isEmpty() && itemInPen.isEmpty() && !blockInPen.isEmpty()) {
         if (level instanceof ServerLevel serverWorld) {
            player.setItemInHand(hand, blockInPen);
            this.inventory.getItem(1).shrink(1);
            level.playSound(null, this.getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.setChanged();
            serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
         }

         return true;
      } else {
         if (this.animalToReference == null && AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
            this.animalToReference = AnimalJarItem.getAnimalFromItemStack(this.inventory.getItem(0), level);
         }

         if (!player.isCrouching() && !itemInPen.isEmpty() && itemInHand.is(Items.BUCKET) && this.animalToReference instanceof Cow cow) {
            if (level instanceof ServerLevel serverWorld) {
               level.playSound(null, this.getBlockPos(), SoundEvents.COW_MILK, SoundSource.BLOCKS, 1.0F, 1.0F);
               ItemStack item = ItemUtils.createFilledResult(player.getItemInHand(hand), player, Items.MILK_BUCKET.getDefaultInstance());
               player.setItemInHand(hand, item);
            }

            return true;
         } else {
            if (!player.isCrouching() && !itemInPen.isEmpty() && this.animalToReference instanceof Bee) {
               CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
               if (tag.contains("honeyReady") && tag.getBoolean("honeyReady")) {
                  if (itemInHand.getItem() instanceof ShearsItem
                     || itemInHand.is(ModItems.TOOL) && VaultGearData.read(itemInHand).get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
                     if (level instanceof ServerLevel serverWorld) {
                        resetHoney(tag);
                        level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                        Block.popResource(level, this.getBlockPos(), new ItemStack(Items.HONEYCOMB, 3));
                        this.setChanged();
                        serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                     }

                     return true;
                  }

                  if (itemInHand.is(Items.GLASS_BOTTLE)) {
                     if (level instanceof ServerLevel serverWorld) {
                        resetHoney(tag);
                        level.playSound(null, this.getBlockPos(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        player.getItemInHand(hand).shrink(1);
                        player.getInventory().placeItemBackInInventory(new ItemStack(Items.HONEY_BOTTLE, 1));
                        this.setChanged();
                        serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                     }

                     return true;
                  }
               }
            }

            if (!player.isCrouching() && !itemInPen.isEmpty() && this.animalToReference instanceof Chicken) {
               CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
               if (!tag.contains("eggTimer") && itemInHand.is(Items.BUCKET)) {
                  if (level instanceof ServerLevel serverWorld) {
                     level.playSound(null, this.getBlockPos(), SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0F, 1.0F);
                     int count = Mth.clamp(itemInPen.getOrCreateTag().getInt("count"), 1, 64);

                     for (int i = 0; i < count; i++) {
                        Block.popResource(level, this.getBlockPos(), new ItemStack(Items.EGG));
                     }

                     this.inventory.getItem(0).getOrCreateTag().putInt("eggTimer", 6000);
                     this.setChanged();
                     serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                  }

                  return true;
               }
            }

            if (!player.isCrouching() && !itemInPen.isEmpty() && this.animalToReference instanceof Turtle) {
               CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
               if (!tag.contains("turtleEggTimer") && itemInHand.is(Items.BUCKET)) {
                  if (level instanceof ServerLevel serverWorld) {
                     level.playSound(null, this.getBlockPos(), SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0F, 1.0F);
                     int count = Mth.clamp((itemInPen.getOrCreateTag().getInt("count") + 1) / 2, 1, 16);

                     for (int i = 0; i < count; i++) {
                        Block.popResource(level, this.getBlockPos(), new ItemStack(Items.TURTLE_EGG));
                     }

                     this.inventory.getItem(0).getOrCreateTag().putInt("turtleEggTimer", 6000);
                     this.setChanged();
                     serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                  }

                  return true;
               }
            }

            if (!player.isCrouching() && !itemInPen.isEmpty() && this.animalToReference instanceof MushroomCow && level instanceof ServerLevel) {
               this.animalToReference.mobInteract(player, hand);
            }

            if (!player.isCrouching()
               && !itemInPen.isEmpty()
               && this.animalToReference instanceof Sheep sheep
               && itemInHand.getItem() instanceof DyeItem dyeItem
               && sheep.getColor() != dyeItem.getDyeColor()) {
               if (level instanceof ServerLevel serverWorld) {
                  CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
                  sheep.setColor(dyeItem.getDyeColor());
                  sheep.save(tag);
                  if (!player.isCreative()) {
                     player.getItemInHand(hand).shrink(1);
                  }

                  serverWorld.playSound(null, this.worldPosition, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                  this.setChanged();
                  serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
               } else {
                  sheep.setColor(dyeItem.getDyeColor());
               }

               return true;
            } else {
               if (!player.isCrouching()
                  && !itemInPen.isEmpty()
                  && (
                     itemInHand.getItem() instanceof ShearsItem shearsItem
                        || itemInHand.is(ModItems.TOOL)
                           && VaultGearData.read(itemInHand).get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())
                  )) {
                  if (this.animalToReference == null && AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
                     this.animalToReference = AnimalJarItem.getAnimalFromItemStack(this.inventory.getItem(0), level);
                  }

                  if (this.animalToReference == null) {
                     return false;
                  }

                  if (this.animalToReference instanceof Sheep sheep) {
                     if (this.inventory.getItem(0).getOrCreateTag().contains("shearTimer")) {
                        return false;
                     }

                     if (level instanceof ServerLevel serverWorld) {
                        Vec3 temp = this.animalToReference.position();
                        this.animalToReference.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() - 0.1F, this.getBlockPos().getZ() + 0.5F);
                        this.animalToReference.setOldPosAndRot();
                        int count = 1;
                        if (this.inventory.getItem(0).getOrCreateTag().contains("count")) {
                           count = this.inventory.getItem(0).getOrCreateTag().getInt("count");
                        }

                        if (count < 75) {
                           for (int i = 0; i < count; i++) {
                              int spawnCount = 1 + level.random.nextInt(3);

                              for (int j = 0; j < spawnCount; j++) {
                                 ItemEntity itementity = this.animalToReference.spawnAtLocation((ItemLike)Sheep.ITEM_BY_DYE.get(sheep.getColor()), 1);
                                 if (itementity != null) {
                                    itementity.setDeltaMovement(
                                       itementity.getDeltaMovement()
                                          .add(
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F,
                                             level.random.nextFloat() * 0.05F,
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F
                                          )
                                    );
                                 }
                              }
                           }
                        } else {
                           int spawnCount = 0;

                           for (int i = 0; i < count; i++) {
                              spawnCount += 1 + level.random.nextInt(3);
                           }

                           if (spawnCount > 256) {
                              spawnCount = 256;
                           }

                           while (spawnCount > 0) {
                              if (spawnCount >= 64) {
                                 spawnCount -= 64;
                                 ItemEntity itementity = this.animalToReference
                                    .spawnAtLocation(new ItemStack((ItemLike)Sheep.ITEM_BY_DYE.get(sheep.getColor()), 64), 1.0F);
                                 if (itementity != null) {
                                    itementity.setDeltaMovement(
                                       itementity.getDeltaMovement()
                                          .add(
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F,
                                             level.random.nextFloat() * 0.05F,
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F
                                          )
                                    );
                                 }
                              } else {
                                 ItemEntity itementity = this.animalToReference
                                    .spawnAtLocation(new ItemStack((ItemLike)Sheep.ITEM_BY_DYE.get(sheep.getColor()), spawnCount), 1.0F);
                                 if (itementity != null) {
                                    itementity.setDeltaMovement(
                                       itementity.getDeltaMovement()
                                          .add(
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F,
                                             level.random.nextFloat() * 0.05F,
                                             (level.random.nextFloat() - level.random.nextFloat()) * 0.1F
                                          )
                                    );
                                 }

                                 spawnCount = 0;
                              }
                           }
                        }

                        sheep.shear(SoundSource.BLOCKS);
                        level.playSound((Player)null, this.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.animalToReference.setPos(temp);
                        this.animalToReference.setOldPosAndRot();
                        this.inventory.getItem(0).getOrCreateTag().putInt("shearTimer", 6000);
                        this.setChanged();
                        serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                     }

                     return true;
                  }
               }

               if (!player.isCrouching()
                  && !itemInPen.isEmpty()
                  && itemInHand.getItem() instanceof AnimalJarItem
                  && itemInHand.getOrCreateTag().contains("entity")
                  && itemInPen.getOrCreateTag().contains("entity")
                  && itemInHand.getOrCreateTag().getString("entity").equals(itemInPen.getOrCreateTag().getString("entity"))) {
                  if (level instanceof ServerLevel serverWorld) {
                     CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
                     tag.putInt("count", tag.getInt("count") + itemInHand.getOrCreateTag().getInt("count"));
                     player.setItemInHand(hand, new ItemStack(ModItems.ANIMAL_JAR));
                     this.setChanged();
                     serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                     level.playSound((Player)null, this.getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                  }

                  return true;
               } else if ((
                     player.isCrouching()
                        || !itemInPen.isEmpty()
                        || !(itemInHand.getItem() instanceof AnimalJarItem)
                        || !itemInHand.hasTag()
                        || !itemInHand.getOrCreateTag().contains("entity")
                  )
                  && (!player.isCrouching() || !itemInHand.isEmpty())) {
                  if (this.animalToReference == null && AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
                     this.animalToReference = AnimalJarItem.getAnimalFromItemStack(this.inventory.getItem(0), level);
                  }

                  if (this.animalToReference == null) {
                     return false;
                  } else if (this.inventory.getItem(0).getOrCreateTag().contains("breedTimer")) {
                     return false;
                  } else if (this.animalToReference.isFood(itemInHand) && this.inventory.getItem(0).getOrCreateTag().contains("count")) {
                     int countx = this.inventory.getItem(0).getOrCreateTag().getInt("count");
                     int stackCount = itemInHand.getCount();
                     int usedCount = 0;
                     if (stackCount > countx) {
                        usedCount = countx;
                     } else {
                        usedCount = stackCount;
                     }

                     if (itemInHand.getCount() == 1) {
                        usedCount = 1;
                     }

                     if (countx > 1) {
                        if (level instanceof ServerLevel serverWorld) {
                           level.playSound(null, this.getBlockPos(), this.animalToReference.getEatingSound(itemInHand), SoundSource.BLOCKS, 0.75F, 1.0F);
                           Vec3 tempx = this.animalToReference.position();
                           this.animalToReference.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 0.1F, this.getBlockPos().getZ() + 0.5F);
                           this.animalToReference.setOldPosAndRot();
                           this.animalToReference.playAmbientSound();
                           this.animalToReference.setPos(tempx);
                           this.animalToReference.setOldPosAndRot();
                           player.getItemInHand(hand).shrink(usedCount);
                           CompoundTag tag = this.inventory.getItem(0).getOrCreateTag();
                           tag.putInt("breedTimer", 1100 + 100 * (usedCount / 2));
                           tag.putInt("count", tag.getInt("count") + usedCount / 2);
                           ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new AnimalPenParticleMessage(this.getBlockPos()));
                        }

                        return true;
                     } else {
                        player.displayClientMessage(new TextComponent("Needs at least two to breed.."), true);
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  if (level instanceof ServerLevel serverWorld) {
                     this.inventory.setItem(0, itemInHand);
                     player.setItemInHand(hand, itemInPen);
                     if (AnimalJarItem.containsEntity(this.inventory.getItem(0))) {
                        this.animalToReference = AnimalJarItem.getAnimalFromItemStack(this.inventory.getItem(0), level);
                     } else {
                        this.animalToReference = null;
                     }

                     if (this.animalToReference != null) {
                        level.playSound(null, this.getBlockPos(), SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 0.75F, 1.0F);
                        Vec3 tempx = this.animalToReference.position();
                        this.animalToReference.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 0.1F, this.getBlockPos().getZ() + 0.5F);
                        this.animalToReference.setOldPosAndRot();
                        this.animalToReference.playAmbientSound();
                        this.animalToReference.setPos(tempx);
                        this.animalToReference.setOldPosAndRot();
                     } else {
                        level.playSound(null, this.getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.75F, 1.0F);
                     }

                     this.setChanged();
                     serverWorld.sendBlockUpdated(hit.getBlockPos(), state, state, 3);
                  }

                  return true;
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 7; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.HEART,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.35F + 0.35F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1,
               offset.z / 2.0
            );
         }
      }
   }

   public static void resetHoney(CompoundTag tag) {
      tag.putBoolean("honeyReady", false);
      tag.putInt("honeyLevel", 0);
      tag.putInt("pollenTimer", Mth.clamp((3600 - (tag.getInt("count") - 1) * 10 * 20) / 5, 40, 3600));
   }

   public void setChanged() {
      super.setChanged();
   }

   public AnimalPenTileEntity.AnimalPenInventory getInventory() {
      return this.inventory;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultDiffuserContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public class AnimalPenInventory extends SimpleContainer {
      public AnimalPenInventory() {
         super(2);
      }

      public boolean canPlaceItem(int slot, ItemStack stack) {
         return true;
      }

      public void setChanged() {
         super.setChanged();
         AnimalPenTileEntity.this.setChanged();
      }
   }
}
