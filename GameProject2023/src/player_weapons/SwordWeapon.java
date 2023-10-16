package player_weapons;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import abstracts.GameObject;
import abstracts.Weapon;
import framework.Animation;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Explosion;
import game_objects.Player;
import player_weapons.abilities.ChainSpikeAttack;
import player_weapons.abilities.DarkSummon;
import player_weapons.abilities.SwordIceAttack;
import player_weapons.abilities.WeaponAbility;
import visual_effects.FadingTrailEffect;
import visual_effects.OneTimeAnimation;
import window.KeyInput;
import window.MouseInput;

public class SwordWeapon extends Weapon {

	enum ActionState {
		None(-1),
		AttackChain1(0),
		AttackChain2(1),
		LightningDash(2),
		SpikeChain(3),
		IceSwords(4),
		PoisonRatSummon(5);

		private int index;

		private ActionState(int value) {
			this.index = value;
		}

		public int getIndex() {
			return index;
		}
	}
	private ActionState state = ActionState.None;
	private Animation currentAnimation;

	private WeaponAbility[] abilities;

	private boolean dashing = false;
	private float dashSpeedX = 10f;
	private long dashStartTimer;
	private int dashLengthMillis = 170;
	private int dashLightningCount = 0;
	private HashSet<GameObject> enemiesHitByDash;

	private boolean spawnedSpikes = false;
	private boolean spawnedIceSwords = false;
	private boolean spawnedPoisonRats = false;
	private int ratCount = 2;

	public SwordWeapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(objectHandler, keyInput, mouseInput);
		enemiesHitByDash = new HashSet<>();
		setupAbilities();
	}

	@Override
	public void tick() {
		Player player = objectHandler.getPlayer();
		switch (state) {
		case None:
			currentAnimation = null;
			for (WeaponAbility weaponAbility : abilities) {
				if (!weaponAbility.isOnCooldown())
					weaponAbility.resetAnimations();
			}

			if (mouseInput.isAttackButtonPressed() && !abilities[ActionState.AttackChain1.index].isOnCooldown())
				state = ActionState.AttackChain1;
//			else if (keyInput.isFirstAbilityKeyPressed() && !abilities[ActionState.LightningDash.index].isOnCooldown())
//				state = ActionState.LightningDash;
						else if (keyInput.isFirstAbilityKeyPressed() && !abilities[ActionState.PoisonRatSummon.index].isOnCooldown())
							state = ActionState.PoisonRatSummon;
			//			else if (keyInput.isSecondAbilityKeyPressed() && !abilities[SwordState.SpikeChain.index].isOnCooldown())
			//				state = SwordState.SpikeChain;
			else if (keyInput.isSecondAbilityKeyPressed() && !abilities[ActionState.IceSwords.index].isOnCooldown())
				state = ActionState.IceSwords;
			break;

		case AttackChain1:
			/*
			 * First stage of the basic attack chain.
			 * The player deals damage to all enemies in a short range.
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				if (mouseInput.isAttackButtonPressed()) {
					state = ActionState.AttackChain2;
					break;
				}
				else {
					abilities[state.getIndex()].startCooldown();
					state = ActionState.None;
					break;
				}
			}

			if (currentAnimation.getCurrentFrame() == 2 || currentAnimation.getCurrentFrame() == 3)
				checkEnemyCollision(getAttackBounds(), null, abilities[state.getIndex()].getDamage(), 0, 0, 400);
			player.setVelX(0);
			break;

		case AttackChain2:
			/*
			 * Second stage of the basic attack chain.
			 * The player deals damage to all enemies in a short range and knocks them back.
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[ActionState.AttackChain1.getIndex()].startCooldown();
				state = ActionState.None;
				break;
			}

			if (currentAnimation.getCurrentFrame() == 4)
				checkEnemyCollision(getAttackBounds(), null, abilities[state.getIndex()].getDamage(),
						4 * player.getDirection(), -2f, 400);
			player.setVelX(0);
			break;

		case LightningDash:
			/*
			 * The player quickly dashes into the current direction and
			 * deals damage to enemies that it passes through.
			 * Then multiple lightning explosions happen in the dash path.
			 * Player moves in the dash direction while the ability is being cast.
			 */

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
				state = ActionState.None;
				dashing = false;
				dashLightningCount = 0;
				enemiesHitByDash.clear();
				player.setLockMovementInputs(false);
				break;
			}

			player.setVelY(0f);
			// Damage enemies that get hit by the dash
			checkEnemyCollision(getSwordDashBounds(), enemiesHitByDash, abilities[state.getIndex()].getDamage(),
					2f * player.getDirection(), -1f, 0);

			// Spawn lightning explosions at certain times during the dash
			dashLightningCount++;
			if (dashLightningCount % 4 == 0) {
				OneTimeAnimation lightningAnimation = new OneTimeAnimation(player.getX(),
						player.getY(), TILE_SIZE, player.getHeight(), TextureName.LightningEffect, 6, objectHandler);
				Explosion lightningExplosion = new Explosion(lightningAnimation, new int[] {6, 7}, 10,
						new Category[] {Category.Enemy}, objectHandler);
				objectHandler.addObject(lightningExplosion, ObjectHandler.MIDDLE_LAYER);
			}

			// Add the dash trail visual effect
			int dashTrailImageIndex = player.getDirection() == 1 ? 1 : 3;
			int playerWidth = player.getWidth();
			int playerHeight = player.getHeight();
			BufferedImage dashTrailImage = TextureLoader.getInstance().getTextures(TextureName.PlayerSwordDash)[dashTrailImageIndex]; 
			objectHandler.addObject(new FadingTrailEffect(player.getX() - playerWidth / 2, player.getY() - playerHeight / 2,
					playerWidth * 2, playerHeight * 2, dashTrailImage, 0.7f, 0.05f, objectHandler), ObjectHandler.TOP_LAYER);
			break;

		case SpikeChain:
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = ActionState.None;
				spawnedSpikes = false;
				break;
			}

			if (!spawnedSpikes) {
				spawnedSpikes = true;
				objectHandler.addObject(new ChainSpikeAttack(player.getX(),
						player.getY() + player.getHeight() - GameConstants.ScaleConstants.TILE_SIZE,
						player.getDirection(), objectHandler), ObjectHandler.MIDDLE_LAYER);
			}
			break;

		case IceSwords:
			/*
			 * Spawns swords of ice that rotate around the player.
			 * The swords deal damage to enemies on contact.
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = ActionState.None;
				spawnedIceSwords = false;
				break;
			}

			player.setVelX(0);
			player.setVelY(0);

			if (!spawnedIceSwords) {
				spawnedIceSwords = true;

				int swordCount = 3;
				objectHandler.addObject(new SwordIceAttack(player, abilities[state.getIndex()].getDamage(), 
						4000, swordCount, 1f, 70, (float) Math.PI / 90, objectHandler), ObjectHandler.TOP_LAYER);
			}
			break;

		case PoisonRatSummon:
			/*
			 * Multiple poison rats get spawned around the player.
			 * Each rat starts running around and attacking enemies.
			 * Rats explode upyyon death, dealing damage to all enemies around them.
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = ActionState.None;
				spawnedPoisonRats = false;
				break;
			}

			player.setVelX(0);
			player.setVelY(0);

			if (!spawnedPoisonRats) {
				spawnedPoisonRats = true;

				if (ratCount == 2) {
					int ratX = (int) player.getX() + player.getWidth();
					int ratHealth = 40;
					int ratDamage = 5;
					int ratExplosionDamage = 20;
					objectHandler.addObject(new DarkSummon(ratX, (int) player.getY(), 
							1, ratDamage, ratExplosionDamage, ratHealth, objectHandler), ObjectHandler.MIDDLE_LAYER);
					ratX = (int) player.getX() - TILE_SIZE; 
					objectHandler.addObject(new DarkSummon(ratX, (int) player.getY(), 
							-1, ratDamage, ratExplosionDamage, ratHealth, objectHandler), ObjectHandler.MIDDLE_LAYER);	
				}
			}
			break;
		}
	}

	public Rectangle getAttackBounds() {
		Player player = objectHandler.getPlayer();
		int px = (int) player.getX();
		int py = (int) player.getY();
		int pWidth = player.getWidth();
		int pHeight = player.getHeight();

		int attackX;
		int attackWidth = (int) (4f * pWidth / 5);
		if (player.getDirection() == 1)
			attackX = px + pWidth / 2;
		else
			attackX = px + pWidth / 2 - attackWidth;
		return new Rectangle(attackX, py, attackWidth, pHeight);
	}

	public Rectangle getSwordDashBounds() {
		Player player = objectHandler.getPlayer();
		int px = (int) player.getX();
		int py = (int) player.getY();
		int pWidth = player.getWidth();
		int pHeight = player.getHeight();
		
		int attackWidth = (int) (player.getWidth() * 1.2f);
		int attackX;
		if (player.getDirection() == 1)
			attackX = px + pWidth / 2;
		else
			attackX = px + pWidth / 2 - attackWidth;
		return new Rectangle(attackX, py, attackWidth, pHeight);
	}

	@Override
	public boolean isUsingAbility() {
		return state != ActionState.None;
	}

	@Override
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	@Override
	protected void setupBaseAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();
		final int idleDelay = 8;
		final int runDelay = 8;

		idleAnimation = new Animation[] { 
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordIdle, 1), idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordIdle, -1), idleDelay, false)
		};
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordRun, 1), runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordRun, -1), runDelay, false),
		};
		jumpSprites = textureLoader.getTextures(TextureName.PlayerSwordJump);
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
		abilities[0] = new WeaponAbility(300, 15, attackAnims);

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
		abilities[2] = new WeaponAbility(2000, 5, dashAnims);

		// TODO Ability 2
		Animation[] tempAnims2 = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordStab, 1), stabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerSwordStab, -1), stabDelay, true),
		};
		abilities[3] = new WeaponAbility(2000, 15, tempAnims2);

		// Ice sword ability
		int glowDelay = 6;
		Animation[] iceGlowAnims = new Animation[] {
				new Animation(tex.getTextures(TextureName.PlayerIceGlow), glowDelay, true),
				new Animation(tex.getTextures(TextureName.PlayerIceGlow), glowDelay, true),
		};
		abilities[4] = new WeaponAbility(2000, 5, iceGlowAnims);

		// Poison Rat Summon Ability
		Animation[] poisonGlowAnims = new Animation[] {
				new Animation(tex.getTextures(TextureName.PlayerSwordDarkGlow), glowDelay, true),
				new Animation(tex.getTextures(TextureName.PlayerSwordDarkGlow), glowDelay, true),
		};
		abilities[5] = new WeaponAbility(2000, 5, poisonGlowAnims);
	}

	private int getIndexFromDirection() {
		return objectHandler.getPlayer().getDirection() == 1 ? 0 : 1;
	}

}
