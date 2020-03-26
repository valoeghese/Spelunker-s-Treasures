package tk.valoeghese.spelunkers.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import tk.valoeghese.spelunkers.TreasureLootTables;

public class TreasureChestFeature extends Feature<DefaultFeatureConfig> {
	
	private final Type t;
	
	public TreasureChestFeature(TreasureChestFeature.Type type) {
		super(DefaultFeatureConfig::deserialize);
		
		t = type;
	}
	
	public static enum Type {
		// overworld
		CAVERN(63, Blocks.STONE, TreasureLootTables.CAVERN_CHEST),
		SPELUNKERS(38, Blocks.STONE, TreasureLootTables.SPELUNKERS_CHEST),
		// nether
		NETHER(100, Blocks.NETHERRACK, TreasureLootTables.NETHER_CHEST);

		public final int maxY;
		public final Block on;
		public final Identifier loot;

		private Type(int my, Block b, Identifier lootTable) {
			maxY = my;
			on = b;
			loot = lootTable;
		}
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random rand, BlockPos origin, DefaultFeatureConfig config) {
		int y = rand.nextInt(t.maxY - 5) + 5;
		BlockPos.Mutable mutPos = new BlockPos.Mutable(origin.getX(), y, origin.getZ());

		int attempts = 25;
		while (mutPos.getY() > 3 && attempts > 0) {
			
			if (world.getBlockState(mutPos).isAir() && world.getBlockState(mutPos.down()).getBlock().equals(t.on)) {
				this.setBlockState(world, mutPos, Blocks.CHEST.getDefaultState());
				
				BlockEntity entity = world.getBlockEntity(mutPos);
				if (entity instanceof ChestBlockEntity) {
					((ChestBlockEntity)entity).setLootTable(t.loot, rand.nextLong());
				}
				return true;
			}

			mutPos.setY(--y);
			attempts--;
		}
		
		return false;
	}

}
