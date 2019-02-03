package net.ts.isn.world;

public class ItemBehavior {
	
	/**
	 * définie les comportements de chaque item lorsqu'il est pris
	 * @param player : le joueur qui a pris l'item
	 * @param item : l'item qui a été pris
	 */
	public static void pickItem(Player player, Item item) {
		// si l'item est ramassable, on le donne au joueur
		if (item.pickable) 
			player.setItem(item);

		// on définie les comportements de chaque item lorsqu'il est pris (s'il ne fait rien il n'apparait pas le switch ci-dessous)
		switch(item) {
			case SPEEDBONUS:
				player.setMaxSpeedX(18);
				break;
			default:
				System.out.println("No item picked up");
				break;
		}
	}
	
	/**
	 * définie le comportement des items "ramassables" quand ils sont utilisés
	 * @param player : le joueur qui utilise l'item
	 * @param item : l'item qui est utilisé
	 */
	public static void useItem(Player player, Item item) {
		if (item.useCount > 0)
			player.addUse();
		
		switch(item) {
			case HADOKEN:
				float projectileXPos = player.getDirection() == 0 ?  player.getPos().x+player.getSize().x+2 : player.getPos().x-77;
				Level.addProjectile(player.getId(), projectileXPos, player.getPos().y+45, 0, 0, 25, 22, 30, player.getDirection() == 0, 500, true);
				break;
			default:
				System.out.println("No item used");
				break;
		}
	}
}
