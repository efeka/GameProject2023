package player_weapons;

import java.awt.Rectangle;
import java.util.HashSet;

import abstracts.GameObject;
import abstracts.Weapon;
import framework.Animation;
import framework.ObjectHandler;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import window.KeyInput;
import window.MouseInput;

public class FistWeapon extends Weapon {

	enum ActionState {
		None(-1),
		AttackChain1(0),
		AttackChain2(1),
		AttackChain3(2);

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
	private HashSet<GameObject> enemiesHitByAttack1, enemiesHitByAttack2, enemiesHitByAttack3;
	
	public FistWeapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(objectHandler, keyInput, mouseInput);
		enemiesHitByAttack1 = new HashSet<>();
		enemiesHitByAttack2 = new HashSet<>();
		enemiesHitByAttack3 = new HashSet<>();
		setupAbilities();
	}
	
	@Override
	public void tick() {
		Player player = objectHandler.getPlayer();
		switch (state) {
		case None:
			enemiesHitByAttack1.clear();
			enemiesHitByAttack2.clear();
			enemiesHitByAttack3.clear();
			
			currentAnimation = null;
			for (WeaponAbility weaponAbility : abilities) {
				if (!weaponAbility.isOnCooldown())
					weaponAbility.resetAnimations();
			}

			if (mouseInput.isAttackButtonPressed() && !abilities[ActionState.AttackChain1.index].isOnCooldown())
				state = ActionState.AttackChain1;
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

			int currentFrame = currentAnimation.getCurrentFrame();
			if (currentFrame == 1 || currentFrame == 2)
				checkEnemyCollision(getJabAttackBounds(),
						enemiesHitByAttack1,
						abilities[state.getIndex()].getDamage(),
						0, 0, 50);
			player.setVelX(0);
			break;

		case AttackChain2:
			/*
			 * Second stage of the basic attack chain.
			 * The player deals damage to all enemies in a short range..
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				if (mouseInput.isAttackButtonPressed()) {
					state = ActionState.AttackChain3;
					break;
				}
				else {
					abilities[state.getIndex()].startCooldown();
					state = ActionState.None;
					break;
				}
			}

			currentFrame = currentAnimation.getCurrentFrame();
			if (currentFrame == 1 || currentFrame == 2)
				checkEnemyCollision(getJabAttackBounds(),
						enemiesHitByAttack2,
						abilities[state.getIndex()].getDamage(),
						0, 0, 50);
			player.setVelX(0);
			break;
			
		case AttackChain3:
			/*
			 * Third stage of the basic attack chain.
			 * The player deals damage to all enemies in a short range and knocks them back.
			 * Player cannot move while the ability is being cast.
			 */

			currentAnimation = abilities[state.getIndex()].getAnimations()[getIndexFromDirection()];
			if (currentAnimation.isPlayedOnce()) {
				abilities[ActionState.AttackChain1.getIndex()].startCooldown();
				state = ActionState.None;
				break;
			}

			currentFrame = currentAnimation.getCurrentFrame();
			if (currentFrame == 2)
				checkEnemyCollision(getJabAttackBounds(),
						enemiesHitByAttack3,
						abilities[state.getIndex()].getDamage(),
						4 * player.getDirection(), -2f, 50);
			player.setVelX(0);
			break;
		}
	}

	public Rectangle getJabAttackBounds() {
		Player player = objectHandler.getPlayer();
		int px = (int) player.getX();
		int py = (int) player.getY();
		int pWidth = player.getWidth();
		int pHeight = player.getHeight();

		int attackX;
		int attackWidth = (int) (7f * pWidth / 10);
		if (player.getDirection() == 1)
			attackX = px + pWidth / 2;
		else
			attackX = px + pWidth / 2 - attackWidth;
		return new Rectangle(attackX, py, attackWidth, pHeight);
	}
	
	public Rectangle getCrossAttackBounds() {
		Player player = objectHandler.getPlayer();
		int px = (int) player.getX();
		int py = (int) player.getY();
		int pWidth = player.getWidth();
		int pHeight = player.getHeight();

		int attackX;
		int attackWidth = pWidth;
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
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, 1), idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerIdle, -1), idleDelay, false)
		};
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, 1), runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerRun, -1), runDelay, false),
		};
		jumpSprites = textureLoader.getTextures(TextureName.PlayerJump);
	}
	
	private void setupAbilities() {
		abilities = new WeaponAbility[3];
		TextureLoader tex = TextureLoader.getInstance();

		// Attack Chain 1
		int firstJabDelay = 6;
		Animation[] jabAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchJab, 1), firstJabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchJab, -1), firstJabDelay, true),
		};
		abilities[0] = new WeaponAbility(150, 7, jabAnims);
		
		// Attack Chain 2
		int secondJabDelay = 2;
		Animation[] jabAnims2 = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchJab, 1), secondJabDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchJab, -1), secondJabDelay, true),
		};
		abilities[1] = new WeaponAbility(150, 7, jabAnims2);
		
		// Attack Chain 3
		int crossDelay = 6;
		Animation[] crossAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchCross, 1), crossDelay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerPunchCross, -1), crossDelay, true),
		};
		abilities[2] = new WeaponAbility(500, 15, crossAnims);
	}
	
	private int getIndexFromDirection() {
		return objectHandler.getPlayer().getDirection() == 1 ? 0 : 1;
	}
	
}
