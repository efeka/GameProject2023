package player_weapons;

import java.awt.Graphics;

import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.Player;
import items.WeaponItem;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public abstract class Weapon extends GameObject {

	protected ObjectHandler objectHandler;
	protected Player player;
	protected KeyInput keyInput;
	protected MouseInput mouseInput;
	
	protected Animation[] idleAnimation = new Animation[2];
	protected Animation[] runAnimation = new Animation[2];
	
	public Weapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(0, 0, 0, 0, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
		player = objectHandler.getPlayer();
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		
		setupAnimations();
	}
	
	public abstract void tick();

	@Override
	public void render(Graphics g) {}
	
	/**
	 * Checks whether an ability of this weapon is being used or not.
	 * @return true if an ability is currently being used.
	 */
	public abstract boolean isUsingAbility();
	
	public abstract Animation getCurrentAnimation();
	
	/**
	 * Initialize the idle and run animations.
	 */
	protected abstract void setupAnimations();
	
	public Animation[] getIdleAnimation() {
		return idleAnimation;
	}
	
	public Animation[] getRunAnimation() {
		return runAnimation;
	}
	
	public abstract WeaponItem createItemFromWeapon(float x, float y);
	
}
