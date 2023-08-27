package player_weapons;

import java.awt.Rectangle;
import java.util.ArrayList;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class Fisticuffs extends Weapon {

	private Animation[] idleAnimation;
	private Animation[] runAnimation;

	public Fisticuffs(ObjectHandler objectHandler) {
		super(objectHandler);
		setupAnimations();
		setupAbilities();
	}

	@Override
	protected void setupAbilities() {
		abilities = new WeaponAbility[2];
		TextureLoader textureLoader = TextureLoader.getInstance();

		int attackDelay = 6;
		Animation attackRightAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerAttack, 1),
				attackDelay, true);
		Animation attackLeftAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerAttack, -1),
				attackDelay, true);
		abilities[0] = new WeaponAbility(500, 15, new Animation[] {attackRightAnim, attackLeftAnim});
		
		int uppercutDelay = 5;
		Animation uppercutRightAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerUppercut, 1),
				uppercutDelay, true);
		Animation uppercutLeftAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerUppercut, -1),
				uppercutDelay, true);
		abilities[1] = new WeaponAbility(1500, 30, new Animation[] {uppercutRightAnim, uppercutLeftAnim});
	}

	@Override
	public void useAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());

		WeaponAbility ability = abilities[index];
		if (!ability.isAbilityBeingUsed()) {
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
			int currentAnimFrame1 = ability.getAnimation(0).getCurrentFrame();
			int currentAnimFrame2 = ability.getAnimation(1).getCurrentFrame();
			if (currentAnimFrame1 != 4 && currentAnimFrame1 != 5 && currentAnimFrame2 != 4 && currentAnimFrame2 != 5)
				break;
			
			for (int i = midLayer.size() - 1; i >= 0; i--) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getAttackBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(abilities[index].getDamage());
						float knockbackVelX = 3f * player.getDirection();
						float knockbackVelY = -1f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
					}
				}
			}
			break;
			
		case 1:
			// Uppercut
			player.setVelX(0);
			for (int i = midLayer.size() - 1; i >= 0; i--) {
				GameObject other = midLayer.get(i);
				if (other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
					if (getUppercutBounds().intersects(other.getBounds())) {
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(abilities[index].getDamage());
						float knockbackVelX = player.getVelX();
						float knockbackVelY = -7f;
						otherCreature.applyKnockback(knockbackVelX, knockbackVelY);

						player.setVelX(knockbackVelX);
						player.setVelY(knockbackVelY);
						player.setY(player.getY() - 2);
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

		TextureLoader textureLoader = TextureLoader.getInstance();

		final int idleDelay = 8;
		final int runDelay = 8;

		idleAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, 1),
				idleDelay, false);
		idleAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, -1),
				idleDelay, false);
		runAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, 1),
				runDelay, false);
		runAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, -1),
				runDelay, false);
	}

	@Override
	public Animation[] getIdleAnimation() {
		return idleAnimation;
	}

	@Override
	public Animation[] getRunAnimation() {
		return runAnimation;
	}

}
