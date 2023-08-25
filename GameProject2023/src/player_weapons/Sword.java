package player_weapons;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import window.Animation;

public class Sword extends Weapon {

	private PlayerAbility[] abilities;

	public Sword(ObjectHandler objectHandler) {
		super(objectHandler);
		setupAbilities();
	}

	private void setupAbilities() {
		abilities = new PlayerAbility[3];
		TextureLoader textureLoader = TextureLoader.getInstance();

		BufferedImage[] swordUppercutSprites = textureLoader.playerAttackSprites;
		Animation uppercutAnim = new Animation(5, true, swordUppercutSprites[0], swordUppercutSprites[1],
				swordUppercutSprites[2], swordUppercutSprites[3], swordUppercutSprites[4], swordUppercutSprites[5]);
		PlayerAbility uppercutAbility = new PlayerAbility(2000, new Animation[] {uppercutAnim, uppercutAnim});
		abilities[0] = uppercutAbility;

		BufferedImage[] swordStabSprites = textureLoader.playerSwordStabSprites;
		Animation stabRightAnim = new Animation(5, true, swordStabSprites[0], swordStabSprites[1], swordStabSprites[2],
				swordStabSprites[3], swordStabSprites[4], swordStabSprites[4], swordStabSprites[5], swordStabSprites[6]);
		Animation stabLeftAnim = new Animation(5, true, swordStabSprites[7], swordStabSprites[8], swordStabSprites[9],
				swordStabSprites[10], swordStabSprites[11], swordStabSprites[11], swordStabSprites[12], swordStabSprites[13]);
		PlayerAbility stabAbility = new PlayerAbility(500, new Animation[] {stabRightAnim, stabLeftAnim});
		abilities[1] = stabAbility;
	}

	@Override
	public void useAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());

		PlayerAbility ability = abilities[index];
		if (!ability.isAbilityBeingUsed()) {
			if (ability.isOnCooldown())
				return;
			else
				abilities[index].startAbility();
		}

		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		switch (index) {
		case 0:
			// Uppercut
			player.setVelX(0);
			for (int i = 0; i < midLayer.size(); i++) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getUppercutBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(10);
						float knockbackVelX = player.getVelX();
						float knockbackVelY = -7f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);

						player.setVelX(knockbackVelX);
						player.setVelY(knockbackVelY);
						player.setY(player.getY() - 5);
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

			for (int i = 0; i < midLayer.size(); i++) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getStabBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(10);
						float knockbackVelX = 5f * player.getDirection();
						float knockbackVelY = -2f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
					}
				}
			}
			break;
		case 2:
			break;
		}

	}

	private Rectangle getUppercutBounds() {
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
	public PlayerAbility getAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());
		return abilities[index];
	}

	private boolean isAbilityIndexValid(int index) {
		return index >= 0 && index < abilities.length && abilities[index] != null;
	}

}
