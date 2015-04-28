BasicGame.Preloader = function (game) {
  this.background = null;
  this.preloadBar = null;
};

BasicGame.Preloader.prototype = {

  preload: function () {

    // show background in preloading state
    this.add.tileSprite(0, 0, 800, 600, 'background');
    
    //  Show the loading progress bar asset we loaded in boot.js
    this.preloadBar = this.add.sprite(400, 300, 'preloaderBar');
    this.preloadBar.anchor.setTo(0.5, 0.5);
    this.add.text(400, 250, "Loading...", { font: "32px monospace", fill: "#fff" }).anchor.setTo(0.5, 0.5);

    //  This sets the preloadBar sprite as a loader sprite.
    //  What that does is automatically crop the sprite from 0 to full-width
    //  as the files below are loaded in.
    this.load.setPreloadSprite(this.preloadBar);

    //  Here we load the rest of the assets our game needs...
    this.load.spritesheet('boy', 'assets/img/boy.png', 112, 114);
    this.load.spritesheet('foods', 'assets/img/foods.png', 33, 32,98,2);
    this.load.spritesheet('btn', 'assets/img/btn.png', 190, 49);   
    this.load.image('cloud1', 'assets/img/cloud1.png');
    this.load.image('cloud2', 'assets/img/cloud2.png');
    this.load.image('ground', 'assets/img/ground.png');
     
    this.load.audio('sfx','assets/aud/aud1.ogg');
    this.load.audio('sfx_food','assets/aud/aud2.ogg');
    this.load.audio('bgm_menu','assets/aud/menu.ogg');
    this.load.audio('bgm_game','assets/aud/game.ogg');
    this.load.audio('bgm_end','assets/aud/end.ogg');
    
      
	this.load.text('high_score', 'assets/highScore.txt');
      
  },

  create: function () {
    // add background again
    this.add.tileSprite(0, 0, 800, 600, 'background');
      
    this.particleSys();
      
    //the "click to restart" handler
    this.title = this.add.text(400, 300, "Obesity", { font: "99px monospace"});
    this.title.anchor.setTo(0.5, 0.5);
    this.title.setShadow(-5, 5, 'rgba(0,0,0,0.5)', 0);
    this.title.stroke = '#ffff00';
    this.title.strokeThickness = 6;
    var grd = this.title.context.createLinearGradient(0, 0, 0, this.title.canvas.height);
    //  Add in 2 color stops
    grd.addColorStop(0.16, '#ff0000');
    grd.addColorStop(0.32, '#ffff00');
    grd.addColorStop(0.48, '#00ff00');
    grd.addColorStop(0.64, '#00ffff');
    grd.addColorStop(0.8, '#0000ff');
    grd.addColorStop(1, '#ff00ff');

    //  And apply to the Text
    this.title.fill = grd;
      
    this.text1 = this.add.text(400, 600, "Click to Start!", { font: "32px Impact", fill: "#000000" });
    this.text1.anchor.setTo(0.5, 1);
    this.game.time.events.loop(Phaser.Timer.SECOND * 0.1, this.textEffect, this);
      
    this.fx = this.game.add.audio('sfx');
    this.fx.addMarker('clickBtn',0,1.0);
   	this.fx.play();
      
    this.game.input.onTap.addOnce(this.start,this); // click to start
  },
    
  textEffect: function(){
      if(this.text1.alpha > 0){
          this.text1.alpha -= 0.1;
      }
      else{
          this.text1.alpha = 1;
      }  
  },

  particleSys: function(){
    // particle system
    var emitter = this.game.add.emitter(this.game.world.centerX, 0, 400);

	emitter.width = this.game.world.width;
	// emitter.angle = 30; // uncomment to set an angle for the rain.

	emitter.makeParticles('foods',[76,77,78,79,80,81,82,83,84,85,86,87,88,89,90]);

	emitter.minParticleScale = 0.1;
	emitter.maxParticleScale = 2;

	emitter.setYSpeed(300, 500);
	emitter.setXSpeed(-5, 5);

	emitter.minRotation = 0;
	emitter.maxRotation = 0;

    emitter.setAlpha(0.5, 1);
	emitter.start(false, 1600, 5, 0);
  },
    
  start: function() {
    this.fx1 = this.game.add.audio('sfx_food');
    this.fx1.addMarker('click',0,1.0);
   	this.fx1.play();
    this.state.start('MainMenu');
    // for development purpose shortcut to game state
    //this.state.start('Game');
  }
};
