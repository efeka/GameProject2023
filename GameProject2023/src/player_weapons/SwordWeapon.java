package player_weapons;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstract_templates.Creature;
import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import items.SwordItem;
import items.WeaponItem;
import visual_effects.FadingTrailEffect;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class SwordWeapon extends Weapon {

	enum SwordState {
		None,
		AttackChain1,
		AttackChain2,
		SwordDash,
		Ability2,
	}
	private SwordState state = SwordState.None;
	private Animation currentAnimation;

	private WeaponAbility[] abilities;
	
	private boolean dashing = false;
	private float dashSpeedX = 10f;
	private long dashStartTimer;
	private int dashLengthMillis = 150;

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
			else if (keyInput.isFirstAbilityKeyPressed() && !abilities[2].isOnCooldown())
				state = SwordState.SwordDash;
			else if (keyInput.isSecondAbilityKeyPressed() && !abilities[3].isOnCooldown())
				state = SwordState.Ability2;
			break;
		case AttackChain1:
			currentAnimation = abilities[0].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				if (mouseInput.isAttackButtonPressed()) {
					state = SwordState.AttackChain2;
					break;
				}
				else {
					abilities[0].startCooldown();
					state = SwordState.None;
					break;
				}
			}
			
			if (currentAnimation.getCurrentFrame() == 2 || currentAnimation.getCurrentFrame() == 3)
				checkEnemyCollision(getChainAttackBounds(), abilities[0].getDamage(), 0, 0, 400);
			player.setVelX(0);
			break;
		case AttackChain2:
			currentAnimation = abilities[1].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[0].startCooldown();
				state = SwordState.None;
				break;
			}
			
			if (currentAnimation.getCurrentFrame() == 4)
				checkEnemyCollision(getChainAttackBounds(), abilities[1].getDamage(), 4 * player.getDirection(), -2f, 400);
			player.setVelX(0);
			break;
		case SwordDash:
			currentAnimation = abilities[2].getAnimations()[getIndexFromDirection()];
			// Dash started
			if (!dashing) {
				dashing = true;
				dashStartTimer = System.currentTimeMillis();
				player.setLockMovementInputs(true);
				player.setVelX(dashSpeedX * player.getDirection());
				abilities[2].startCooldown();
			}
			// Dash ended or player got knocked back
			if (player.isKnockedBack() || System.currentTimeMillis() - dashStartTimer > dashLengthMillis) {
				state = SwordState.None;
				dashing = false;
				player.setLockMovementInputs(false);
				break;
			}
			
			player.setVelY(0f);
			checkEnemyCollision(getSwordDashBounds(), abilities[2].getDamage(), 0f, 0f, 700);
			
			// Add the dash trail effect
			int dashTrailImageIndex = player.getDirection() == 1 ? 1 : 3;
			int playerWidth = player.getWidth();
			int playerHeight = player.getHeight();
			BufferedImage dashTrailImage = TextureLoader.getInstance().getTextures(TextureName.PlayerSwordDash)[dashTrailImageIndex]; 
			objectHandler.addObject(new FadingTrailEffect(player.getX() - playerWidth / 2, player.getY() - playerHeight / 2,
					playerWidth * 2, playerHeight * 2, dashTrailImage, 0.05f, objectHandler), ObjectHandler.TOP_LAYER);
			break;
		case Ability2:
			currentAnimation = abilities[3].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				state = SwordState.None;
				break;
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
		abilities = new WeaponAbility[4];
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
	}
	
	private int getIndexFromDirection() {
		return player.getDirection() == 1 ? 0 : 1;
	}

	@Override
	public WeaponItem createItemFromWeapon(float x, float y) {
		return new SwordItem(x, y, keyInput, mouseInput, objectHandler);
	}

}
