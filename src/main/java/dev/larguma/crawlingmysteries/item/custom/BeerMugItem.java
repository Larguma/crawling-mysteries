package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import dev.larguma.crawlingmysteries.block.custom.BeerMugBlock;
import dev.larguma.crawlingmysteries.block.entity.BeerMugBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import dev.larguma.crawlingmysteries.client.item.BeerMugItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BeerMugItem extends BlockItem implements GeoItem {
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

  public BeerMugItem(Block block, Properties properties) {
    super(block, properties);
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player != null && !player.isCrouching()) {
      return InteractionResult.PASS;
    }

    Level level = context.getLevel();
    BlockPos clickedPos = context.getClickedPos();
    BlockState clickedState = level.getBlockState(clickedPos);

    if (player != null && player.isCrouching() && clickedState.is(this.getBlock())) {
      int mugs = clickedState.getValue(BeerMugBlock.MUGS);
      if (mugs < 4) {
        if (!level.isClientSide) {
          BlockState newState = clickedState.setValue(BeerMugBlock.MUGS, mugs + 1);
          level.setBlock(clickedPos, newState, 3);

          SoundType soundtype = newState.getSoundType(level, clickedPos, player);
          level.playSound(null, clickedPos, soundtype.getPlaceSound(), SoundSource.BLOCKS, soundtype.getVolume(),
              soundtype.getPitch());

          if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
          }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }

    return super.useOn(context);
  }

  @Override
  public int getUseDuration(ItemStack stack, LivingEntity entity) {
    return 32;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.DRINK;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
    ItemStack stack = player.getItemInHand(usedHand);
    int currentLevel = getBeerLevel(stack);
    if (currentLevel > 0) {
      if (!level.isClientSide) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
      }
      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    return super.use(level, player, usedHand);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
    Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
    if (player instanceof ServerPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
    }

    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
      setBeerLevel(stack, getBeerLevel(stack) - 1);
    }

    livingEntity.gameEvent(GameEvent.DRINK);
    return stack;
  }

  public int getBeerLevel(ItemStack stack) {
    CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
    CompoundTag tag = customData.copyTag();
    return tag.contains(BeerMugBlockEntity.BEER_LEVEL_KEY) ? tag.getInt(BeerMugBlockEntity.BEER_LEVEL_KEY) : 4;
  }

  public void setBeerLevel(ItemStack stack, int level) {
    CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
    CompoundTag tag = customData.copyTag();
    tag.putInt(BeerMugBlockEntity.BEER_LEVEL_KEY, level);
    tag.putString("id", "crawlingmysteries:beer_mug_be");
    stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
  }

  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private BeerMugItemRenderer renderer;

      @Override
      public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (this.renderer == null)
          this.renderer = new BeerMugItemRenderer();
        return this.renderer;
      }
    });
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, this::idleAnimController));
  }

  protected <E extends BeerMugItem> PlayState idleAnimController(final AnimationState<E> state) {
    return state.setAndContinue(IDLE_ANIM);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    if (getBeerLevel(stack) == 0) {
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.beer_mug.empty"));
    } else {
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.beer_mug.tooltip"));
      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.beer_mug.beer_level", getBeerLevel(stack))
          .withStyle(ChatFormatting.GOLD));
    }

    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }
}
