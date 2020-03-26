package tk.valoeghese.spelunkers;

import java.util.function.Predicate;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import tk.valoeghese.spelunkers.block.CrystalBlock;
import tk.valoeghese.spelunkers.feature.MobSpawnerFeature;
import tk.valoeghese.spelunkers.feature.TreasureChestFeature;

public class SpelunkersTreasures implements ModInitializer {
	public static final Predicate<Biome> OVERWORLD = biome -> {
		Biome.Category category = biome.getCategory();
		return !(category == Biome.Category.NETHER || category == Biome.Category.THEEND);
	};

	public static final Predicate<Biome> NETHER = biome -> {
		Biome.Category category = biome.getCategory();
		return category == Biome.Category.NETHER;
	};

	public static final SpelunkersConfig CONFIG = SpelunkersConfig.load();

//	public static final Block CAVE_CRYSTAL_QUARTZ = new CrystalBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.QUARTZ).strength(0.3F, 0.3F).sounds(BlockSoundGroup.GLASS));

//	public static final Block CAVE_CRYSTAL_FLUORITE_GREEN = new CrystalBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.GREEN).strength(0.3F, 0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(3));
//	public static final Block CAVE_CRYSTAL_FLUORITE_PURPLE = new CrystalBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.PURPLE).strength(0.3F, 0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(3));
//	public static final Block CAVE_CRYSTAL_FLUORITE_TEAL = new CrystalBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.CYAN).strength(0.3F, 0.3F).sounds(BlockSoundGroup.GLASS).lightLevel(3));

	public static final TreasureChestFeature CAVERN_CHEST = new TreasureChestFeature(TreasureChestFeature.Type.CAVERN);
	public static final TreasureChestFeature SPELUNKERS_CHEST = new TreasureChestFeature(TreasureChestFeature.Type.SPELUNKERS);

	public static final TreasureChestFeature NETHER_CHEST = new TreasureChestFeature(TreasureChestFeature.Type.NETHER);

	public static final MobSpawnerFeature CAVE_SPAWNER = new MobSpawnerFeature(50, EntityType.ZOMBIE, EntityType.SPIDER);
	public static final MobSpawnerFeature RARE_CAVE_SPAWNER = new MobSpawnerFeature(35, EntityType.SKELETON, EntityType.SKELETON, EntityType.SKELETON, EntityType.CAVE_SPIDER, EntityType.CREEPER);

	@Override
	public void onInitialize() {
		System.out.println("Initializing Spelunker's Treasures!");
		registerAll();
		addGeneration();
	}

	private void registerAll() {
		// Block
//		registerBlock("cave_crystal_quartz", CAVE_CRYSTAL_QUARTZ, ItemGroup.DECORATIONS);
//		registerBlock("cave_crystal_fluorite_green", CAVE_CRYSTAL_FLUORITE_GREEN, ItemGroup.DECORATIONS);
//		registerBlock("cave_crystal_fluorite_purple", CAVE_CRYSTAL_FLUORITE_PURPLE, ItemGroup.DECORATIONS);
//		registerBlock("cave_crystal_fluorite_teal", CAVE_CRYSTAL_FLUORITE_TEAL, ItemGroup.DECORATIONS);

		// Feature
		registerFeature("cavern_chest", CAVERN_CHEST);
		registerFeature("spelunkers_chest", SPELUNKERS_CHEST);

		registerFeature("nether_chest", NETHER_CHEST);

		registerFeature("cave_spawner", CAVE_SPAWNER);
		registerFeature("rare_cave_spawner", RARE_CAVE_SPAWNER);
	}

	private void addGeneration() {
		// Vegetal decoration is one of the last stages, so adding there so as to not be disturbed by other stuff. Probably should have used underground structures tho

		// ================
		//    OVERWORLD
		// ================

		addFeatureTo(
				GenerationStep.Feature.VEGETAL_DECORATION,
				CAVERN_CHEST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(CONFIG.cavernChestRarity))),
				OVERWORLD);

		addFeatureTo(
				GenerationStep.Feature.VEGETAL_DECORATION,
				SPELUNKERS_CHEST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(CONFIG.spelunkersChestRarity))),
				OVERWORLD);

		addFeatureTo(
				GenerationStep.Feature.VEGETAL_DECORATION,
				CAVE_SPAWNER.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(CONFIG.normalSpawnerRarity))),
				OVERWORLD);

		addFeatureTo(
				GenerationStep.Feature.VEGETAL_DECORATION,
				RARE_CAVE_SPAWNER.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(CONFIG.rareSpawnerRarity))),
				OVERWORLD);

		// ================
		//      NETHER
		// ================

		addFeatureTo(
				GenerationStep.Feature.UNDERGROUND_DECORATION,
				NETHER_CHEST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(CONFIG.netherChestRarity))),
				NETHER);
	}

	private static <C extends FeatureConfig, F extends Feature<C>, T extends ConfiguredFeature<C, F>> void addFeatureTo(final GenerationStep.Feature step, final T feature, final Predicate<Biome> predicate) {
		Registry.BIOME.forEach(biome -> {
			if (predicate.test(biome)) {
				biome.addFeature(step, feature);
			}
		});
		RegistryEntryAddedCallback.event(Registry.BIOME).register((rawId, id, biome) -> {
			if (predicate.test(biome)) {
				biome.addFeature(step, feature);
			}
		});
	}

	private static void registerBlock(String id, Block block, ItemGroup group) {
		Registry.register(Registry.BLOCK, from(id), block);
		Registry.register(Registry.ITEM, from(id), new BlockItem(block, new Item.Settings().group(group)));
	}

	private static void registerFeature(String id, Feature<?> feature) {
		Registry.register(Registry.FEATURE, from(id), feature);
	}

	public static Identifier from(String s) {
		return new Identifier("spelunkers", s);
	}

	public static Block[] getCrystals() {
		return CrystalBlock.BLOCKS.toArray(new Block[0]);
	}
}
