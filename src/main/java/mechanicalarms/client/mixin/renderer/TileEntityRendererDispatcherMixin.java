package mechanicalarms.client.mixin.renderer;

import mechanicalarms.client.renderer.InstanceRender;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityRendererDispatcher.class, remap = false)
public class TileEntityRendererDispatcherMixin {

    @Inject(method = "drawBatch", at = @At("TAIL"))
    public void draw(int pass, CallbackInfo ci) {
        InstanceRender.draw();
    }

}
