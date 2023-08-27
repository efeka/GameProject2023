package player_weapons;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import window.Animation;

public class Sword extends Weapon {

	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;

	public Sword(ObjectHandler objectHandler) {
		super(objectHandler);
		setupAnimations();
		setupAbilities();
	}

	@Override
	protected void setupAbilities() {
		abilities = new WeaponAbility[2];
		TextureLoader textureLoader = TextureLoader.getInstance();

		// Regular attack
		int attackDelay = 4;
		BufferedImage[] swordAttackSprites = textureLoader.playerSwordAttackSprites;
		Animation attackRightAnim = new Animation(Arrays.copyOfRange(swordAttackSprites, 0, swordAttackSprites.length / 2), attackDelay, true); 
		Animation attackLeftAnim = new Animation(Arrays.copyOfRange(swordAttackSprites, swordAttackSprites.length / 2, swordAttackSprites.length), attackDelay, true);
		abilities[0] = new WeaponAbility(500, new Animation[] {attackRightAnim, attackLeftAnim});

		// Sword stab
		int stabDelay = 6;
		BufferedImage[] swordStabSprites = textureLoader.playerSwordStabSprites;
		Animation stabRightAnim = new Animation(Arrays.copyOfRange(swordStabSprites, 0, swordStabSprites.length / 2), stabDelay, true);
		Animation stabLeftAnim = new Animation(Arrays.copyOfRange(swordStabSprites, swordStabSprites.length / 2, swordStabSprites.length), stabDelay, true);
		abilities[1] = new WeaponAbility(1500, new Animation[] {stabRightAnim, stabLeftAnim});
	}

	@Override
	public void useAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());

		WeaponAbility ability = abilities[index];
		if (!ability.isAbilityBeingUsed()) {
			// Skips the rest of this method if the ability
			// is on cooldown and if its animation is done playing.
			if (ability.isOnCooldown())
				return;
			else
				abilities[index].startAbility();
		}

		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		switch (index) {
		
		case 0:
			// Attack
			player.setVelX(0);
			for (int i = midLayer.size() - 1; i >= 0; i--) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getAttackBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(abilities[index].getDamage());
						float knockbackVelX = 2f * player.getDirection();
						float knockbackVelY = -1f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
					}
				}
			}
			break;

		case 1:
			// Stab
			player.setVelX(0);
			int currentAnimFrame1 = ability.getAnimation(0).getCurrentFrame();
			int currentAnimFrame2 = ability.getAnimation(1).getCurrentFrame();
			if (currentAnimFrame1 != 4 && currentAnimFrame1 != 5 && currentAnimFrame2 != 4 && currentAnimFrame2 != 5)
				break;

			for (int i = midLayer.size() - 1; i >= 0; i--) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getStabBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(abilities[index].getDamage());
						float knockbackVelX = 5f * player.getDirection();
						float knockbackVelY = -2f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
					}
				}
			}
			break;
		}
	}

	private Rectangle getAttackBounds() {
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

	private Rectangle getStabBounds() {
		int attackX;
		int attackWidth = (int) (4f * player.getWidth() / 5);
		int playerX = (int) player.getX();
		int playerY = (int) player.getY();
		int playerWidth = player.getWidth();
		int playerHeight = player.getHeight();

		if (player.getDirection() == 1)
			attackX = playerX + playerWidth / 2;
		else
			attackX = (int) playerX - attackWidth / 2;
		return new Rectangle(attackX, playerY, attackWidth, playerHeight);
	}

	public Animation getAbilityAnimation(int abilityIndex, int animationIndex) {
		if (!isAbilityIndexValid(abilityIndex))
			throw new IndexOutOfBoundsException("Index " + abilityIndex + " is invalid for " + getClass());
		return abilities[abilityIndex].getAnimation(animationIndex);
	}

	@Override
	public WeaponAbility getAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());
		return abilities[index];
	}

	@Override
	protected void setupAnimations() {
		idleAnimation = new Animation[2];
		runAnimation = new Animation[2];
		attackAnimation = new Animation[2];

		TextureLoader textureLoader = TextureLoader.getInstance();

		BufferedImage[] idleSprites = textureLoader.playerSwordIdleSprites;
		BufferedImage[] runSprites = textureLoader.playerSwordRunSprites;
		BufferedImage[] attackSprites = textureLoader.playerSwordAttackSprites;

		final int idleDelay = 8;
		final int runDelay = 8;
		final int attackDelay = 4;

		idleAnimation[0] = new Animation(Arrays.copyOfRange(idleSprites, 0, idleSprites.length / 2), idleDelay, false);
		idleAnimation[1] = new Animation(Arrays.copyOfRange(idleSprites, idleSprites.length / 2, idleSprites.length), idleDelay, false);

		runAnimation[0] = new Animation(Arrays.copyOfRange(runSprites, 0, runSprites.length / 2), runDelay, false);
		runAnimation[1] = new Animation(Arrays.copyOfRange(runSprites, runSprites.length / 2, runSprites.length), runDelay, false);

		attackAnimation[0] = new Animation(Arrays.copyOfRange(attackSprites, 0, attackSprites.length / 2), attackDelay, true);
		attackAnimation[1] = new Animation(Arrays.copyOfRange(attackSprites, attackSprites.length / 2, attackSprites.length), attackDelay, true);
	}

	@Override
	public Animation[] getIdleAnimation() {
		return idleAnimation;
	}

	@Override
	public Animation[] getRunAnimation() {
		return runAnimation;
	}

	@Override
	public Animation[] getAttackAnimation() {
		return attackAnimation;
	}

}
