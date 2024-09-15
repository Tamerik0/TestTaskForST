package org.necr0manth.task_st;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;

@Mod(TaskStMod.MOD_ID)
public class TaskStMod
{
	public static final String MOD_ID = "task_st";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public TaskStMod()
	{
		LanguageAdapter.registerMod(MOD_ID);
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}