package net.ts.isn.screens;

import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.screens.gui.Button;
import net.ts.isn.util.Util;

public class ScoreScreen extends RootScreen {
	private int[][] scores; // premier crochet : 0=kills 1=deaths 2=score 3=charID     deuxième crochet : id du joueur
	private int playerNumber; 
	private int winner = 0;
	
	
	private boolean[] winners;
	
	private static Button playAgain;
	private static Button gameEnd;

	public ScoreScreen(Main game) {
		super(game);
		
		this.winners = new boolean[4];
	    this.scores = new int[3][4]; 
	    this.playerNumber = 0;
	    playAgain = new Button ("Play again", 15, 65);
	    gameEnd = new Button ("Back to title screen", 1385, 65);
	}
	
	public void getAndOrganizeScore(int[][] scores, int playerNumber) {
		
		
		this.scores = scores;
		this.playerNumber = playerNumber;
		
		for(int k=0; k<playerNumber; k++) {
			winners[k]= false;
		}
		
		for(int n=0;n<playerNumber; n++){
			if(n==0){
				winner = n;
			}
			else{
				if(scores[2][n]>scores[2][winner]){
					winner = n;
				}
			}
		}
		for(int k=0;k<playerNumber; k++){
			if(scores[2][k]==scores[2][winner]){
				winners[k]= true;
			}
		
		}
	}

	public void update(float delta) {
		super.update(delta);
		if (playAgain.isClicked()){
			((GameScreen)Main.screens.get(2)).create(GameScreen.getMode().setParam(GameScreen.getTimeOrigin()), GameScreen.getLevelID(), playerNumber, GameScreen.getIds());
			this.game.setScreen(Main.screens.get(2));
		}
		if (gameEnd.isClicked()){
			game.setScreen(Main.screens.get(0));;
		}
	}
	
	public void render(float delta) {
		super.render(delta);
		batch.begin();
		
		String kill; 
		String death;
		
		batch.draw(Resources.backgrounds[0], 0, 0, screenWidth, screenHeight);
		Resources.font.draw(batch, "Scores", 870, 1020); 
		
		for(int n=0; n<playerNumber; n++) {
			
			if (scores[0][n]>1)
				kill = " Kills : ";
			else
				kill = " Kill : ";
			if (scores[1][n]>1)
				death = "  Deaths : ";
			else 
				death = "  Death : ";
				
			batch.draw(Resources.heads, n*480+50+((int)(Util.getStringLength(kill + scores[0][n] +    death   + scores[1][n] + "  Score : " + scores[2][n],Resources.font))/2)-14, 540, 54, 60, 27*scores[3][n], 0, 27, 30, false, false);
			Resources.font.draw(batch, kill + scores[0][n] + death + scores[1][n] + "  Score : " + scores[2][n], n*480+50, 490);
		}
		
		playAgain.render(batch);
		gameEnd.render(batch);
		
		for(int n=0; n<playerNumber; n++) {
			String kills; 
			String deaths;
				
			if (scores[0][n]>1)
				kills = " Kills : ";
			else
				kills = " Kill : ";
			if (scores[1][n]>1)
				deaths = "  Deaths : ";
			else
				deaths = "  Death : ";
				
			if (winners[n]==true) {
				batch.draw(Resources.medal,(int)(n*480+50+(Util.getStringLength(kills + scores[0][n] +deaths + scores[1][n] + "  Score : " + scores[2][n],Resources.font))/2 + 60), 600); //mettre icône médaille
			}
		}
		batch.end();
	}
}