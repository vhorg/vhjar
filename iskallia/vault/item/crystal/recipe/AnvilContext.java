package iskallia.vault.item.crystal.recipe;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AnvilContext {
   private final Level world;
   private final BlockPos pos;
   private final Player player;
   private final ItemStack[] input;
   private ItemStack output;
   private String name;
   private int levelCost;
   private AnvilContext.TakeEvent take;

   public AnvilContext(Level world, BlockPos pos, Player player, ItemStack[] input, String name) {
      this.world = world;
      this.pos = pos;
      this.player = player;
      this.input = input;
      this.output = ItemStack.EMPTY;
      this.name = name;
      this.take = AnvilContext.TakeEvent.EMPTY;
   }

   public static AnvilContext ofSimulated(ItemStack primaryInput, ItemStack secondaryInput) {
      return new AnvilContext(null, null, null, new ItemStack[]{primaryInput, secondaryInput}, null);
   }

   public static AnvilContext ofAnvil(ContainerLevelAccess access, Player player, Container input, String name) {
      Level[] world = new Level[1];
      BlockPos[] pos = new BlockPos[1];
      access.execute((_world, _pos) -> {
         world[0] = _world;
         pos[0] = _pos;
      });
      return new AnvilContext(world[0], pos[0], player, new ItemStack[]{input.getItem(0), input.getItem(1)}, name);
   }

   public Optional<Level> getWorld() {
      return Optional.ofNullable(this.world);
   }

   public Optional<BlockPos> getPos() {
      return Optional.ofNullable(this.pos);
   }

   public Optional<Player> getPlayer() {
      return Optional.ofNullable(this.player);
   }

   public ItemStack[] getInput() {
      return this.input;
   }

   public ItemStack getOutput() {
      return this.output;
   }

   public String getName() {
      return this.name;
   }

   public int getLevelCost() {
      return this.levelCost;
   }

   public AnvilContext.TakeEvent getTake() {
      return this.take;
   }

   public void setOutput(ItemStack output) {
      this.output = output;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setLevelCost(int levelCost) {
      this.levelCost = levelCost;
   }

   public void onTake(AnvilContext.TakeEvent onTake) {
      this.take = onTake;
   }

   public Optional<BlockState> getBlockState() {
      return this.world != null && this.pos != null ? Optional.of(this.world.getBlockState(this.pos)) : Optional.empty();
   }

   @FunctionalInterface
   public interface TakeEvent {
      AnvilContext.TakeEvent EMPTY = () -> {};

      void run();

      default AnvilContext.TakeEvent prepend(AnvilContext.TakeEvent other) {
         return () -> {
            other.run();
            this.run();
         };
      }

      default AnvilContext.TakeEvent append(AnvilContext.TakeEvent other) {
         return () -> {
            this.run();
            other.run();
         };
      }
   }
}
