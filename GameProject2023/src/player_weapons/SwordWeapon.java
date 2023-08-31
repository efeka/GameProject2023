package player_weapons;

import java.awt.Rectangle;
import java.util.ArrayList;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import items.Item;
import items.SwordItem;
import window.Animation;

public class SwordWeapon extends Weapon {

	private Animation[] idleAnimation;
	private Animation[] runAnimation;

	public SwordWeapon(ObjectHandler objectHandler) {
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
		Animation attackRightAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordAttack, 1),
				attackDelay, true);
		Animation attackLeftAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordAttack, -1),
				attackDelay, true);
		abilities[0] = new WeaponAbility(500, 20, new Animation[] {attackRightAnim, attackLeftAnim});

		// Sword stab
		int stabDelay = 6;
		Animation stabRightAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordStab, 1),
				stabDelay, true);
		Animation stabLeftAnim = new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerSwordStab, -1),
				stabDelay, true);
		abilities[1] = new WeaponAbility(1500, 35, new Animation[] {stabRightAnim, stabLeftAnim});
	}

	@Override
	public void useAbility(int index) {
		if (!isAbilityIndexValid(index))
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());

		WeaponAbility ability = abilities[index];
		if (ability.getAnimation(0).isPlayedOnce() || ability.getAnimation(1).isPlayedOnce()) {
			if (ability.isOnCooldown())
				return;
			else
				ability.resetAbility();
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

	@Override
	public Animation[] getIdleAnimation() {
		return idleAnimation;
	}

	@Override
	public Animation[] getRunAnimation() {
		return runAnimation;
	}

	@Override
	public Item createItemFromWeapon(float x, float y) {
		return new SwordItem(x, y, objectHandler);
	}

}
