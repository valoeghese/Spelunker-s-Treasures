package tk.valoeghese.spelunkers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import net.fabricmc.loader.api.FabricLoader;
import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.Container;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class SpelunkersConfig {
	public int
	cavernChestRarity, spelunkersChestRarity, netherChestRarity,
	normalSpawnerRarity, rareSpawnerRarity;

	public static SpelunkersConfig load() {
		File file = new File(FabricLoader.getInstance().getConfigDirectory().getPath() + "/spelunkers.cfg");	
		SpelunkersConfig result = new SpelunkersConfig();

		boolean write;

		try {
			// if new file was created (i.e. doesn't already exist), write to it
			write = file.createNewFile();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		// load config
		WritableConfig loaded = ZoesteriaConfig.loadConfigWithDefaults(file, defaults());

		Container chestGeneration = loaded.getContainer("generation.chests");
		Container spawnerGeneration = loaded.getContainer("generation.spawners");

		// set values from config
		result.cavernChestRarity = chestGeneration.getIntegerValue("cavernChestRarity");
		result.spelunkersChestRarity = chestGeneration.getIntegerValue("spelunkersChestRarity");
		result.netherChestRarity = chestGeneration.getIntegerValue("netherChestRarity");

		result.normalSpawnerRarity = spawnerGeneration.getIntegerValue("normalSpawnerRarity");
		result.rareSpawnerRarity = spawnerGeneration.getIntegerValue("rareSpawnerRarity");

		// write to file if newly created
		if (write) {
			loaded.writeToFile(file);
		}

		return result;
	}

	private static ConfigTemplate defaults() {
		return ConfigTemplate.builder()
				.addContainer("generation", c0 -> c0
						.addContainer("chests", c -> c
								.addDataEntry("cavernChestRarity", "3")
								.addDataEntry("spelunkersChestRarity", "7")
								.addDataEntry("netherChestRarity", "6"))
						.addContainer("spawners", c -> c
								.addDataEntry("normalSpawnerRarity", "9")
								.addDataEntry("rareSpawnerRarity", "15")))
				.build();
	}
}
