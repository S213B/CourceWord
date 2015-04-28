BasicGame.MainMenu = function (game) {

  this.playButton = null;
  this.text;
  this.img = [];
};

BasicGame.MainMenu.prototype = {

  create: function () {
	
	// background
	this.add.tileSprite(0, 0, 800, 600, 'background');
      
    // add menu buttons
    this.createBtn();
      
    // sound effect
    this.fx = this.game.add.audio('sfx');
    this.fx.addMarker('clickBtn',0,1.0);
      
    // particle system
    this.particleSys();
      
    // backgound music
    this.bgm = this.game.add.audio('bgm_menu');
    this.bgm.play("",0,0.1,true);

	//High score
	this.highScore = this.game.cache.getText('high_score');
      
    // title
    this.title = this.add.text(400, 50, "Obesity", { font: "99px monospace"});
    this.title.anchor.setTo(0.5, 0.5);
    this.title.setShadow(-5, 5, 'rgba(0,0,0,0.5)', 0);
    this.title.stroke = '#ffff00';
    this.title.strokeThickness = 6;
    var grd = this.title.context.createLinearGradient(0, 0, 0, this.title.canvas.height);
    grd.addColorStop(0.16, '#ff0000');
    grd.addColorStop(0.32, '#ffff00');
    grd.addColorStop(0.48, '#00ff00');
    grd.addColorStop(0.64, '#00ffff');
    grd.addColorStop(0.8, '#0000ff');
    grd.addColorStop(1, '#ff00ff');

    //  And apply to the Text
    this.title.fill = grd;
  },
    
  particleSys: function(){
    // particle system
    var emitter = this.game.add.emitter(this.game.world.centerX, 0, 100);

	emitter.width = this.game.world.width;
	// emitter.angle = 30; // uncomment to set an angle for the rain.

	emitter.makeParticles('foods',[76,77,78,79,80,81,82,83,84,85,86,87,88,89,90]);

	emitter.minParticleScale = 0.1;
	emitter.maxParticleScale = 1;

	emitter.setYSpeed(300, 500);
	emitter.setXSpeed(-5, 5);

	emitter.minRotation = 0;
	emitter.maxRotation = 90;

    emitter.setAlpha(0.5, 1);
	emitter.start(false, 1600, 5, 0);
  },
    
  update: function () {
    //  Do some nice funky main menu effect here
  },
    
  createBtn: function(){
      var x = 400;
      var y = 150;
    this.playButton = new LabelButton(this.game, x, y, 'btn', 'Play', this.actionOnClick,this, 1, 0, 2);	
    this.helpButton = new LabelButton(this.game, x, y+100, 'btn', 'Help', this.showHelp,this, 1, 0, 2);
    this.recordButton = new LabelButton(this.game, x, y+200, 'btn', 'Record', this.showRecord,this, 1, 0, 2);
    this.aboutButton = new LabelButton(this.game, x, y+300, 'btn', 'About', this.showAbout,this, 1, 0, 2);
  },

  destroyBtn: function(){
      this.playButton.destroy();
	  //this.highScoreButton.destroy();
      this.helpButton.destroy();
      this.recordButton.destroy();
      this.aboutButton.destroy();
	},

  actionOnClick: function(){
      this.startGame();
      this.fx.play();
  },

  showRecord: function(){
  		this.destroyBtn();
		var style = { font: "25px Georgia", fill: "#000000", align: "center", lineHeight: "30px" };
		this.text = this.game.add.text(400,300, "Top Ten High Score\n"+highScoreRecord[0]+"\n"+highScoreRecord[1]+"\n"+highScoreRecord[2]+"\n"+highScoreRecord[3]+"\n"+highScoreRecord[4]+"\n"+highScoreRecord[5]+"\n"+highScoreRecord[6]+"\n"+highScoreRecord[7]+"\n"+highScoreRecord[8]+"\n"+highScoreRecord[9],style);
		this.text.anchor.setTo(0.5, 0.5);
		this.backButton = new LabelButton(this.game, 400, 500, 'btn', 'Back', this.backTOMenu,this, 1, 0, 2);
		this.fx.play();
  },
    
  showHelp: function(){
	  this.destroyBtn();
      var style = { font: "25px Georgia", fill: "#000000", align: "center" };
      this.text = this.game.add.text(400,300, "Press left/right key to move,\nDouble tap left/right key to dash,\nEat dropping items by touch it,\nEach item will affect the score/energy/fat/muscle,\nWalk, Dash and Stand will consume energy,\nDie if energy is zero,\nFat/muscle ratio will affect walking speed\n and energy consumption rate,\nEat as much as you can!",style);
      this.text.anchor.setTo(0.5, 0.5);
      this.text.lineSpacing = 10;
      this.nextButton = new LabelButton(this.game, 400, 500, 'btn', 'Next', this.showImg,this, 1, 0, 2);
      this.fx.play();
  },

  showAbout: function(){
	  this.destroyBtn();
      var style = { font: "25px Georgia", fill: "#000000", align: "center" };
      this.text = this.game.add.text(400,300, "About: Final Project for Course CSCI 4455\nTitle: Computer Game Design and Programming\nInstructor: Hao Sun\nCreator: Zhisheng Liu & Liqun Xu\nSemester: Fall 2014",style);
      this.text.anchor.setTo(0.5, 0.5);
      this.backButton = new LabelButton(this.game, 400, 500, 'btn', 'Back', this.backTOMenu,this, 1, 0, 2);
      this.fx.play();
  },
    
  showImg: function(){
      this.text.destroy();
      this.destroyBtn();
      
      var x = 100;
      var y = 100;
      this.img.push(this.game.add.image(x, y, 'foods',30));
      this.img.push(this.game.add.image(x+40, y, 'foods',92));
      this.img.push(this.game.add.image(x+80, y, 'foods',85));
      this.img.push(this.game.add.image(x+120, y, 'foods',83));
      this.img.push(this.game.add.image(x, y+40, 'foods',53));
      this.img.push(this.game.add.image(x+40, y+40, 'foods',54));
      this.img.push(this.game.add.image(x+80, y+40, 'foods',55));
      this.img.push(this.game.add.image(x, y+80, 'foods',60));
      this.img.push(this.game.add.image(x, y+120, 'foods',11));
      this.img.push(this.game.add.image(x, y+160, 'foods',84));
      this.img.push(this.game.add.image(x, y+200, 'foods',12));
      this.img.push(this.game.add.image(x, y+240, 'foods',0));
      this.img.push(this.game.add.image(x, y+280, 'foods',13));
      this.img.push(this.game.add.image(x, y+320, 'foods',3));
      this.img.push(this.game.add.image(x, y+360, 'foods',17));
      var style = { font: "25px Georgia", fill: "#000000", align: "left" };
      this.text = this.game.add.text(440,290, "                    +Score +Energy +Fat \n            +Score +Energy +Fat +Muscle\n+Score +Energy +Fat -Muscle\nDeduce fat for a few seconds\nEnlarge body for a few seonds\nTransform fat and muscle into energy\nGame over if ate\nAbsorb all the food and bouns items for a few seconds\nDouble common food score for a few seconds\nDeduce score, energy, fat and muscle for a few seconds",style);
      this.text.anchor.setTo(0.5, 0.5);
      this.text.lineSpacing = 13;
      this.backButton = new LabelButton(this.game, 400, 500, 'btn', 'Back', this.backTOMenu,this, 1, 0, 2);
      this.fx.play();
  },
    
  backTOMenu: function(){
      this.createBtn();
      this.backButton.destroy();
      if(this.nextButton != null){
          this.nextButton.destroy();
      }
      this.text.destroy();
      this.fx.play();
      for(i = 0;i < this.img.length; i++){
        this.img[i].destroy();
    }
  },

  startGame: function () {
    // start the  game
    this.bgm.stop();
	this.destroyBtn();
    this.state.start('Game');
  }

};
