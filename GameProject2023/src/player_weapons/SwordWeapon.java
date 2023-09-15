package player_weapons;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstract_templates.Creature;
import abstract_templates.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.RatSummon;
import items.SwordItem;
import items.WeaponItem;
import visual_effects.FadingTrailEffect;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

public class SwordWeapon extends Weapon {

	enum SwordState {
		None(-1),
		AttackChain1(0),
		AttackChain2(1),
		SwordDash(2),
		SpikeChain(3),
		IceSwords(4),
		PoisonRatSummon(5);
		
		private int index;
		
		private SwordState(int value) {
			this.index = value;
		}
		
		public int getIndex() {
			return index;
		}
	}
	private SwordState state = SwordState.None;
	private Animation currentAnimation;

	private WeaponAbility[] abilities;
	
	private boolean dashing = false;
	private float dashSpeedX = 10f;
	private long dashStartTimer;
	private int dashLengthMillis = 150;

	private boolean spawnedSpikes = false;
	private boolean spawnedIceSwords = false;
	private boolean spawnedPoisonRats = false;
	
	public SwordWeapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(objectHandler, keyInput, mouseInput);
		setupAbilities();
	}

	@Override
	public void tick() {
		switch (state) {
		case None:
			currentAnimation = null;
			for (WeaponAbility weaponAbility : abilities) {
				if (!weaponAbility.isOnCooldown())
					weaponAbility.resetAnimations();
			}
			
			if (mouseInput.isAttackButtonPressed() && !abilities[0].isOnCooldown())
				state = SwordState.AttackChain1;
//			else if (keyInput.isFirstAbilityKeyPressed() && !abilities[2].isOnCooldown())
//				state = SwordState.SwordDash;
			else if (keyInput.isFirstAbilityKeyPressed() && !abilities[5].isOnCooldown())
				state = SwordState.PoisonRatSummon;
//			else if (keyInput.isSecondAbilityKeyPressed() && !abilities[3].isOnCooldown())
//				state = SwordState.SpikeChain;
			else if (keyInput.isSecondAbilityKeyPressed() && !abilities[4].isOnCooldown())
				state = SwordState.IceSwords;
			break;
			
		case AttackChain1:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				if (mouseInput.isAttackButtonPressed()) {
					state = SwordState.AttackChain2;
					break;
				}
				else {
					abilities[state.getIndex()].startCooldown();
					state = SwordState.None;
					break;
				}
			}
			
			if (currentAnimation.getCurrentFrame() == 2 || currentAnimation.getCurrentFrame() == 3)
				checkEnemyCollision(getChainAttackBounds(), abilities[state.getIndex()].getDamage(), 0, 0, 400);
			player.setVelX(0);
			break;
			
		case AttackChain2:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[SwordState.AttackChain1.getIndex()].startCooldown();
				state = SwordState.None;
				break;
			}
			
			if (currentAnimation.getCurrentFrame() == 4)
				checkEnemyCollision(getChainAttackBounds(), abilities[state.getIndex()].getDamage(), 4 * player.getDirection(), -2f, 400);
			player.setVelX(0);
			break;
			
		case SwordDash:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			// Dash started
			if (!dashing) {
				dashing = true;
				dashStartTimer = System.currentTimeMillis();
				player.setLockMovementInputs(true);
				player.setVelX(dashSpeedX * player.getDirection());
				abilities[state.getIndex()].startCooldown();
			}
			// Dash ended or player got knocked back
			if (player.isKnockedBack() || System.currentTimeMillis() - dashStartTimer > dashLengthMillis) {
				state = SwordState.None;
				dashing = false;
				player.setLockMovementInputs(false);
				break;
			}
			
			player.setVelY(0f);
			checkEnemyCollision(getSwordDashBounds(), abilities[state.getIndex()].getDamage(), 0f, 0f, 700);
			
			// Add the dash trail effect
			int dashTrailImageIndex = player.getDirection() == 1 ? 1 : 3;
			int playerWidth = player.getWidth();
			int playerHeight = player.getHeight();
			BufferedImage dashTrailImage = TextureLoader.getInstance().getTextures(TextureName.PlayerSwordDash)[dashTrailImageIndex]; 
			objectHandler.addObject(new FadingTrailEffect(player.getX() - playerWidth / 2, player.getY() - playerHeight / 2,
					playerWidth * 2, playerHeight * 2, dashTrailImage, 0.05f, objectHandler), ObjectHandler.TOP_LAYER);
			break;
			
		case SpikeChain:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = SwordState.None;
				spawnedSpikes = false;
				break;
			}
			
			if (!spawnedSpikes) {
				spawnedSpikes = true;
				objectHandler.addObject(new ChainSpikeAttack(player.getX(),
						player.getY() + player.getHeight() - GameConstants.ScaleConstants.TILE_SIZE,
						player.getDirection(), objectHandler),
						ObjectHandler.MIDDLE_LAYER);
			}
			
			break;
			
		case IceSwords:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = SwordState.None;
				spawnedIceSwords = false;
				break;
			}
			
			if (!spawnedIceSwords) {
				spawnedIceSwords = true;
				
				int swordCount = 3;
					objectHandler.addObject(new SwordIceAttack(player,
						abilities[state.getIndex()].getDamage(), swordCount, 1f, 70, (float) Math.PI / 90,
						objectHandler), ObjectHandler.TOP_LAYER);
			}
			break;
			
			// TODO incomplete
		case PoisonRatSummon:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = SwordState.None;
				spawnedPoisonRats = false;
				break;
			}
			
			if (!spawnedPoisonRats) {
				spawnedPoisonRats = true;
				objectHandler.addObject(new RatSummon((int) player.getX() + TILE_SIZE * 3, (int) player.getY(), 
						5, 30, objectHandler), ObjectHandler.MIDDLE_LAYER);
			}
			
			break;
		}
	}

	private void checkEnemyCollision(Rectangle attackBounds, int damage, float knockbackVelX,
			float knockbackVelY, int enemyInvulnerabilityDuration) {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			
			if (attackBounds.intersects(other.getBounds()) && other.getObjectId().getCategory() == Category.Enemy) {
				Creature otherCreature = (Creature) other;
				if (knockbackVelX != 0 || knockbackVelY != 0)
					otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
				otherCreature.takeDamage(damage, enemyInvulnerabilityDuration);
			}
		}
	}
	
	private Rectangle getChainAttackBounds() {
		int x = (int) player.getX();
		int y = (int) player.getY();
		int width = player.getWidth();
		int height = player.getHeight();

		int attackX;
		int attackWidth = (int) (4f * width / 5);
		if (player.getDirection() == 1)
			attackX = (int) x + width / 2;
		else
			attackX = (int) x - attackWidth / 2;
		return new Rectangle(attackX, (int) y, attackWidth, height);
	}
	
	private Rectangle getSwordDashBounds() {
		int playerWidth = player.getWidth();
		int playerHeight = player.getHeight();
		return new Rectangle((int) (player.getX() - playerWidth / 2), (int) (player.getY() - playerHeight / 2),
				playerWidth * 2, playerHeight * 2);
	}
	
	@Override
	public boolean isUsingAbility() {
		return state != SwordState.None;
	}

	@Override
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	@Override
	protected void setupAnimations() {
		idleAnimation = new Animation[2];
		runAnimation = new Animation[2];

		TextureLoader textureLoader = TextureLoader.getInstance();

		final int idleDelay = 8;
		final int runDelay = 8;

		idleAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordIdle, 1),
				idleDelay, false);
		idleAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordIdle, -1),
				idleDelay, false);
		runAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordRun, 1),
				runDelay, false);
		runAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordRun, -1),
				runDelay, false);
	}

	private void setupAbilities() {
		abilities = new WeaponAbility[6];
		TextureLoader tex = TextureLoader.getInstance();

		// Combo Chain 1
		int attackDelay = 5;
		Animation[] attackAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordAttack, 1), attackDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordAttack, -1), attackDelay, true),
		};
		abilities[0] = new WeaponAbility(500, 15, attackAnims);
		
		// Combo Chain 2
		int stabDelay = 5;
		Animation[] stabAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordStab, 1), stabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordStab, -1), stabDelay, true),
		};
		abilities[1] = new WeaponAbility(1000, 20, stabAnims);
		
		// Sword Dash
		BufferedImage[] dashSprites = tex.getTextures(TextureName.PlayerSwordDash);
		Animation[] dashAnims = new Animation[] {
				new Animation(5, false, dashSprites[0]),
				new Animation(5, false, dashSprites[2]),
		};
		abilities[2] = new WeaponAbility(2000, 15, dashAnims);
		
		// TODO Ability 2
		Animation[] tempAnims2 = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, 1), stabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, -1), stabDelay, true),
		};
		abilities[3] = new WeaponAbility(2000, 15, tempAnims2);
		
		// Ice sword ability
		Animation[] tempAnims3 = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, 1), stabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, -1), stabDelay, true),
		};
		abilities[4] = new WeaponAbility(2000, 5, tempAnims3);
		
		// TODO Poison Rat Summon Ability
		Animation[] tempAnims4 = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, 1), stabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerHammerSwing, -1), stabDelay, true),
		};
		abilities[5] = new WeaponAbility(2000, 5, tempAnims4);
	}
	
	private int getIndexFromDirection() {
		return player.getDirection() == 1 ? 0 : 1;
	}

	@Override
	public WeaponItem createItemFromWeapon(float x, float y) {
		return new SwordItem(x, y, keyInput, mouseInput, objectHandler);
	}

}
