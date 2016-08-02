package minecraftflightsimulator.planes.PZLP11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelPZLP11ElevatorL extends ModelBase{
	private static final float scale=0.0625F;
	
    ModelRenderer El1;
    ModelRenderer El2;
    ModelRenderer El3;
    ModelRenderer El4;
    ModelRenderer El5;
    ModelRenderer El6;
    ModelRenderer El7;
    ModelRenderer El8;
    ModelRenderer El9;
    ModelRenderer El10;
    ModelRenderer El11;
    ModelRenderer El12;
    ModelRenderer El13;
    ModelRenderer El14;
    ModelRenderer El15;
    ModelRenderer El16;
    ModelRenderer El17;
    ModelRenderer El18;
    ModelRenderer El19;
    ModelRenderer El20;
    ModelRenderer Ep1;
    ModelRenderer Ep2;
  
  public ModelPZLP11ElevatorL(){
    textureWidth = 64;
    textureHeight = 24;
    
      El1 = new ModelRenderer(this, 0, 15);
      El1.addBox(0F, 0F, 0F, 17, 2, 0);
      El1.setRotationPoint(-8F, 0F, 9F);
      El1.setTextureSize(64, 24);
      El1.mirror = true;
      setRotation(El1, 0F, 0F, 0F);
      El2 = new ModelRenderer(this, -9, 6);
      El2.addBox(0F, 0F, 0F, 17, 0, 9);
      El2.setRotationPoint(-8F, 0F, 0F);
      El2.setTextureSize(64, 24);
      El2.mirror = true;
      setRotation(El2, 0F, 0F, 0F);
      El3 = new ModelRenderer(this, -9, 6);
      El3.addBox(0F, 0F, 0F, 17, 0, 9);
      El3.setRotationPoint(-8F, 2F, 0F);
      El3.setTextureSize(64, 24);
      El3.mirror = true;
      setRotation(El3, 0F, 0F, 0F);
      El4 = new ModelRenderer(this, 0, -5);
      El4.addBox(0F, 0F, 0F, 0, 2, 7);
      El4.setRotationPoint(-11F, 0.001F, 2F);
      El4.setTextureSize(64, 24);
      El4.mirror = true;
      setRotation(El4, 0F, 0.4363323F, 0F);
      El5 = new ModelRenderer(this, 27, 10);
      El5.addBox(0F, 0F, 0F, 2, 0, 7);
      El5.setRotationPoint(9F, 2F, 0F);
      El5.setTextureSize(64, 24);
      El5.mirror = true;
      setRotation(El5, 0F, 0F, 0F);
      El6 = new ModelRenderer(this, 14, 0);
      El6.addBox(0F, 0F, 0F, 0, 2, 2);
      El6.setRotationPoint(-11F, 0F, 0F);
      El6.setTextureSize(64, 24);
      El6.mirror = true;
      setRotation(El6, 0F, 0F, 0F);
      El7 = new ModelRenderer(this, 32, 8);
      El7.addBox(0F, 0F, 0F, 3, 0, 2);
      El7.setRotationPoint(-11F, 0F, 0F);
      El7.setTextureSize(64, 24);
      El7.mirror = true;
      setRotation(El7, 0F, 0F, 0F);
      El8 = new ModelRenderer(this, 32, 8);
      El8.addBox(0F, 0F, 0F, 3, 0, 2);
      El8.setRotationPoint(-11F, 2F, 0F);
      El8.setTextureSize(64, 24);
      El8.mirror = true;
      setRotation(El8, 0F, 0F, 0F);
      El9 = new ModelRenderer(this, 32, 6);
      El9.addBox(0F, 0F, 0F, 2, 0, 2);
      El9.setRotationPoint(-10F, 0F, 2F);
      El9.setTextureSize(64, 24);
      El9.mirror = true;
      setRotation(El9, 0F, 0F, 0F);
      El10 = new ModelRenderer(this, 32, 6);
      El10.addBox(0F, 0F, 0F, 2, 0, 2);
      El10.setRotationPoint(-10F, 2F, 2F);
      El10.setTextureSize(64, 24);
      El10.mirror = true;
      setRotation(El10, 0F, 0F, 0F);
      El11 = new ModelRenderer(this, 20, 2);
      El11.addBox(0F, 0F, 0F, 1, 0, 2);
      El11.setRotationPoint(-9F, 2F, 4F);
      El11.setTextureSize(64, 24);
      El11.mirror = true;
      setRotation(El11, 0F, 0F, 0F);
      El12 = new ModelRenderer(this, 20, 2);
      El12.addBox(0F, 0F, 0F, 1, 0, 2);
      El12.setRotationPoint(-9F, 0F, 4F);
      El12.setTextureSize(64, 24);
      El12.mirror = true;
      setRotation(El12, 0F, 0F, 0F);
      El13 = new ModelRenderer(this, 31, 10);
      El13.addBox(0F, 0F, 0F, 2, 0, 7);
      El13.setRotationPoint(-11F, 2.001F, 2F);
      El13.setTextureSize(64, 24);
      El13.mirror = true;
      setRotation(El13, 0F, 0.4363323F, 0F);
      El14 = new ModelRenderer(this, 31, 10);
      El14.addBox(0F, 0F, 0F, 2, 0, 7);
      El14.setRotationPoint(-11F, 0.001F, 2F);
      El14.setTextureSize(64, 24);
      El14.mirror = true;
      setRotation(El14, 0F, 0.4363323F, 0F);
      El15 = new ModelRenderer(this, 0, -7);
      El15.addBox(0F, 0F, 0F, 0, 2, 7);
      El15.setRotationPoint(11F, 0F, 0F);
      El15.setTextureSize(64, 24);
      El15.mirror = true;
      setRotation(El15, 0F, 0F, 0F);
      El16 = new ModelRenderer(this, 27, 10);
      El16.addBox(0F, 0F, 0F, 2, 0, 7);
      El16.setRotationPoint(9F, 0F, 0F);
      El16.setTextureSize(64, 24);
      El16.mirror = true;
      setRotation(El16, 0F, 0F, 0F);
      El17 = new ModelRenderer(this, 14, 0);
      El17.addBox(0F, 0F, 0F, 0, 2, 2);
      El17.setRotationPoint(9F, 0F, 7F);
      El17.setTextureSize(64, 24);
      El17.mirror = true;
      setRotation(El17, 0F, 0F, 0F);
      El18 = new ModelRenderer(this, 18, 3);
      El18.addBox(0F, 0F, 0F, 2, 1, 0);
      El18.setRotationPoint(9F, 0F, 7F);
      El18.setTextureSize(64, 24);
      El18.mirror = true;
      setRotation(El18, 0F, 0F, 0F);
      El19 = new ModelRenderer(this, 0, 4);
      El19.addBox(0F, 0F, 0F, 22, 2, 0);
      El19.setRotationPoint(-11F, 0F, 0F);
      El19.setTextureSize(64, 24);
      El19.mirror = true;
      setRotation(El19, 0F, 0F, 0F);
      El20 = new ModelRenderer(this, 38, 5);
      El20.addBox(0F, 0F, 0F, 0, 2, 1);
      El20.setRotationPoint(-8F, 0F, 8F);
      El20.setTextureSize(64, 24);
      El20.mirror = true;
      setRotation(El20, 0F, 0F, 0F);
      Ep1 = new ModelRenderer(this, 0, 17);
      Ep1.addBox(0F, 0F, 0F, 17, 1, 1);
      Ep1.setRotationPoint(-8F, 1F, 9F);
      Ep1.setTextureSize(64, 24);
      Ep1.mirror = true;
      setRotation(Ep1, 0F, 0F, 0F);
      Ep2 = new ModelRenderer(this, 0, 19);
      Ep2.addBox(0F, 0F, 0F, 2, 1, 1);
      Ep2.setRotationPoint(9F, 1F, 7F);
      Ep2.setTextureSize(64, 24);
      Ep2.mirror = true;
      setRotation(Ep2, 0F, 0F, 0F);
  }
  
  public void render(){
    El1.render(scale);
    El2.render(scale);
    El3.render(scale);
    El4.render(scale);
    El5.render(scale);
    El6.render(scale);
    El7.render(scale);
    El8.render(scale);
    El9.render(scale);
    El10.render(scale);
    El11.render(scale);
    El12.render(scale);
    El13.render(scale);
    El14.render(scale);
    El15.render(scale);
    El16.render(scale);
    El17.render(scale);
    El18.render(scale);
    El19.render(scale);
    El20.render(scale);
    Ep1.render(scale);
    Ep2.render(scale);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z){
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}