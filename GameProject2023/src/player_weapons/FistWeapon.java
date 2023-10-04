package player_weapons;

import java.awt.Rectangle;

import abstracts.Weapon;
import framework.ObjectHandler;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class FistWeapon extends Weapon {

	enum ActionState {
		None(-1),
		Attack(0);

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
	
	public FistWeapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(objectHandler, keyInput, mouseInput);
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

			if (mouseInput.isAttackButtonPressed() && !abilities[ActionState.Attack.index].isOnCooldown())
				state = ActionState.Attack;
			break;

		case Attack:
			/*
			 * The player deals damage to all enemies in a short range.
			 * Player cannot move while the ability is being cast. 
			 */
			
			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[state.getIndex()].startCooldown();
				state = ActionState.None;
				break;
			}

			if (currentAnimation.getCurrentFrame() == 4) {
				float knockbackX = player.getDirection() * 3f;
				float knockbackY = -2f;
				checkEnemyCollision(getAttackBounds(), null, abilities[state.getIndex()].getDamage(),
						knockbackX, knockbackY, 400);
			}
			
			if (!player.isFalling()) {
				player.setVelX(0);
				player.setVelY(0);
			}
			break;
		}
	}

	private Rectangle getAttackBounds() {
		Player player = objectHandler.getPlayer();
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

	@Override
	public boolean isUsingAbility() {
		return state != ActionState.None;
	}

	@Override
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	@Override
	protected void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();
		final int idleDelay = 8;
		final int runDelay = 8;

		idleAnimation = new Animation[] { 
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, 1), idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, -1), idleDelay, false)
		};
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, 1), runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, -1), runDelay, false),
		};
	}
	
	private void setupAbilities() {
		abilities = new WeaponAbility[1];
		TextureLoader tex = TextureLoader.getInstance();

		// Basic Attack
		int attackDelay = 7;
		Animation[] attackAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerAttack, 1), attackDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerAttack, -1), attackDelay, true),
		};
		abilities[0] = new WeaponAbility(500, 15, attackAnims);
	}
	
	private int getIndexFromDirection() {
		return objectHandler.getPlayer().getDirection() == 1 ? 0 : 1;
	}
	
}
