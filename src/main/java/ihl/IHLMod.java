package ihl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import ihl.items_blocks.BlocksAndItems;
import ihl.recipes.IHLRecipes;
import org.apache.logging.log4j.Logger;
import codechicken.nei.NEIModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import ic2.core.util.StackUtil;
import ihl.enviroment.LaserHitMirrorEventHandler;
import ihl.explosion.ChunkAndWorldLoadEventHandler;
import ihl.explosion.ExplosionVectorBlockV2;
import ihl.explosion.IHLEntityFallingPile;
import ihl.flexible_cable.IHLENet;
import ihl.flexible_cable.NodeEntity;
import ihl.flexible_cable.PowerCableNodeEntity;
import ihl.items_blocks.IHLBucketHandler;
import ihl.nei_integration.NEIIHLConfig;
import ihl.servitor.LostHeadEntity;
import ihl.utils.EntityDropEventHandler;
import ihl.utils.FluidDictionary;
import ihl.utils.IHLUtils;
import ihl.worldgen.IHLWorldGenerator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import static ihl.items_blocks.BlocksAndItems.*;

@Mod(modid = IHLModInfo.MODID, name = IHLModInfo.MODNAME, version = IHLModInfo.MODVERSION, dependencies = "required-after:IC2@[2.2.767-experimental,)")
public class IHLMod implements IFuelHandler {

	@SidedProxy(clientSide = "ihl.ClientProxy", serverSide = "ihl.ServerProxy")
	public static ServerProxy proxy;
	public static IHLModConfig config;
	public static IHLENet enet;
	// This used to determine if GregTech mod presented on server and load
	// GregTech recipes.
	public static boolean isGregTechModLoaded = false;
	public static boolean isGT_API_Version_5 = false;

	public static Logger log;

	public static FluidDictionary fluidDictionary;
	public static Map<String, Integer> moltenAmounts = new HashMap<String, Integer>();
	public static ExplosionVectorBlockV2 explosionHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException, ParserConfigurationException {
		fluidDictionary = new FluidDictionary();
		log = evt.getModLog();
		IHLMod.config = new IHLModConfig(evt);

		GameRegistry.registerFuelHandler(this);

		BlocksAndItems.RegisterBlocksAndItems();

		GameRegistry.registerWorldGenerator(new IHLWorldGenerator(), 0);

		IHLMod.enet = new IHLENet();
		MinecraftForge.EVENT_BUS.register(new EntityDropEventHandler());
		MinecraftForge.EVENT_BUS.register(new LaserHitMirrorEventHandler());
		MinecraftForge.EVENT_BUS.register(new IHLBucketHandler());
		MinecraftForge.EVENT_BUS.register(proxy);
		FMLCommonHandler.instance().bus().register(proxy);

		MinecraftForge.EVENT_BUS.register(new ChunkAndWorldLoadEventHandler());
		IHLMod.log.info("IHL precalculating explosion.");
		IHLMod.explosionHandler = new ExplosionVectorBlockV2();
		IHLMod.log.info("Explosion calculated.");
		proxy.load();
		registerEntities();
		OreDictionary.registerOre("ingotBrick", Items.brick);
		OreDictionary.registerOre("dustGunpowder", Items.gunpowder);
		OreDictionary.registerOre("toolLighter", new ItemStack(Items.flint_and_steel, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("charcoal", new ItemStack(Items.coal, 1, 1));
		OreDictionary.registerOre("blockDirt", new ItemStack(Blocks.dirt, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("blockDirt", new ItemStack(Blocks.grass, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("platePaper", new ItemStack(Items.paper, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("dustGunpowder", new ItemStack(Items.gunpowder, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("blockExplosive", IHLUtils.getThisModItemStack("ihlExplosive"));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) throws IOException {
		ic2Leaves = StackUtil.getBlock(IC2Items.getItem("rubberLeaves"));
		ic2Wood = StackUtil.getBlock(IC2Items.getItem("rubberWood"));
		if (!IHLMod.config.skipRecipeLoad) {

			isGregTechModLoaded = Loader.isModLoaded("gregtech");
			if (isGregTechModLoaded) {
				try {
					Class.forName("gregapi.GT_API");
				} catch (ClassNotFoundException e) {
					isGT_API_Version_5 = true;
				}
			}
			IHLRecipes.RegisterRecipes();
			IHLMod.config.loadRecipeModificators();
		}
		if (Loader.isModLoaded("NotEnoughItems")) {
			NEIModContainer.plugins.add(new NEIIHLConfig());
		}
		IHLMod.proxy.initBlockRenderer();
		IHLMod.log.info("IHL loaded.");
	}

	private void registerEntities() {
		EntityRegistry.registerModEntity(PowerCableNodeEntity.class, "PowerCableNodeEntity", 2, this, 80, 3, true);
		EntityRegistry.registerModEntity(NodeEntity.class, "NodeEntity", 3, this, 80, 3, true);
		EntityRegistry.registerModEntity(IHLEntityFallingPile.class, "IHLEntityFallingPile", 4, this, 80, 3, true);
		EntityRegistry.registerGlobalEntityID(LostHeadEntity.class, "LostHead",
				EntityRegistry.findGlobalUniqueEntityId(), 0x0033FF, 0x00CCFF);
		if (IHLMod.config.enableWailers) {
			BiomeGenBase[] biomes = BiomeDictionary.getBiomesForType(Type.END);
			for (int i = 0; i < biomes.length; i++) {
				if (biomes[i].biomeName.equals("Sky")) {
					EntityRegistry.addSpawn(LostHeadEntity.class, 50, 1, 10, EnumCreatureType.monster, biomes[i]);
				}
			}
		}
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		if (IHLUtils.getFirstOreDictName(stack) == "ingotTarPitch") {
			return 2000;
		}
		if (IHLUtils.getFirstOreDictName(stack) == "dustSodiumZeoliteCoked") {
			return 500;
		} else if (IHLUtils.getFirstOreDictName(stack) == "nuggetTarPitch") {
			return 222;
		} else if (IHLUtils.getFirstOreDictName(stack) == "dustSulfur") {
			return 160;
		}
		return 0;
	}
}
