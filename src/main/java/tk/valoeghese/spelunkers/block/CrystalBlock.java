package tk.valoeghese.spelunkers.block;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;

public class CrystalBlock extends Block {
	public CrystalBlock(FabricBlockSettings settings) {
		super(settings.build());
		BLOCKS.add(this);
	}

	public static final List<Block> BLOCKS = new ArrayList<>();
}
