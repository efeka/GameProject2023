package framework;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

public class CreatureAnimationManager {

	public enum AnimationType {
		Idle,
		Run,
		Attack,
		Hurt,
		Death,
		Summon,
	}
	
	private Map<AnimationType, Animation[]> animationMap;
	
	public CreatureAnimationManager() {
		animationMap = new HashMap<>();
	}
	
	public void addAnimation(AnimationType type, Animation[] animations) {
		animationMap.put(type, animations);
	}
	
	public void runAnimation(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			anim.runAnimation();
	}
	
	public void drawAnimation(AnimationType type, Graphics g, int direction, int x, int y, int width, int height) {
		Animation[] animations = animationMap.get(type);
		int directionIndex = direction == 1 ? 0 : 1;
		animations[directionIndex].drawAnimation(g, x, y, width, height);
	}
	
	public void resetAnimation(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			anim.resetAnimation();
	}
	
	public boolean isAnimationPlayedOnce(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			if (anim.isPlayedOnce())
				return true;
		return false;
	}
	
	public int getCurrentAnimationFrame(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		int currentFrame = 0;
		for (Animation anim : animations)
			if (anim.getCurrentFrame() > currentFrame)
				currentFrame = anim.getCurrentFrame();
		return currentFrame;
	}
	
}
