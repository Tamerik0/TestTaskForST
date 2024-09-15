package org.necr0manth.task_st.init;

import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.ProvideRecipes;
import org.zeith.hammerlib.api.IRecipeProvider;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

@ProvideRecipes
public class Recipes implements IRecipeProvider
{
    @Override
    public void provideRecipes(RegisterRecipesEvent event)
    {
        event.shaped()
                .result(Items.LIGHTNING_WAND)
                .shape( "  d",
                        " n ",
                        "n  ")
                .map('n', Tags.Items.NUGGETS_IRON)
                .map('d', Tags.Items.GEMS_DIAMOND)
                .register();
    }
}