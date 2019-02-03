package net.ts.isn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Resources {
    private static long loadingStart;
    public static int[][] playersSize;
    public static int[] playerOffsetX;
    public static Vector2[] backgroundTileSourceCoord;
    
    // TODO reconcentrer les images
    public static Texture tiles;
    public static Texture items;
    public static Texture levels;
    public static Texture levelsBackground;
    public static Texture charactersSheet;
    public static Texture projectiles;
    public static Texture heads;
    public static Texture thumbnails;
    public static Texture gui;
	public static Texture arrowkeys;
	public static Texture medal;
	public static Texture editBackground;
	public static Texture spawn;
    public static Texture[] backgrounds;
    
    public static ParticleEffect dustParticle;
    public static ParticleEffect dustDashParticle;
    public static ParticleEffectPool dustParticlePool;
    public static ParticleEffectPool dustDashParticlePool;
    
    public static ShaderProgram[] shaders;
    
    public static BitmapFont font;
    public static BitmapFont titleFont;
    public static BitmapFont buttonFont;
    public static BitmapFont smallButtonFont;
    public static BitmapFont feedbackFont;
    
    public static Sound death;
    public static Sound jump;
    public static Sound dash;
    public static Sound selectionClick;
    public static Sound putBlock;
    public static Sound deleteBlock;
    
    public static Music battletheme;
    
    public static Color dark;

    static {
        playersSize = new int[][]{{34, 87}, {28, 87}};
        playerOffsetX = new int[]{30, 36};
        backgroundTileSourceCoord = new Vector2[]{new Vector2(160, 32), new Vector2(192, 32)};
    }

    public static void init() {
        loadingStart = System.currentTimeMillis();
        tiles = new Texture(Gdx.files.internal("gfx/tiles.png"));
        items = new Texture(Gdx.files.internal("gfx/items.png"));
        charactersSheet = new Texture(Gdx.files.internal("gfx/players.png"));
        projectiles = new Texture(Gdx.files.internal("gfx/projectiles.png"));
        heads = new Texture(Gdx.files.internal("gfx/heads.png"));
        thumbnails = new Texture(Gdx.files.internal("gfx/thumbnails.png"));
        gui = new Texture(Gdx.files.internal("gfx/gui.png"));
        arrowkeys = new Texture(Gdx.files.internal("gfx/arrowkeys.png"));
        medal = new Texture(Gdx.files.internal("gfx/medal.png"));
        editBackground = new Texture(Gdx.files.internal("gfx/editzone.png"));
        spawn = new Texture(Gdx.files.internal("gfx/spawn.png"));
        levels = new Texture(Gdx.files.internal("levels/official_levels.png"));
        levelsBackground = new Texture(Gdx.files.internal("levels/official_levels_background.png"));
        backgrounds = new Texture[1];
        Resources.backgrounds[0] = new Texture(Gdx.files.internal("gfx/background_test.png"));
        
        dustParticle = new ParticleEffect();
        dustParticle.load(Gdx.files.internal("particles/dust_particle.p"), Gdx.files.internal("particles/"));
        dustDashParticle = new ParticleEffect();
        dustDashParticle.load(Gdx.files.internal("particles/test.p"), Gdx.files.internal("particles/"));
        dustParticlePool = new ParticleEffectPool(dustParticle, 0, 10);
        dustDashParticlePool = new ParticleEffectPool(dustDashParticle, 0, 250);
        
        ShaderProgram.pedantic = false;
        shaders = new ShaderProgram[5];
        Resources.shaders[0] = null;
        Resources.shaders[1] = new ShaderProgram(Gdx.files.internal("shaders/retro1.vsh"), Gdx.files.internal("shaders/retro1.fsh"));
        Resources.shaders[2] = new ShaderProgram(Gdx.files.internal("shaders/retro2.vsh"), Gdx.files.internal("shaders/retro2.fsh"));
        Resources.shaders[3] = new ShaderProgram(Gdx.files.internal("shaders/retro3.vsh"), Gdx.files.internal("shaders/retro3.fsh"));
        Resources.shaders[4] = new ShaderProgram(Gdx.files.internal("shaders/screen_shake.vsh"), Gdx.files.internal("shaders/screen_shake.fsh"));
        
        titleFont = new BitmapFont(Gdx.files.internal("fonts/lazer.fnt"), false);
        font = new BitmapFont(Gdx.files.internal("fonts/flipps.fnt"), false);
        buttonFont = new BitmapFont(Gdx.files.internal("fonts/flipps_32.fnt"), false);
        feedbackFont = new BitmapFont(Gdx.files.internal("fonts/feedback_font.fnt"), false);
        font.setColor(Color.BLACK);
        buttonFont.setColor(Color.BLACK);
        
        death = Gdx.audio.newSound(Gdx.files.internal("sounds/death.mp3"));
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.mp3"));
        dash = Gdx.audio.newSound(Gdx.files.internal("sounds/dash.mp3"));
        selectionClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click.mp3"));
        putBlock = Gdx.audio.newSound(Gdx.files.internal("sounds/put_block.mp3"));
        deleteBlock = Gdx.audio.newSound(Gdx.files.internal("sounds/delete_block.mp3"));
        battletheme = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        battletheme.setVolume(0.5f);
        
        System.out.println("Chargement des ressources graphiques et audios : " + (System.currentTimeMillis() - loadingStart) + "ms");
        
        int i = 1;
        
        while (i < shaders.length) {
            System.out.println(shaders[i].getLog());
            ++i;
        }
        
        dark = new Color(0.1f, 0.1f, 0.1f, 0.2f);
    }
    public static void dispose() {
        tiles.dispose();
        items.dispose();
        levels.dispose();
        levelsBackground.dispose();
        charactersSheet.dispose();
        projectiles.dispose();
        heads.dispose();
        thumbnails.dispose();
        gui.dispose();
        arrowkeys.dispose();
        medal.dispose();
        editBackground.dispose();
        spawn.dispose();
        backgrounds[0].dispose();
        dustParticle.dispose();
        dustDashParticle.dispose();
        dustParticlePool.clear();
        dustDashParticlePool.clear();
        shaders[1].dispose();
        shaders[2].dispose();
        shaders[3].dispose();
        titleFont.dispose();
        font.dispose();
        buttonFont.dispose();
        feedbackFont.dispose();
        death.dispose();
        jump.dispose();
        dash.dispose();
        selectionClick.dispose();
        putBlock.dispose();
        deleteBlock.dispose();
        battletheme.dispose();
    }
}