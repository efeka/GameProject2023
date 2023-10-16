package player_weapons;

import java.awt.image.BufferedImage;

import abstracts.Weapon;
import framework.Animation;
import framework.ObjectHandler;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import player_weapons.SwordWeapon.ActionState;
import player_weapons.abilities.WeaponAbility;
import window.KeyInput;
import window.MouseInput;

public class KatanaWeapon extends Weapon {

	enum ActionState {
		None(-1),
		AttackChain1(0),
		AttackChain2(1);

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

	public KatanaWeapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
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
					abilities[ActionState.AttackChain2.getIndex()].resetAnimations();
					break;
				}
				else {
					abilities[state.getIndex()].startCooldown();
					state = ActionState.None;
					break;
				}
			}

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
				if (mouseInput.isAttackButtonPressed()) {
					state = ActionState.AttackChain1;
					abilities[ActionState.AttackChain1.getIndex()].resetAnimations();
					break;
				}
				else {
					abilities[state.getIndex()].startCooldown();
					state = ActionState.None;
					break;
				}
			}

			player.setVelX(0);
			break;
		}
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
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerKatanaIdle, 1), idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerKatanaIdle, -1), idleDelay, false)
		};
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerKatanaRun, 1), runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerKatanaRun, -1), runDelay, false),
		};
		jumpSprites = textureLoader.getTextures(TextureName.PlayerKatanaJump);
	}
	
	private void setupAbilities() {
		abilities = new WeaponAbility[2];
		TextureLoader tex = TextureLoader.getInstance();

		// Combo Chain 1
		int attack1Delay = 7;
		Animation[] attackAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerKatanaAttack1, 1), attack1Delay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerKatanaAttack1, -1), attack1Delay, true),
		};
		abilities[0] = new WeaponAbility(300, 15, attackAnims);

		// Combo Chain 2
		int attack2Delay = 7;
		Animation[] stabAnims = new Animation[] {
				new Animation(tex.getTexturesByDirection(TextureName.PlayerKatanaAttack2, 1), attack2Delay, true),
				new Animation(tex.getTexturesByDirection(TextureName.PlayerKatanaAttack2, -1), attack2Delay, true),
		};
		abilities[1] = new WeaponAbility(1000, 20, stabAnims);
	}
	
	private int getIndexFromDirection() {
		return objectHandler.getPlayer().getDirection() == 1 ? 0 : 1;
	}
	
}
